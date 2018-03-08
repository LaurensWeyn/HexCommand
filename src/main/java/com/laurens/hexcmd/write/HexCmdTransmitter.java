package com.laurens.hexcmd.write;

import com.laurens.hexcmd.HexCmdException;
import com.laurens.hexcmd.read.HexCmdReceiver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HexCmdTransmitter extends BasicDataWriter
{
    private OutputStream outputStream;
    private boolean inPacket = false;
    private boolean inChunk = false;

    public HexCmdTransmitter(OutputStream outputStream)
    {
        if(!(outputStream instanceof BufferedOutputStream))
            this.outputStream = new BufferedOutputStream(outputStream);
        else
            this.outputStream = outputStream;
    }
    @Override
    public void sendBytes(char code, byte[] data) throws IOException
    {
        if(!HexCmdReceiver.inCommandRange(code))
            throw new HexCmdException("Invalid command code: " + code);

        inPacket = true;
        inChunk = false;

        outputStream.write((int)code);
        for(byte b:data)
        {
            sendHex(b);
        }
    }

    public void sendChunk(byte[] data)throws IOException
    {
        if(!inChunk)
            throw new HexCmdException("Not in chunk transmission mode");
        for(byte b:data)
        {
            sendHex(b);
        }
    }
    public void startChunk(char code)throws IOException
    {
        if(!HexCmdReceiver.inCommandRange(code))
            throw new HexCmdException("Invalid command code: " + code);

        inChunk = true;
        inPacket = true;
        outputStream.write((int)code);
    }
    private void sendHex(byte b)throws IOException
    {
        outputStream.write(nibbleToChar((b & 0b11110000) >> 4));
        outputStream.write(nibbleToChar(b & 0b1111));
    }

    private int nibbleToChar(int value)
    {
        switch(value)
        {
            case 0b0000: return '0';
            case 0b0001: return '1';
            case 0b0010: return '2';
            case 0b0011: return '3';
            case 0b0100: return '4';
            case 0b0101: return '5';
            case 0b0110: return '6';
            case 0b0111: return '7';
            case 0b1000: return '8';
            case 0b1001: return '9';
            case 0b1010: return 'a';
            case 0b1011: return 'b';
            case 0b1100: return 'c';
            case 0b1101: return 'd';
            case 0b1110: return 'e';
            case 0b1111: return 'f';
            default: throw new IllegalArgumentException("Invalid hex character: " + value);
        }
    }

    public void endPacket()throws IOException
    {
        if(!inPacket)
            throw new HexCmdException("No packet to write");

        outputStream.write((int)'\n');
        outputStream.flush();
        inPacket = false;
    }

    public void close() throws IOException
    {
        if(inPacket)
            throw new HexCmdException("Cannot close stream: packet transmission in progress");
        outputStream.close();
    }
}
