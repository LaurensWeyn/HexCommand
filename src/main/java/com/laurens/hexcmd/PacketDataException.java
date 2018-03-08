package com.laurens.hexcmd;

public class PacketDataException extends IllegalStateException
{
    public PacketDataException() {
        super();
    }

    public PacketDataException(String message) {
        super(message);
    }

    public PacketDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketDataException(Throwable cause) {
        super(cause);
    }
}
