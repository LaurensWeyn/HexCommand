package com.laurens.hexcmd.read;

import com.laurens.hexcmd.PacketDataException;

/**
 * A standard for retrieving bytes from packets in recieved HexCommand packets in native Java types.
 */
public interface DataReader
{
    byte[] getBytes(char code) throws PacketDataException;

    int getByte(char code)throws PacketDataException;

    int getInt(char code)throws PacketDataException;

    long getLong(char code)throws PacketDataException;

    String getString(char code)throws PacketDataException;

    double getDouble(char code)throws PacketDataException;

    float getFloat(char code)throws PacketDataException;
}
