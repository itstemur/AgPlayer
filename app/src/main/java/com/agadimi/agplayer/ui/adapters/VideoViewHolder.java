package com.agadimi.agplayer.ui.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agadimi.agplayer.databinding.RowFolderBinding;
import com.agadimi.agplayer.databinding.RowVideoFileBinding;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.models.SimpleFile;
import com.agadimi.agplayer.models.VideoFile;

class VideoViewHolder extends RecyclerView.ViewHolder implements FileListAdapter.FileViewHolder
{
    private RowVideoFileBinding binding;
    private FileListAdapter.ClickListener clickListener;

    public VideoViewHolder(@NonNull View itemView, FileListAdapter.ClickListener clickListener)
    {
        super(itemView);
        this.clickListener = clickListener;
        binding = RowVideoFileBinding.bind(itemView);
    }

    @Override
    public void update(SimpleFile simpleFile)
    {
        VideoFile file = (VideoFile) simpleFile;
        binding.fileNameTv.setText(file.getName());
        binding.fileDurationTv.setText(String.valueOf(file.getDuration()));

        itemView.setOnClickListener(v -> {
            clickListener.onFileClick(simpleFile);
        });

    }
}
