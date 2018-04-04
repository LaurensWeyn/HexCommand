package com.laurens.hexcmd.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fazecast.jSerialComm.SerialPort;
import com.laurens.hexcmd.read.DataReader;
import com.laurens.hexcmd.read.HexCmdReceiver;
import com.laurens.hexcmd.read.readers.FixedDataReader;
import com.laurens.hexcmd.write.DataWriter;
import com.laurens.hexcmd.write.HexCmdTransmitter;

/**
 * Tests the MathPacket system on a connection to a separate device (over a COM port)
 */
public class MathPacketTestExternal
{
    private static DataReader reader;
    private static DataWriter writer;


    private static int processDelay = 100;
    private static int startupDelay = 2000;
    private static String comPortName = "COM8";
    private static SerialPort port;


    @BeforeClass
    public static void initConnection()throws Exception
    {
        port = SerialPort.getCommPort(comPortName);
        if(port.openPort())
        {
            port.setBaudRate(115200);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);

            Thread.sleep(startupDelay);

            InputStream in = port.getInputStream();
            OutputStream out = port.getOutputStream();

            //HexCmd network layer - our side
            HexCmdTransmitter transmitter = new HexCmdTransmitter(out);
            FixedDataReader reader = new FixedDataReader(50);
            HexCmdReceiver receiver = new HexCmdReceiver(in, reader);
            new Thread(receiver).start();

            MathPacketTestExternal.reader = reader;
            MathPacketTestExternal.writer = transmitter;
        }
        else
        {
            System.out.println("WARNING: could not open port on " + comPortName);
            System.out.println("Skipping external tests");
            port = null;
        }



    }

    @AfterClass
    public static void closeConnection()
    {
        port.closePort();
    }

    @Test
    public void testShorts()throws Exception
    {
        if(port == null)
            return;
        writer.sendByte('M', 1);//short mode
        writer.sendShort('A', (short)648);//A0288  8820
        writer.sendShort('B', (short)913);//B0391  2930
        writer.endPacket();
        Thread.sleep(processDelay);
        Assert.assertEquals((short)1561, reader.getShort('E'));
        //getting 1305 (0x0519)
        //expecting 1561 (0x0619)
    }

    @Test
    public void testFloats()throws Exception
    {
        if(port == null)
            return;
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
