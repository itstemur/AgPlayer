package com.agadimi.agplayer.common;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.agadimi.agplayer.models.FileFactory;
import com.agadimi.agplayer.models.FolderFile;
import com.agadimi.agplayer.models.VideoFile;

import javax.inject.Inject;

import timber.log.Timber;


public class FileManager
{
    private Context context;
    private FolderFile[] folders;
    private FileListener fileListener;

    @Inject
    public FileManager(Context context)
    {
        this.context = context;
    }

    public void setFileListener(FileListener fileListener)
    {
        this.fileListener = fileListener;
    }

    public void scanFiles()
    {
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA,
        };
        String sortOrder = MediaStore.Video.Media.DATA + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        ))
        {
            //i assume there's a folder for each video at this point, then at the end i resize array size to fit folders count
            folders = new FolderFile[cursor.getCount()];

            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);


            while (cursor.moveToNext())
            {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                String path = cursor.getString(pathColumn);

                VideoFile videoFile = FileFactory.createVideoFile(contentUri, name, duration, size);
                videoFile.setPath(path);
                addVideo(videoFile, path);
            }

            resizeFoldersArray();
            notifyFilesChanged();
        }
    }

    private void addVideo(VideoFile videoFile, String folderPath)
    {
        FolderFile folderFile = FileFactory.createFolderFile(folderPath);

        for (int i = 0; i < folders.length; i++)
        {
            if (null == folders[i])
            {
                folderFile.addChild(videoFile);
                folders[i] = folderFile;
                break;
            }
            else if (folderFile.equals(folders[i]))
            {
                folders[i].addChild(videoFile);
                return;
            }
        }
    }

    private void resizeFoldersArray()
    {
        Timber.d("original size: %d", folders.length);
        FolderFile[] tempFolders = null;

        for (int i = folders.length - 1; i >= 0; i--)
        {
            if (null == folders[i])
            {
                Timber.d("item %d is null", i);
                continue;
            }
            else if (null == tempFolders)
            {
                Timber.d("item %d is the first folder", i);
                tempFolders = new FolderFile[i + 1];
            }

            tempFolders[i] = folders[i];
        }

        folders = tempFolders;
    }

    public FolderFile[] getFolders()
    {
        return folders;
    }

    private void notifyFilesChanged()
    {
        if (null != fileListener)
        {
            fileListener.onFilesListUpdated(getFolders());
        }
    }

    public interface FileListener
    {
        void onFilesListUpdated(FolderFile[] files);
    }
}
