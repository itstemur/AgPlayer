package com.agadimi.agplayer.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.app.App;
import com.agadimi.agplayer.common.FileManager;
import com.agadimi.agplayer.databinding.ActivityMainBinding;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.ui.fragments.FileListFragment;

import javax.inject.Inject;

import timber.log.Timber;

public class HomeActivity extends AppCompatActivity implements FileManager.FileListener
{
    private ActivityMainBinding binding;
    @Inject
    FileManager fileManager;

    FileListFragment fileListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //inject
        ((App) getApplication()).appComponent.inject(this);

        //setup activity view
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup fragments
        fileListFragment = new FileListFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_file_list, fileListFragment).commit();

        // init file manager
        fileManager.setFileListener(this);
        fileManager.scanFiles();
    }

    @Override
    public void onBackPressed()
    {
        if (!fileListFragment.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onFilesListUpdated(FolderFile[] files)
    {
        fileListFragment.setFiles(files);
    }
}