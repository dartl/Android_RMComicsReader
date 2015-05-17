package com.rmcomicsreader;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FilenameFilter;


public class MainActivity extends ActionBarActivity {
    MyWebView imageViewComics = null;
    FrameLayout generalPane = null;
    private int currentPage = -1;           // Номер текущего изображения
    private String[] jpgList;              // Массив изображений
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generalPane = (FrameLayout)findViewById(R.id.generalPane);
        imageViewComics = new MyWebView(this);
        generalPane.addView(imageViewComics);
        imageViewComics.loadData("<style>img{display: inline;height: auto;max-width: 100%;}</style>", "UTF-8", null);
        imageViewComics.getSettings().setLoadWithOverviewMode(true);
        imageViewComics.setPadding(0, 0, 0, 0);
        imageViewComics.setScrollbarFadingEnabled(true);
        imageViewComics.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    class MyWebView extends WebView {
        Context context;
        GestureDetector gd;
        ScaleGestureDetector sgd;

        public MyWebView(Context context) {
            super(context);
            this.context = context;
            gd = new GestureDetector(context, sogl);
            sgd = new ScaleGestureDetector(context,sosgl);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return sgd.onTouchEvent(event) && gd.onTouchEvent(event);
        }

        ScaleGestureDetector.SimpleOnScaleGestureListener sosgl = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            private float scaleFactor = 1;
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor); // prevent our view from becoming too small //
                scaleFactor = ((float)((int)(scaleFactor * 100))) / 100; // Change precision to help with jitter when user just rests their fingers //
                imageViewComics.setScaleX(scaleFactor);
                imageViewComics.setScaleY(scaleFactor);
                return true;
            }

            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        };
        GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent event) {
                return true;
            }

            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                flingScroll(-(int) velocityX, -(int) velocityY);

                if (event1.getRawX() > event2.getRawX()) {
                    if (!canScrollHorizontally(1)) {
                        nextPage(imageViewComics);
                    }
                } else {
                    if (!canScrollHorizontally(-1)) {
                        prevPage(imageViewComics);
                    }
                }
                return true;
            }

        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.equals(Configuration.ORIENTATION_LANDSCAPE)){
        }if(newConfig.equals(Configuration.ORIENTATION_PORTRAIT)){
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.getStringExtra("path") != null) {
            String fpath = intent.getStringExtra("path");
            currentPage = intent.getIntExtra("cur", 0);
            selectComics(fpath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void setBookMark(View view) {
    }

    public void selectComics(String path) {
        File currentDir = new File(path);
        PageTypeFilter filter = new PageTypeFilter();
        jpgList = currentDir.list(filter);
        if (jpgList.length == 0) { return; }
        if (currentPage == -1) {currentPage = 0;}
        this.path = path;
        showPage();
    }

    private void showPage() {
        if (jpgList.length == 0) { return; }
        String data = "<div style=\"text-align:center; weight:100%; height:100%;\">" +
                    "<img src=\"" + jpgList[currentPage] + "\"/></div>";
        imageViewComics.loadDataWithBaseURL("file:///" + path + "/", data, "text/html", "utf-8", null);
    }

    public void nextPage(View view) {
            if (currentPage >= 0 && currentPage < jpgList.length - 1) {
                currentPage++;
                showPage();
            }
    }

    public void prevPage(View view) {
        if (currentPage > 0 && currentPage <= jpgList.length) {
            currentPage--;
            showPage();
        }
    }

    class PageTypeFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".jpg") || name.endsWith(".png"));
        }
    }

}
