package com.agadimi.agplayer.ui.adapters;

import android.media.ThumbnailUtils;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.common.FileManager;
import com.agadimi.agplayer.databinding.RowFolderBinding;
import com.agadimi.agplayer.databinding.RowVideoFileBinding;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.models.SimpleFile;
import com.agadimi.agplayer.models.VideoFile;
import com.agadimi.agplayer.ui.utilities.ScreenUtils;

import java.io.IOException;

import timber.log.Timber;

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
        binding.fileDurationTv.setText(file.getVideoLength());
        if ((file.getThumbnail() != null))
        {
            binding.fileThumbnailIv.setPadding(0, 0, 0, 0);
            binding.fileThumbnailIv.setImageBitmap(file.getThumbnail());
        }
        else
        {
            int topPad = ScreenUtils.convertDIPToPixels(itemView.getContext(), 50);
            int rightPad, bottomPad;
            rightPad = bottomPad = ScreenUtils.convertDIPToPixels(itemView.getContext(), 8);
            int leftPad = ScreenUtils.convertDIPToPixels(itemView.getContext(), 60);
            binding.fileThumbnailIv.setPadding(leftPad, topPad, rightPad, bottomPad);
            binding.fileThumbnailIv.setImageResource(R.drawable.ic_videocam);
        }

//        Timber.d("byte count: %d, %d * %d", file.getThumbnail().getByteCount(), file.getThumbnail().getWidth(), file.getThumbnail().getHeight());

        itemView.setOnClickListener(v -> {
            clickListener.onFileClick(simpleFile);
        });

    }
}
