package com.agadimi.agplayer.ui.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agadimi.agplayer.databinding.RowFolderBinding;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.models.SimpleFile;

class FolderViewHolder extends RecyclerView.ViewHolder implements FileListAdapter.FileViewHolder
{
    private RowFolderBinding binding;
    private FileListAdapter.ClickListener clickListener;

    public FolderViewHolder(@NonNull View itemView, FileListAdapter.ClickListener clickListener)
    {
        super(itemView);
        this.clickListener = clickListener;
        binding = RowFolderBinding.bind(itemView);
    }

    @Override
    public void update(SimpleFile simpleFile)
    {
        FolderFile folder = (FolderFile) simpleFile;
        binding.folderNameTv.setText(folder.getName());
        int videoCount = folder.getChildren().size();
        binding.childFileCountTv.setText(videoCount > 1 ? String.format("%d videos", videoCount) : "1 video");

        itemView.setOnClickListener(v -> {
            clickListener.onFileClick(simpleFile);
        });
    }
}
