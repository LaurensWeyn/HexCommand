package com.laurens.hexcmd;

import java.io.IOException;

public class HexCmdException extends IOException
{
    public HexCmdException() {
        super();
    }

    public HexCmdException(String message) {
        super(message);
    }

    public HexCmdException(String message, Throwable cause) {
        super(message, cause);
    }

    public HexCmdException(Throwable cause) {
        super(cause);
    }
}
