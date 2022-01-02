package com.agadimi.agplayer.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.app.App;
import com.agadimi.agplayer.common.FileManager;
import com.agadimi.agplayer.databinding.ActivityMainBinding;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.ui.dialogs.YesNoDialog;
import com.agadimi.agplayer.ui.fragments.FileListFragment;

import javax.inject.Inject;

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

        fileManager.setFileListener(this);


        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
        {
            fileManager.scanFiles();
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                YesNoDialog.getInstance("I'm sorry. I can't work without the permission", "Grant =)", "never :(")
                        .setListener(new YesNoDialog.OnYesNoListener()
                        {
                            @Override
                            public void onYesClick()
                            {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1021);
                            }

                            @Override
                            public void onNoClick()
                            {
                                HomeActivity.this.finish();
                            }
                        })
                        .show(getSupportFragmentManager(), "permission_request_dialog");
            }
            else
            {
                // You can directly ask for the permission.
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1021);
            }
        }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 1021)
        {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                fileManager.scanFiles();
            }
            else
            {
                YesNoDialog.getInstance("Sorry, but I need the permission to work. If you won't grant it, then there's no point in this.", "Grant =)", "Goodbye :(")
                        .setListener(new YesNoDialog.OnYesNoListener()
                        {
                            @Override
                            public void onYesClick()
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1021);
                                }
                            }

                            @Override
                            public void onNoClick()
                            {
                                HomeActivity.this.finish();
                            }
                        })
                        .show(getSupportFragmentManager(), "permission_request_dialog");
            }
        }
    }

    @Override
    public void onFilesListUpdated(FolderFile[] files)
    {
        fileListFragment.setFiles(files);
    }

}