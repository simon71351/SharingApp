package com.bignerdranch.android.newprojectdemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    //Declaring All The Variables Needed

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Assigning view variables to thier respective view in xml
        by findViewByID method
         */

//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        /*
        Creating Adapter and setting that adapter to the viewPager
        setSupportActionBar method takes the toolbar and sets it as
        the default action bar thus making the toolbar work like a normal
        action bar.
         */
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        //setSupportActionBar(toolbar);

        /*
        TabLayout.newTab() method creates a tab view, Now a Tab view is not the view
        which is below the tabs, its the tab itself.
         */

        final TabLayout.Tab image = tabLayout.newTab();
        final TabLayout.Tab music = tabLayout.newTab();
        final TabLayout.Tab video = tabLayout.newTab();

        /*
        Setting Title text for our tabs respectively
         */

        //home.setText("Home");
        image.setIcon(R.drawable.ic_image);
        music.setIcon(R.drawable.ic_music_dark);
        video.setIcon(R.drawable.ic_video_dark);

        /*
        Adding the tab view to our tablayout at appropriate positions
        As I want home at first position I am passing home and 0 as argument to
        the tablayout and like wise for other tabs as well
         */
        tabLayout.addTab(image, 0);
        tabLayout.addTab(music, 1);
        tabLayout.addTab(video, 2);

        /*
        TabTextColor sets the color for the title of the tabs, passing a ColorStateList here makes
        tab change colors in different situations such as selected, active, inactive etc

        TabIndicatorColor sets the color for the indiactor below the tabs
         */

        tabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));


        /*
        Adding a onPageChangeListener to the viewPager
        1st we add the PageChangeListener and pass a TabLayoutPageChangeListener so that Tabs Selection
        changes when a viewpager page changes.
         */

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TabPos", "TabPosition: "+tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        /*
                        setting Home as White and rest grey
                        and like wise for all other positions
                         */
                        image.setIcon(R.drawable.ic_image);
                        music.setIcon(R.drawable.ic_music_dark);
                        video.setIcon(R.drawable.ic_video_dark);
                        break;
                    case 1:
                        image.setIcon(R.drawable.ic_image_dark);
                        music.setIcon(R.drawable.ic_music);
                        video.setIcon(R.drawable.ic_video_dark);
                        break;
                    case 2:
                        image.setIcon(R.drawable.ic_image_dark);
                        music.setIcon(R.drawable.ic_music_dark);
                        video.setIcon(R.drawable.ic_video);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }


}

