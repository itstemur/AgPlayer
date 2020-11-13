package com.agadimi.agplayer.models;

public class SimpleFile
{
    private String name;
    private String path;
    private byte type;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public byte getType()
    {
        return type;
    }

    public void setType(byte type)
    {
        this.type = type;
    }


    public static class Type{
        public static final byte VIDEO = 1;
        public static final byte FOLDER = 2;
    }
}
