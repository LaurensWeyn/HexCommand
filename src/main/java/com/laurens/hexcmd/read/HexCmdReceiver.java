package com.laurens.hexcmd.read;

import com.laurens.hexcmd.HexCmdException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Low level HexCommand parser. Takes in bytes and passes them to a HexReader implementation for processing.
 */
public class HexCmdReceiver implements Runnable
{
    private HexReader hexReader;
    private InputStream inputStream;
    private ReceiveState state;
    private char lastHex;

    private boolean continueOnError = false;

    public HexCmdReceiver(InputStream inputStream, HexReader hexReader)
    {
        this.hexReader = hexReader;

        if(!(inputStream instanceof BufferedInputStream))
            this.inputStream = new BufferedInputStream(inputStream);
        else
            this.inputStream = inputStream;

        state = ReceiveState.noPacket;
    }

    void readByte() throws IOException, HexCmdException
    {
        if(state == ReceiveState.dead)
            return;

        int value = inputStream.read();
        char cvalue = (char)value;
        switch(state)
        {
            case noPacket:
                if(value == -1)
                {
                    state = ReceiveState.dead;//graceful close of connection
                    hexReader.endConnection(true);
                    return;
                }
                else
                {
                    if(!inCommandRange(cvalue))
                    {
                        hexReader.endPacket(false);
                        throw new HexCmdException("Packet did not start with command byte: found '" + cvalue + "' (byte " + value + ")");
                    }
                    state = ReceiveState.inPacket;
                    hexReader.beginPacket();//start of packet
                    hexReader.switchMode(cvalue);//start of data command
                }
                break;
            case inPacket:
                if(value == -1)//end of stream
                {
                    state = ReceiveState.dead;
                    hexReader.endPacket(false);
                    hexReader.endConnection(false);
                    throw new HexCmdException("End of stream reached partway through packet; packet and connection lost");
                }
                else if(inCommandRange(cvalue))//mode switch
                {
                    hexReader.switchMode(cvalue);
                }
                else if(inHexRange(cvalue))//data
                {
                    lastHex = cvalue;
                    state = ReceiveState.partialHex;
                }
                else if(cvalue == '\n')//end of packet
                {
                    hexReader.endPacket(true);
                    state = ReceiveState.noPacket;
                }
                else
                {
                    hexReader.endPacket(false);
                    throw new HexCmdException("Illegal HexCommand character: found '" + cvalue + "' (byte " + value + ")");
                }
                break;
            case partialHex:
                if(value == -1)//end of stream
                {
                    state = ReceiveState.dead;
                    hexReader.endPacket(false);
                    hexReader.endConnection(false);
                    throw new HexCmdException("End of stream reached partway through packet; packet and connection lost");
                }
                else if(inHexRange(cvalue))//data (Should be the only one to trigger!)
                {
                    //combine the two hex characters:
                    int byteValue = toNibble(lastHex) << 4 | toNibble(cvalue);
                    hexReader.acceptByte(byteValue);//receive
                    state = ReceiveState.inPacket;//no longer in partial hex
                }
                else if(inCommandRange(cvalue))//mode switch
                {
                    hexReader.endPacket(false);
                    throw new HexCmdException("Mode switch encountered partway through byte");
                }
                else if(cvalue == '\n')//end of packet
                {
                    hexReader.endPacket(false);
                    throw new HexCmdException("End of packet encountered partway through byte");
                }
                else
                {
                    hexReader.endPacket(false);
                    throw new HexCmdException("Illegal HexCommand character: found '" + cvalue + "' (byte " + value + ")");
                }
                break;
        }
    }

    public static boolean inCommandRange(char c)
    {
        return (c >= 'A' && c <= 'Z');
    }

    public static boolean inHexRange(char c)
    {
        return ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'));
    }

    private static int toNibble(char c)
    {
        switch(c)
        {
            case '0':return 0b0000;
            case '1':return 0b0001;
            case '2':return 0b0010;
            case '3':return 0b0011;
            case '4':return 0b0100;
            case '5':return 0b0101;
            case '6':return 0b0110;
            case '7':return 0b0111;
            case '8':return 0b1000;
            case '9':return 0b1001;
            case 'a':return 0b1010;
            case 'b':return 0b1011;
            case 'c':return 0b1100;
            case 'd':return 0b1101;
            case 'e':return 0b1110;
            case 'f':return 0b1111;
            default: throw new IllegalArgumentException("Invalid hex character: " + c);
        }
    }

    @Override
    public void run()
    {
        while(!isDead())
        {
            try
            {
                readByte();
            }catch (IOException err)
            {
                err.printStackTrace();
                if(!continueOnError)
                    state = ReceiveState.dead;
            }
        }
    }

    public boolean isDead()
    {
        return state == ReceiveState.dead;
    }

    public void setContinueOnError(boolean continueOnError)
    {
        this.continueOnError = continueOnError;
    }

    private enum ReceiveState
    {
        dead,//stream closed
        noPacket,//waiting for new packet data
        inPacket,//currently in packet, either for new byte or new command
        partialHex;//part way through hex byte
    }
}
