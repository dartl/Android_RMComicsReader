package com.rmcomicsreader;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {
    WebView imageViewComics = null;
    private int currentPage = -1;           // Номер текущего изображения
    private String[] jpgList;              // Массив изображений
    private String path;

    float width;
    float height;
    float currentHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewComics = (WebView)findViewById(R.id.webView);
        imageViewComics.loadData("<style>img{display: inline;height: auto;max-width: 100%;}</style>", "UTF-8", null);
        imageViewComics.getSettings().setLoadWithOverviewMode(true);
        imageViewComics.getSettings().setUseWideViewPort(true);
        imageViewComics.setInitialScale(1);
        imageViewComics.getSettings().setBuiltInZoomControls(true);
        imageViewComics.getSettings().setSupportZoom(true);
        imageViewComics.getSettings().setDisplayZoomControls(false);
        imageViewComics.setPadding(0, 0, 0, 0);
        imageViewComics.setScrollbarFadingEnabled(true);
        imageViewComics.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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
        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path + jpgList[currentPage], options);
        int width = options.outWidth;
        int height = options.outHeight;*/
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
