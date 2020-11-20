package com.agadimi.agplayer.ui.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.common.FileManager;
import com.agadimi.agplayer.common.ThumbnailTaskResultListener;
import com.agadimi.agplayer.models.SimpleFile;
import com.agadimi.agplayer.models.VideoFile;

import javax.inject.Inject;

public class FileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ThumbnailTaskResultListener
{
    private SimpleFile[] data;
    private ClickListener clickListener;
    private FileManager fileManager;

    @Inject
    public FileListAdapter(FileManager fileManager)
    {
        this.fileManager = fileManager;
    }

    public void setData(SimpleFile[] data)
    {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == SimpleFile.Type.FOLDER)
        {
            return new FolderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_folder, parent, false), clickListener);
        }
        else if (viewType == SimpleFile.Type.VIDEO)
        {
            return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_video_file, parent, false), clickListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (getItemViewType(position) == SimpleFile.Type.VIDEO && ((VideoFile) data[position]).getThumbnail() == null)
        {
            fileManager.loadThumbnail((VideoFile) data[position], this, position);
        }
        ((FileViewHolder) holder).update(data[position]);
    }

    @Override
    public int getItemCount()
    {
        return null == data ? 0 : data.length;
    }

    @Override
    public int getItemViewType(int position)
    {
        return data[position].getType();
    }

    @Override
    public void onFinished(Bitmap bitmap, int position)
    {
        ((VideoFile) data[position]).setThumbnail(bitmap);
        notifyItemChanged(position);
    }

    public interface FileViewHolder
    {
        void update(SimpleFile simpleFile);
    }

    public interface ClickListener
    {
        void onFileClick(SimpleFile simpleFile);
    }
}
