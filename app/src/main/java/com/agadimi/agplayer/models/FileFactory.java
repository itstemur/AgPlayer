package com.agadimi.agplayer.models;

import android.net.Uri;

public class FileFactory
{
    public static SimpleFile createFile(byte type)
    {
        if (type == SimpleFile.Type.FOLDER)
        {
            return new FolderFile();
        }
        else if (type == SimpleFile.Type.VIDEO)
        {
            return new VideoFile();
        }
        else
        {
            return new SimpleFile();
        }
    }

    public static SimpleFile createFile(String name, String path, byte type)
    {
        SimpleFile file = createFile(type);
        file.setName(name);
        file.setPath(path);
        file.setType(type);
        return file;
    }

    public static VideoFile createVideoFile(Uri uri, String name, int duration, int size)
    {
        VideoFile videoFile = (VideoFile) createFile(SimpleFile.Type.VIDEO);
        videoFile.setUri(uri);
        videoFile.setName(name);
        videoFile.setDuration(duration);
        videoFile.setSize(size);
        videoFile.setType(SimpleFile.Type.VIDEO);
        return videoFile;
    }

    public static FolderFile createFolderFile(String path)
    {
        int lastIndex = path.lastIndexOf('/');
        String[] pathSegments = path.split("/");
        if (pathSegments.length < 2)
        {
            return null;
        }

        String folderName = pathSegments[pathSegments.length - 2];
        String folderPath = path.substring(0, lastIndex);

        return (FolderFile) createFile(folderName, folderPath, SimpleFile.Type.FOLDER);
    }
}
