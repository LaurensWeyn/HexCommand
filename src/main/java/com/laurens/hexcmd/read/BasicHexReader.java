package com.laurens.hexcmd.read;

public abstract class BasicHexReader implements HexReader
{
    @Override
    public void beginPacket()
    {
    }

    @Override
    public void endPacket(boolean success)
    {
    }

    @Override
    public void endConnection(boolean success)
    {
    }
}
