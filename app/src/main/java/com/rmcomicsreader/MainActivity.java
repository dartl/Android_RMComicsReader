package com.rmcomicsreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Environment;
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
import android.widget.GridLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;


public class MainActivity extends ActionBarActivity {
    private WebView imageViewComics = null;
    private FrameLayout generalPane = null;
    private int currentPage = -1;           // Номер текущего изображения
    private String[] jpgList;               // Массив изображений
    private String path;
    private String data;
    private boolean chekHorizontal = true;
    private boolean chekZoom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generalPane = (FrameLayout)findViewById(R.id.generalPane);
        imageViewComics = new MyWebView(this);
        generalPane.addView(imageViewComics);
        imageViewComics.setPadding(0, 0, 0, 0);
        imageViewComics.setLayerType(View.LAYER_TYPE_NONE, null);
        imageViewComics.setBackgroundColor(00000000);
        imageViewComics.getSettings().setUseWideViewPort(true);
        imageViewComics.setInitialScale(1);
        generalPane.setPadding(0,0,0,0);
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
            public boolean onScale(ScaleGestureDetector detector) {
                if (chekZoom) {
                    if (detector.getScaleFactor() > 1) {
                        imageViewComics.zoomIn();
                    } else {
                        imageViewComics.zoomOut();
                    }
                }
                return true;
            }

            public boolean onScaleBegin(ScaleGestureDetector detector) {
                chekZoom = true;
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector detector) {
                chekZoom = false;
            }
        };
        GestureDetector.SimpleOnGestureListener sogl = new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent event) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
                if (!chekZoom && canScrollHorizontally(1) && canScrollHorizontally(-1)) {
                    if (((currentPage == jpgList.length) && !(event1.getRawX() < event2.getRawX())) ||
                            ((currentPage == 0) && (event1.getRawX() < event2.getRawX())) || !canScrollHorizontally(1) || !canScrollHorizontally(-1)) {
                    } else {
                        imageViewComics.scrollBy((int) distanceX, (int) distanceY);
                    }
                    if (!canScrollHorizontally(1)) {
                        if (event2.getRawX() > event1.getRawX()) {
                            imageViewComics.scrollBy((int) distanceX, (int) distanceY);
                        }
                    }
                    if (!canScrollHorizontally(-1)) {
                        if (event2.getRawX() < event1.getRawX()) {
                            imageViewComics.scrollBy((int) distanceX, (int) distanceY);
                        }
                    }
                }
                return super.onScroll(event1, event2, distanceX, distanceY);
            }

            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                if (!chekZoom) {
                    flingScroll((int)-velocityX,(int)-velocityY);
                    if (Math.abs(event2.getRawX() - event1.getRawX()) > Math.abs(event2.getRawY() - event1.getRawY()) && Math.abs(event2.getRawX() - event1.getRawX()) > 300) {
                        if (event1.getRawX() > event2.getRawX()) {
                            if (!canScrollHorizontally(1)) {
                                nextPage(imageViewComics);
                            }
                        } else {
                            if (!canScrollHorizontally(-1)) {
                                prevPage(imageViewComics);
                            }
                        }
                    }
                }
                return true;
            }

        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Проверяем ориентацию экрана
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            chekHorizontal = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            chekHorizontal = true;
        }
        showPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.getStringExtra("path") != null) {
            String fpath = intent.getStringExtra("path");
            currentPage = intent.getIntExtra("cur", 0);
            selectComics(fpath);
            if (!getScreenOrientation()) {
                chekHorizontal = false;
            }
            showPage();
        }
    }

    private boolean getScreenOrientation(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return true;
        else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        if (currentPage >= 0) {
            intent.putExtra("path", path);
            intent.putExtra("cur", currentPage);
        }
        startActivity(intent);
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
        if (jpgList != null) {
            if (jpgList.length == 0) { return; }
            data = "<html><head><style type='text/css'>body{margin:auto auto;text-align:center;}</style></head><body>" +
                    "<table style=\"width:100%;height:100%;vertical-align:middle;text-align:center\">\n" +
                    "  <tr>\n" +
                    "    <td><img style=\"padding:0;margin:0;\" src=\"" + jpgList[currentPage] + "\"/></td>\n" +
                    "  </tr>\n" +
                    "</table></body></html>";
            imageViewComics.loadDataWithBaseURL("file:///" + path + "/", data, "text/html", "utf-8", null);
        }
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

    /*private void setImageSize() {
        try {
            // Конвертируем Drawable в Bitmap
            String pat = path + "/" + jpgList[currentPage];
            File file = new File(pat);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            int mPhotoWidth = bitmap.getWidth();
            int mPhotoHeight = bitmap.getHeight();
            if (chekHorizontal) {
                if (mPhotoWidth > mPhotoHeight) {
                    data = "<img style=\"padding:0;margin:0;height:100%;width:auto\" src=\"" + jpgList[currentPage] + "\"/>";
                } else {
                    data = "<img style=\"padding:0;margin:0;height:auto;width:100%\" src=\"" + jpgList[currentPage] + "\"/>";
                }
            } else {
                data = "<img style=\"padding:0;margin:0;height:auto;width:100%\" src=\"" + jpgList[currentPage] + "\"/>";
            }
        } catch (Exception e) {

        }
    }*/
}
