package com.agadimi.agplayer.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.app.App;
import com.agadimi.agplayer.dagger.components.AppComponent;
import com.agadimi.agplayer.databinding.FragmentFileListBinding;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.models.SimpleFile;
import com.agadimi.agplayer.models.VideoFile;
import com.agadimi.agplayer.ui.activities.PlayerActivity;
import com.agadimi.agplayer.ui.adapters.FileListAdapter;

import java.util.Arrays;

import javax.inject.Inject;

public class FileListFragment extends Fragment implements FileListAdapter.ClickListener
{
    private FragmentFileListBinding binding;
    private FolderFile[] folders;


    FileListAdapter fileListAdapter;
    LinearLayoutManager layoutManager;

    private boolean isFolderLevel = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentFileListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ((App) getActivity().getApplicationContext()).appComponent.inject(this);


        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.filesRv.setLayoutManager(layoutManager);

        fileListAdapter = new FileListAdapter();
        fileListAdapter.setClickListener(this);
        binding.filesRv.setAdapter(fileListAdapter);

        if (null != folders)
        {
            fileListAdapter.setData(folders);
        }
    }

    public boolean backPressed()
    {
        if(!isFolderLevel)
        {
            fileListAdapter.setData(folders);
            isFolderLevel = true;
            return true;
        }
        return false;
    }

    public void setFiles(FolderFile[] folders)
    {
        this.folders = folders;
        if (null != fileListAdapter)
        {
            fileListAdapter.setData(folders);
        }
    }

    @Override
    public void onFileClick(SimpleFile simpleFile)
    {
        if (simpleFile instanceof FolderFile)
        {
            FolderFile folder = (FolderFile) simpleFile;
            VideoFile[] children = new VideoFile[folder.getChildren().size()];
            children = folder.getChildren().toArray(new VideoFile[0]);
            fileListAdapter.setData(children);
            isFolderLevel = false;
        }
        else if (simpleFile instanceof VideoFile)
        {
            VideoFile file = (VideoFile) simpleFile;
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra(VideoFile.INTENT_KEY, file.getUri().toString()                                                 );
            startActivity(intent);
        }
    }
}
