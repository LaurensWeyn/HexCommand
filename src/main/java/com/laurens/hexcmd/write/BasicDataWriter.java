package com.laurens.hexcmd.write;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public abstract class BasicDataWriter implements DataWriter
{
    @Override
    public void sendByte(char code, int data) throws IOException
    {
        sendBytes(code, new byte[]{(byte)data});
    }

    @Override
    public void sendInt(char code, int data) throws IOException
    {
        sendBytes(code, ByteBuffer.wrap(new byte[4]).putInt(data).array());
    }

    @Override
    public void sendLong(char code, long data) throws IOException
    {
        sendBytes(code, ByteBuffer.wrap(new byte[8]).putLong(data).array());
    }

    @Override
    public void sendString(char code, String data) throws IOException
    {
        try
        {
            sendBytes(code, data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            //will literally never happen
        }

    }

    @Override
    public void sendDouble(char code, double data) throws IOException
    {
        sendBytes(code, ByteBuffer.wrap(new byte[8]).putDouble(data).array());
    }

    @Override
    public void sendFloat(char code, float data) throws IOException
    {
        sendBytes(code, ByteBuffer.wrap(new byte[4]).putFloat(data).array());
    }
}
