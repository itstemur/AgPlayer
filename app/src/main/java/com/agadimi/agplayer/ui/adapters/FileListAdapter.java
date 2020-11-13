package com.agadimi.agplayer.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.models.SimpleFile;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private SimpleFile[] data;
    private ClickListener clickListener;

    @Inject
    public FileListAdapter()
    {
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

    public interface FileViewHolder
    {
        void update(SimpleFile simpleFile);
    }

    public interface ClickListener
    {
        void onFileClick(SimpleFile simpleFile);
    }
}
