package com.rmcomicsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmcomicsreader.model.DirectoryChooserDialog;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ромочка on 11.05.2015.
 */
public class SettingsActivity extends Activity {
    private String m_chosenDir = "";
    private ListView listView = null;
    private ArrayList<File> listFileComics = new ArrayList<File>();
    private int currentComics = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);


        // получаем экземпляр элемента ListView
        listView = (ListView)findViewById(R.id.listViewComics);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                currentComics = position;
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
                                        Toast.makeText(
                                                SettingsActivity.this, "Chosen directory: " +
                                                        chosenDir, Toast.LENGTH_LONG).show();
                                    }
                                });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory(m_chosenDir);
                m_newFolderEnabled = !m_newFolderEnabled;
            }
        });
    }


    public void onClickToView(View view) {
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        intent.putExtra("path",listFileComics.get(0).getPath());
        startActivity(intent);
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
}