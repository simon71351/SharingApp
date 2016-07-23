package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
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

import java.io.File;
import java.util.ArrayList;

/**
 * Created by simon on 6/30/16.
 */
public class ImageGalleryFragment extends Fragment{

    private Cursor cursor;
    private int columnIndex;
    private ArrayList<ImageModel> modelList;
    private ArrayList<Integer> selectedPos;
    private ImageAdapter adapter;
    //private ImageCursorAdapter adapter;
    GridView gridView;

    class ImageModel{
        private boolean checked;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        modelList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_gridlayout, container, false);



        try {


            //Retriving Images from Database(SD CARD) by Cursor.
            final String[] list = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, list, null, null, MediaStore.Images.Media._ID);

            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            int numPhotos = cursor.getCount();

            for (int i = 0; i < numPhotos; i++) {
                modelList.add(new ImageModel());
            }

//            gridView = (GridView) view.findViewById(R.id.gridview);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerView.setAdapter(new PhotoAdapter());
            //adapter = new ImageAdapter(getContext());

            //adapter = new ImageCursorAdapter(getActivity());

            //gridView.setAdapter(adapter);

            Log.e("Cycle", "View gets successfully created");

//            recyclerView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView absListView, int i) {
//                    int firstVisibleRow = gridView.getFirstVisiblePosition();
//                    int lastVisibleRow = gridView.getLastVisiblePosition();
//                    Log.e("Scroll", "FirstVisibleRow: "+firstVisibleRow+" "+"LastVisibleRow: "+lastVisibleRow);
//                }
//
//                @Override
//                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//
//                }
//            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    int firstVisibleRow = gridView.getFirstVisiblePosition();
                    int lastVisibleRow = gridView.getLastVisiblePosition();
                    Log.e("Scroll", "FirstVisibleRow: "+firstVisibleRow+" "+"LastVisibleRow: "+lastVisibleRow);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });


/*        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/

            recyclerView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {


                @Override
                public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int i, long l, boolean b) {
                    int selectCount = gridView.getCheckedItemCount();
                    switch (selectCount) {
                        case 1:
                            actionMode.setSubtitle("One item selected");
                            break;
                        default:
                            actionMode.setSubtitle("" + selectCount + " items selected");
                            break;
                    }


                    int pos = i - gridView.getFirstVisiblePosition();
                    try {
                        MarkableImageView imageView = (MarkableImageView) gridView.getChildAt(pos);
                        if (modelList.get(i).isChecked()) {
                            imageView.setChecked(false);
                            modelList.get(i).setChecked(false);
                            selectedPos.remove(selectedPos.lastIndexOf(i));
                        } else {
                            imageView.setChecked(true);
                            modelList.get(i).setChecked(true);
                            selectedPos.add(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                    actionMode.setTitle("Select Items");
                    actionMode.setSubtitle("One item selected");
                    selectedPos = new ArrayList<Integer>();
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(android.view.ActionMode actionMode) {
                    adapter.removeSelection(selectedPos);
                }
            });


            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MarkableImageView imageView = (MarkableImageView) view;

                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToPosition(i);
                    String filename = cursor.getString(columnIndex);

//                if(modelList.get(i).isChecked()){
//                    imageView.setChecked(false);
//                    modelList.get(i).setChecked(false);
//                }
//                else{
//                    imageView.setChecked(true);
//                    modelList.get(i).setChecked(true);
//                }
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    File file = new File(filename);
                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                    startActivity(intent);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        return view;
    }

//    class ImageLoaderTask extends AsyncTask<Void, Void, Cursor>{
//        @Override
//        protected void onPostExecute(Cursor cursor) {
//            Log.e("Cursor", "Cursor gets swapped");
//            adapter.swapCursor(cursor);
//        }
//
//        @Override
//        protected Cursor doInBackground(Void... voids) {
//            //Searching Images ID's from Gallery. _ID is the Default id code for all. You can retrive image,contacts,music id in the same way.
//
//        }
//    }

//    private class ImageCursorAdapter extends CursorAdapter{
//
//        public ImageCursorAdapter(Context context){
//            super(context, null, false);
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            MarkableImageView imageView = new MarkableImageView(getContext());
//            imageView.setLayoutParams(new GridView.LayoutParams(200, 220));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(5,5,5,5);
//
//            return imageView;
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            int imageID = cursor.getInt(columnIndex);
//
//            //In Uri "" + imageID is to convert int into String as it only take String Parameter and imageID is in Integer format.
//            //You can use String.valueOf(imageID) instead.
//            Uri uri = Uri.withAppendedPath(
//                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);
//
//            //Setting Image to View Holder Image View.
//
//            ((MarkableImageView)view).setImageURI(uri);
//            int pos = gridView.getPositionForView(view);
//            Log.e("ImageView", "ImageView gets selected");
//            ((MarkableImageView)view).setChecked(modelList.get(pos).isChecked());
//        }
//
//        public void removeSelection(ArrayList<Integer> selectionList){
//            for(int i = 0; i < selectionList.size(); i++){
//                modelList.get(selectionList.get(i).intValue()).setChecked(false);
//                Log.e("SelectionList", "SelectionList: "+selectionList.get(i).intValue());
//            }
//            selectionList.clear();
//            notifyDataSetChanged();
//        }
//    }

    // Adapter for Grid View
    private class ImageAdapter extends BaseAdapter {

        private Context context;


        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
            return  cursor.getCount();

        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {

            return position;

        }

        public void removeSelection(ArrayList<Integer> selectionList){
            for(int i = 0; i < selectionList.size(); i++){
                modelList.get(selectionList.get(i).intValue()).setChecked(false);
                Log.e("SelectionList", "SelectionList: "+selectionList.get(i).intValue());
            }
            selectionList.clear();
            notifyDataSetChanged();
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

            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

            //In Uri "" + imageID is to convert int into String as it only take String Parameter and imageID is in Integer format.
            //You can use String.valueOf(imageID) instead.
            Uri uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageID);

            //Setting Image to View Holder Image View.

//            imageView.setImageURI(uri);
//            imageView.setChecked(modelList.get(position).isChecked());

            new LoadImage(imageView, imagePath, position).execute();

            //this is some comment


            return imageView;

        }

        // View Holder pattern used for Smooth Scrolling. As View Holder pattern recycle the findViewById() object.

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
                        getResources(), R.drawable.check_circle);
                int width = check.getWidth();
                int height = check.getHeight();
                int margin = 15;
                int x = canvas.getWidth() - width ;
                int y = canvas.getHeight() - height;
                canvas.drawBitmap(check, x, y, new Paint());
            }
        }
    }

    class LoadImage extends AsyncTask<Object, Void, Bitmap>{
        ImageView imageView;
        String imagePath;
        int pos;

        LoadImage(ImageView imageView, String imagePath, int pos){
            this.imageView = imageView;
            this.imagePath = imagePath;
            this.pos = pos;
        }

        @Override
        protected Bitmap doInBackground(Object... objects) {
            Log.e("Path", "Path: "+imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            return Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            ((MarkableImageView)imageView).setChecked(modelList.get(pos).isChecked());


        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public PhotoHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.gallery_image_view);
        }

        public void bindDrawable(Drawable drawable){
            imageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MarkableImageView imageView;

            imageView = new MarkableImageView(getContext());
            imageView.setLayoutParams(new GridView.LayoutParams(200, 220));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5,5,5,5);

            return new PhotoHolder(imageView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            cursor.moveToPosition(position);
            int imageID = cursor.getInt(columnIndex);

            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

            //In Uri "" + imageID is to convert int into String as it only take String Parameter and imageID is in Integer format.
            //You can use String.valueOf(imageID) instead.
            Uri uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageID);

            //Setting Image to View Holder Image View.

//            imageView.setImageURI(uri);
//            imageView.setChecked(modelList.get(position).isChecked());

            new LoadImage(holder.imageView, imagePath, position).execute();

//            holder.bindDrawable();
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        private int focusedItem = 0;

        @Override
        public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

            // Handle key up and key down and attempt to move selection
            recyclerView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

                    // Return false if scrolled to the bounds and allow focus to move off the list
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            return tryMoveSelection(lm, 1);
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            return tryMoveSelection(lm, -1);
                        }
                    }

                    return false;
                }
            });

            recyclerView.setON
        }
    }
}
