package com.rmcomicsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rmcomicsreader.model.DirectoryChooserDialog;

import java.io.File;

/**
 * Created by Ромочка on 11.05.2015.
 */
public class SettingsActivity extends Activity {
    private String m_chosenDir = "";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        textView = (TextView) findViewById(R.id.textView);

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
        startActivity(intent);
    }

    public void setRefresh(View view) {
        textView.setText(m_chosenDir);
    }
}
