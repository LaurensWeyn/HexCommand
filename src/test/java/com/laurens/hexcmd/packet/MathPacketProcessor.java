package com.laurens.hexcmd.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.laurens.hexcmd.read.DataReader;
import com.laurens.hexcmd.read.HexCmdReceiver;
import com.laurens.hexcmd.read.HexReader;
import com.laurens.hexcmd.read.PacketListener;
import com.laurens.hexcmd.read.readers.FixedDataReader;
import com.laurens.hexcmd.write.DataWriter;
import com.laurens.hexcmd.write.HexCmdTransmitter;

/**
 * Implementation of a simple test packet standard used in MathPacketTest
 */
public class MathPacketProcessor implements PacketListener
{
    private DataWriter writer;

    public MathPacketProcessor(InputStream input, OutputStream output)
    {
        writer = new HexCmdTransmitter(output);
        HexReader reader = new FixedDataReader(5);
        reader.addPacketListener(this);
        new Thread(new HexCmdReceiver(input, reader)).start();
    }

    @Override
    public void onComplete(DataReader reader)
    {
        try
        {
            switch(reader.getByte('M'))//mode
            {
                case 1://add shorts
                    writer.sendShort('E', (short) (reader.getShort('A') + reader.getShort('B')));
                    break;
                case 2://add floats
                    writer.sendFloat('E', reader.getFloat('A') + reader.getFloat('B'));
                    break;
            }
            writer.endPacket();
        }
        catch(IOException err)
        {
            throw new IllegalStateException("Transmission failure during test", err);
        }
    }

    public void close()
    {
        try
        {
            writer.close();
        }
        catch(IOException err)
        {
            throw new IllegalStateException("Transmission failure during test", err);
        }
    }

    @Override
    public void onFailure(DataReader reader)
    {
        throw new IllegalStateException("Packet failure during test");
    }

    @Override
    public void onDisconnect(boolean graceful)
    {
        if(!graceful)
            onFailure(null);
    }
}
