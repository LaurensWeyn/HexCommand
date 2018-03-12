package com.laurens.hexcmd.read.readers;

import com.laurens.hexcmd.PacketDataException;
import com.laurens.hexcmd.read.BasicDataReader;
import com.laurens.hexcmd.read.HexReader;
import com.laurens.hexcmd.read.PacketListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A data reader with fixed size buffers for each command channel.
 */
public class FixedDataReader extends BasicDataReader implements HexReader
{
    private byte[][] data;
    private int[] pointer;
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

    public FixedDataReader(int maxSize)
    {
        pointer = new int[channels];
        data = new byte[channels][maxSize];
    }
    public FixedDataReader(int[] channelSizes)
    {
        if(channelSizes.length != channels)
            throw new IllegalArgumentException("Must provide a size for exactly " + channels + " channels. Found " + channelSizes.length);

        pointer = new int[channels];
        data = new byte[channels][];

        for (int i = 0; i < channels; i++)
        {
            data[i] = new byte[channelSizes[i]];
        }
    }

    @Override
    public byte[] getBytes(char code) throws PacketDataException
    {
        int addr = code - 'A';
        int size = pointer[addr];
        return Arrays.copyOfRange(data[addr], 0, size);
    }


    private int writeAdr = 0;
    @Override
    public void switchMode(char mode)
    {
        writeAdr = mode - 'A';
        if(pointer[writeAdr] != 0)
            throw new PacketDataException("Packet code " + mode + " already recieved for this packet");
    }

    @Override
    public void acceptByte(int value)
    {
        if(pointer[writeAdr] >= data[writeAdr].length)
            throw new PacketDataException("Packet buffer overflow for channel " + (char)(writeAdr + 'A'));
        data[writeAdr][pointer[writeAdr]++] = (byte)value;
    }

    private void clear()
    {
        for (int i = 0; i < channels; i++)
            pointer[i] = 0;
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
