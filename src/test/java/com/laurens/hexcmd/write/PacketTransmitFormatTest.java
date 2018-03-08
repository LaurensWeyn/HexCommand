package com.laurens.hexcmd.write;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PacketTransmitFormatTest
{
    @Test
    public void testBasicPacketFormat()throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexCmdTransmitter transmitter = new HexCmdTransmitter(stream);

        transmitter.sendByte('A', 0);
        transmitter.sendByte('B', 1);
        transmitter.sendInt('C', 2);
        transmitter.endPacket();

        String output = new String(stream.toByteArray());
        Assert.assertEquals("A00B01C00000002\n", output);
    }

    @Test
    public void testHexFormat()throws IOException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        HexCmdTransmitter transmitter = new HexCmdTransmitter(stream);

        transmitter.sendByte('B', 0xFF);
        transmitter.sendByte('A', 0xE6);
        transmitter.sendInt('C', 0xFEB0C0F2);
        transmitter.sendLong('X', 0xFEB0C0F2DEADBEEFL);
        transmitter.endPacket();

        String output = new String(stream.toByteArray());
        Assert.assertEquals("BffAe6Cfeb0c0f2Xfeb0c0f2deadbeef\n", output);
    }
}