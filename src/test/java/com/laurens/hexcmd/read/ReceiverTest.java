package com.laurens.hexcmd.read;

import com.laurens.hexcmd.read.readers.FixedDataReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ReceiverTest
{
    @Test
    public void basicReceiveTest() throws Exception
    {
        String input = "C01X3f\n";
        InputStream is = new ByteArrayInputStream(input.getBytes("UTF-8"));

        FixedDataReader reader = new FixedDataReader(100);
        HexCmdReceiver receiver = new HexCmdReceiver(is, reader);

        receiver.run();//run in this thread, will terminate when out of data

        Assert.assertEquals(1, reader.getByte('C'));
        Assert.assertEquals(0x3F, reader.getByte('X'));
    }
    @Test
    public void fixedSizeReaderTest()throws Exception
    {
        String input = "C01Xf372ea21\n";
        InputStream is = new ByteArrayInputStream(input.getBytes("UTF-8"));

        FixedDataReader reader = new FixedDataReader(4);
        HexCmdReceiver receiver = new HexCmdReceiver(is, reader);

        receiver.run();//run in this thread, will terminate when out of data

        Assert.assertEquals(1, reader.getByte('C'));
        Assert.assertEquals(0xf372ea21, reader.getInt('X'));
        //Assert.assertNull(reader.getBytes('Z'));//actually undefined so cannot test
    }
}