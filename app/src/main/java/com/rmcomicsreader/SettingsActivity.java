package com.rmcomicsreader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmcomicsreader.model.DirectoryChooserDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Ромочка on 11.05.2015.
 */
public class SettingsActivity extends Activity {
    private String m_chosenDir = "";
    private ListView listView = null;
    private ArrayList<File> listFileComics = new ArrayList<File>();
    private int currentComics = 0;
    private int currentPage = 0;
    private SharedPreferences sPref;
    private TextView textView;
    private String[] jpgList;              // Массив изображений
    private EditText editText;
    private int oldCurrentPage = 0;

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        if(!loadHomeDir().equals("null")) {
            m_chosenDir = loadHomeDir();
            parseDirHome();
            refreshList();
        }
        // получаем экземпляр элемента ListView
        listView = (ListView)findViewById(R.id.listViewComics);
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                currentComics = position;
                selectComics(listFileComics.get(position).getPath());
                textView.setText(" / " + jpgList.length);
                editText.setText(String.valueOf(currentPage+1));
            }
        });

        Button dirChooserButton = (Button) findViewById(R.id.dirHome);
        dirChooserButton.setOnClickListener(new View.OnClickListener() {
            private boolean m_newFolderEnabled = true;

            @Override
            public void onClick(View v) {
                // Create DirectoryChooserDialog and register a callback
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(SettingsActivity.this,
                                new DirectoryChooserDialog.ChosenDirectoryListener() {
                                    @Override
                                    public void onChosenDir(String chosenDir) {
                                        m_chosenDir = chosenDir;
                                        saveText(m_chosenDir);
                                        Toast.makeText(
                                                SettingsActivity.this, "Chosen directory: " +
                                                        chosenDir, Toast.LENGTH_LONG).show();
                                        parseDirHome();
                                        refreshList();
                                    }
                                });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory("");
                m_newFolderEnabled = !m_newFolderEnabled;
            }
        });
    }


    public void onClickToView(View view) {
        try {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            if (!listFileComics.isEmpty()) {
                intent.putExtra("path", listFileComics.get(currentComics).getPath());
                intent.putExtra("cur", currentPage);
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRefresh(View view) {
        parseDirHome();
        refreshList();
    }

    private void parseDirHome() {
        try {
            File homeDir = new File(m_chosenDir);
            File[] filesList = homeDir.listFiles();
            int count = 0;
            listFileComics = new ArrayList<File>();
            for (int i = 0; i < filesList.length; i++) {
                if (filesList[i].isDirectory()) {
                    listFileComics.add(count++,filesList[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshList() {
        try {
            String[] tempList = new String[listFileComics.size()];
            for (int i = 0; i < listFileComics.size(); i++) {
                tempList[i] = listFileComics.get(i).getName();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,	android.R.layout.simple_list_item_1, tempList);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveText(String text) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("homeDir", text);
        ed.commit();
    }

    String loadHomeDir() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString("homeDir", "");
        return savedText;
    }

    public void selectComics(String path) {
        File currentDir = new File(path);
        PageTypeFilter filter = new PageTypeFilter();
        jpgList = currentDir.list(filter);
        if (jpgList.length == 0) { return; }
    }

    public void moveToPage(View view) {
        try {
            int t = new Integer(editText.getText().toString());
            if (t > 0 && t <= jpgList.length) {
                currentPage = t - 1;
                editText.setText(String.valueOf(currentPage + 1));
            } else {
                currentPage = oldCurrentPage;
                editText.setText(String.valueOf(currentPage + 1));
            }
        } catch (Exception e) {

        }

    }

    public void setOldCurrent(View view) {
        oldCurrentPage = currentPage;
    }

    class PageTypeFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".jpg") || name.endsWith(".png"));
        }
    }
}