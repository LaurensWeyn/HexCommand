package com.laurens.hexcmd.packet;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.laurens.hexcmd.read.DataReader;
import com.laurens.hexcmd.read.HexCmdReceiver;
import com.laurens.hexcmd.read.readers.FixedDataReader;
import com.laurens.hexcmd.write.DataWriter;
import com.laurens.hexcmd.write.HexCmdTransmitter;

/**
 * A simple packet protocol built on top of HexCommand for testing purposes.
 */
public class MathPacketTest
{
    private static DataReader reader;
    private static DataWriter writer;

    private static MathPacketProcessor mpp;

    private static int processDelay = 100;
    private static boolean processRemote = false;

    @BeforeClass
    public static void initConnection()throws IOException
    {
        if(processRemote)
            throw new IllegalStateException("Not implemented yet");
        else
            initLocal();
    }
    private static void initLocal()throws IOException
    {
        //simulated connections
        PipedOutputStream osIn = new PipedOutputStream();
        PipedInputStream isIn = new PipedInputStream(osIn);
        PipedOutputStream osOut = new PipedOutputStream();
        PipedInputStream isOut = new PipedInputStream(osOut);

        //HexCmd network layer - our side
        HexCmdTransmitter transmitter = new HexCmdTransmitter(osOut);
        FixedDataReader reader = new FixedDataReader(50);
        HexCmdReceiver receiver = new HexCmdReceiver(isIn, reader);
        new Thread(receiver).start();

        //HexCmd network layer - app side
        mpp = new MathPacketProcessor(isOut, osIn);


        MathPacketTest.reader = reader;
        MathPacketTest.writer = transmitter;
    }

    @AfterClass
    public static void closeConnection()
    {
        if(mpp != null)
            mpp.close();
    }

    @Test
    public void testShorts()throws Exception
    {
        writer.sendByte('M', 1);//short mode
        writer.sendShort('A', (short)648);
        writer.sendShort('B', (short)913);
        writer.endPacket();
        Thread.sleep(processDelay);
        Assert.assertEquals((short)1561, reader.getShort('E'));
    }

    @Test
    public void testFloats()throws Exception
    {
        float fA = 3.16815f;
        float fB = 157.34891f;
        float ans = fA + fB;
        writer.sendByte('M', 2);//float mode
        writer.sendFloat('A', fA);
        writer.sendFloat('B', fB);
        writer.endPacket();
        Thread.sleep(processDelay);
        Assert.assertEquals(ans, reader.getFloat('E'), 0.000001f);
    }
}
