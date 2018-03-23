package com.laurens.hexcmd.read.readers;

import com.laurens.hexcmd.PacketDataException;
import com.laurens.hexcmd.read.BasicDataReader;
import com.laurens.hexcmd.read.HexReader;
import com.laurens.hexcmd.read.PacketListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * A data reader with fixed size buffers for each command channel.
 */
public class DynamicDataReader extends BasicDataReader implements HexReader
{
    private ByteArrayOutputStream[] data;
    private static int channels = 26;
    private ArrayList<PacketListener> listeners = new ArrayList<>();

    @Override
    public void addPacketListener(PacketListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removePacketListener(PacketListener listener)
    {
        listeners.remove(listener);
    }

    public DynamicDataReader()
    {
        data = new ByteArrayOutputStream[channels];
        for (int i = 0; i < channels; i++)
            data[i] = new ByteArrayOutputStream();
    }

    @Override
    public byte[] getBytes(char code) throws PacketDataException
    {
        int addr = code - 'A';
        return data[addr].toByteArray();
    }


    private int writeAdr = 0;
    @Override
    public void switchMode(char mode)
    {
        writeAdr = mode - 'A';
        if(data[writeAdr].size() != 0)
            throw new PacketDataException("Packet code " + mode + " already recieved for this packet");
    }

    @Override
    public void acceptByte(int value)
    {
        data[writeAdr].write(value);
    }

    private void clear()
    {
        for (int i = 0; i < channels; i++)
            data[i].reset();
    }

    @Override
    public void beginPacket()
    {
        clear();
    }

    @Override
    public void endPacket(boolean success)
    {
        for(PacketListener listener:listeners)
        {
            if(success)
                listener.onComplete(this);
            else
                listener.onFailure(this);
        }
    }

    @Override
    public void endConnection(boolean success)
    {
        for(PacketListener listener:listeners)
        {
            listener.onDisconnect(success);
        }
        listeners = null;
    }
}
