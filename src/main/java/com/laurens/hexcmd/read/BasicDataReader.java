package com.laurens.hexcmd.read;

import com.laurens.hexcmd.PacketDataException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public abstract class BasicDataReader implements DataReader
{
    @Override
    public int getByte(char code) throws PacketDataException
    {
        byte[] bytes = getBytes(code);
        if(bytes == null)
            throw new PacketDataException("Null data for code " + code);
        if(bytes.length != 1)
            throw new PacketDataException("Incorrect data length for code " + code +": expected 1 byte(s), found " + bytes.length);

        return bytes[0];
    }

    @Override
    public int getInt(char code) throws PacketDataException
    {
        byte[] bytes = getBytes(code);
        if(bytes == null)
            throw new PacketDataException("Null data for code " + code);
        if(bytes.length != 4)
            throw new PacketDataException("Incorrect data length for code " + code +": expected 4 byte(s), found " + bytes.length);

        return ByteBuffer.wrap(bytes).getInt();
    }

    @Override
    public long getLong(char code) throws PacketDataException
    {
        byte[] bytes = getBytes(code);
        if(bytes == null)
            throw new PacketDataException("Null data for code " + code);
        if(bytes.length != 8)
            throw new PacketDataException("Incorrect data length for code " + code +": expected 8 byte(s), found " + bytes.length);

        return ByteBuffer.wrap(bytes).getLong();
    }

    @Override
    public String getString(char code) throws PacketDataException
    {
        byte[] bytes = getBytes(code);
        if(bytes == null)
            throw new PacketDataException("Null data for code " + code);

        try
        {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            //will literally never happen
            return null;
        }
    }

    @Override
    public double getDouble(char code) throws IllegalStateException
    {
        byte[] bytes = getBytes(code);
        if(bytes == null)
            throw new PacketDataException("Null data for code " + code);
        if(bytes.length != 8)
            throw new PacketDataException("Incorrect data length for code " + code +": expected 8 byte(s), found " + bytes.length);

        return ByteBuffer.wrap(bytes).getDouble();
    }

    @Override
    public float getFloat(char code) throws IllegalStateException
    {
        byte[] bytes = getBytes(code);
        if(bytes == null)
            throw new PacketDataException("Null data for code " + code);
        if(bytes.length != 4)
            throw new PacketDataException("Incorrect data length for code " + code +": expected 4 byte(s), found " + bytes.length);

        return ByteBuffer.wrap(bytes).getFloat();
    }

    @Override
    public short getShort(char code)
    {
        byte[] bytes = getBytes(code);
        if(bytes == null)
            throw new PacketDataException("Null data for code " + code);
        if(bytes.length != 2)
            throw new PacketDataException("Incorrect data length for code " + code +": expected 2 byte(s), found " + bytes.length);

        return ByteBuffer.wrap(bytes).getShort();
    }

    @Override
    public boolean hasValue(char code)
    {
        return getBytes(code) != null && getBytes(code).length > 0;
    }
}
