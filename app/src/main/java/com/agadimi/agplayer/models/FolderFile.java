package com.agadimi.agplayer.models;

import java.util.ArrayList;
import java.util.List;

public class FolderFile extends SimpleFile
{
    private List<SimpleFile> children;

    public void addChild(SimpleFile simpleFile)
    {
        if(null == children)
        {
            children = new ArrayList<>();
        }

        children.add(simpleFile);
    }

    public List<SimpleFile> getChildren()
    {
        return children;
    }

    public void setChildren(List<SimpleFile> children)
    {
        this.children = children;
    }

    public boolean equals(FolderFile folderFile)
    {
        return getPath().equals(folderFile.getPath());
    }

    @Override
    public String toString()
    {
        return "FolderFile{" +
                "name=" + getName() + ", " +
                "path=" + getPath() + ", " +
                "children=" + children +
                '}';
    }
}
