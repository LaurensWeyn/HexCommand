package com.laurens.hexcmd.write;

import java.io.IOException;

public interface DataWriter
{
    void sendBytes(char code, byte[] data) throws IOException;

    void sendByte(char code, int data) throws IOException;

    void sendInt(char code, int data) throws IOException;

    void sendShort(char code, short data) throws IOException;

    void sendLong(char code, long data) throws IOException;

    void sendString(char code, String data) throws IOException;

    void sendDouble(char code, double data) throws IOException;

    void sendFloat(char code, float data) throws IOException;

    void endPacket() throws IOException;;

    void close() throws IOException;;
}
