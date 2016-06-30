package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by simon on 6/30/16.
 */
public class ImageGalleryFragment extends Fragment{

    private Cursor cursor;
    private int columnIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_gridlayout, container, false);

        //Searching Images ID's from Gallery. _ID is the Default id code for all. You can retrive image,contacts,music id in the same way.
        String[] list = {MediaStore.Images.Media._ID};

        //Retriving Images from Database(SD CARD) by Cursor.
        cursor = getActivity().getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, list, null, null, MediaStore.Images.Thumbnails._ID);
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

        final GridView gridView = (GridView) view.findViewById(R.id.gridview);
        final ImageAdapter adapter=new ImageAdapter(getContext());
        gridView.setAdapter(adapter);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

/*        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("Select Items");
                mode.setSubtitle("One item selected");
                return true;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return true;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                  boolean checked) {
                int selectCount = gridView.getCheckedItemCount();
                switch (selectCount) {
                    case 1:
                        mode.setSubtitle("One item selected");
                        break;
                    default:
                        mode.setSubtitle("" + selectCount + " items selected");
                        break;
                }

                ImageAdapter.MarkableImageView imageView = (ImageAdapter.MarkableImageView) gridView.getChildAt(position);
                imageView.setChecked(checked);

            }
        });



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageAdapter.MarkableImageView imageView = (ImageAdapter.MarkableImageView) view;
                if(imageView.isChecked()) imageView.setChecked(false);
                else imageView.setChecked(true);
            }
        });

        return view;
    }

    // Adapter for Grid View
    private class ImageAdapter extends BaseAdapter {

        private Context context;


        public ImageAdapter(Context localContext) {

            context = localContext;

        }

        public int getCount() {

            return cursor.getCount();

        }

        public Object getItem(int position) {

            return position;

        }

        public long getItemId(int position) {

            return position;

        }


        public View getView(int position, View convertView, ViewGroup parent) {
            MarkableImageView imageView;


            if (convertView == null) {
                imageView = new MarkableImageView(getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(200, 220));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5,5,5,5);

            } else {
                imageView = (MarkableImageView) convertView;
            }
            cursor.moveToPosition(position);
            int imageID = cursor.getInt(columnIndex);

            //In Uri "" + imageID is to convert int into String as it only take String Parameter and imageID is in Integer format.
            //You can use String.valueOf(imageID) instead.
            Uri uri = Uri.withAppendedPath(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);

            //Setting Image to View Holder Image View.

            imageView.setImageURI(uri);

            //this is some comment


            return imageView;

        }
        // View Holder pattern used for Smooth Scrolling. As View Holder pattern recycle the findViewById() object.
        class ViewHolder {
            private MarkableImageView picturesView;
        }

        class MarkableImageView extends ImageView{
            private boolean checked = false;

            public MarkableImageView(Context context) {
                super(context);
            }

            public MarkableImageView(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public MarkableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
            }

            public void setChecked(boolean checked){
                this.checked = checked;
                invalidate();
            }

            public boolean isChecked() {
                return checked;
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if(checked) {
                    Bitmap check = BitmapFactory.decodeResource(
                            getResources(), R.drawable.check_custom);
                    int width = check.getWidth();
                    int height = check.getHeight();
                    int margin = 15;
                    int x = canvas.getWidth() - width ;
                    int y = canvas.getHeight() - height;
                    canvas.drawBitmap(check, x, y, new Paint());
                }
            }
        }
    }
}
