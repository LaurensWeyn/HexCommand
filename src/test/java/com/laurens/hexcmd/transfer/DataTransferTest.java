package com.laurens.hexcmd.transfer;

import com.laurens.hexcmd.read.HexCmdReceiver;
import com.laurens.hexcmd.read.readers.FixedDataReader;
import com.laurens.hexcmd.write.HexCmdTransmitter;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DataTransferTest
{
    @Test
    public void typeTransferTest()throws Exception
    {
        //simulated connection
        PipedOutputStream os = new PipedOutputStream();
        PipedInputStream is = new PipedInputStream(os);

        //HexCmd network layer
        HexCmdTransmitter transmitter = new HexCmdTransmitter(os);
        FixedDataReader reader = new FixedDataReader(50);
        HexCmdReceiver receiver = new HexCmdReceiver(is, reader);

        //transmit data
        transmitter.sendByte('A', 123);
        transmitter.sendInt('B', -10_000);
        transmitter.sendLong('C', Long.MAX_VALUE - 42);
        transmitter.sendDouble('D', 123.4567);
        transmitter.sendFloat('E', 12.3f);
        transmitter.sendString('F', "Hello, World! こんにちは、世界！");
        transmitter.sendShort('G', (short)1234);

        transmitter.endPacket();//transmit
        os.close();//end the simulated connection so receiver will terminate
        receiver.run();//receive

        //confirm data (in reverse order, to test independent codes)
        Assert.assertEquals((short)1234, reader.getShort('G'));
        Assert.assertEquals("Hello, World! こんにちは、世界！", reader.getString('F'));
        Assert.assertEquals(12.3f, reader.getFloat('E'), 0);
        Assert.assertEquals(123.4567, reader.getDouble('D'), 0);
        Assert.assertEquals(Long.MAX_VALUE - 42, reader.getLong('C'));
        Assert.assertEquals(-10_000, reader.getInt('B'));
        Assert.assertEquals(123, reader.getByte('A'));
    }
}
