package com.laurens.hexcmd.read;

/**
 * Generic interface for parsing low level HexCommand packets.
 */
public interface HexReader
{
    /**
     * Called when a command byte is reached. This will always be called at the start of a packet before data is accepted.
     * @param mode the mode character, A-Z
     */
    void switchMode(char mode);

    /**
     * Called for every byte (2 hex characters) of data received.
     * Type is int for convenience in Java, but values are strictly in the range of 0 to 255.
     * This byte is part of the mode given by the last call to {@link HexReader#switchMode(char)}.
     * @param value the next byte in the HexCommand packet
     */
    void acceptByte(int value);

    /**
     * Called at the start of a new packet, or on receiving the first packet.
     */
    void beginPacket();

    /**
     * Called when all packet data is complete
     * @param success false if the packet was invalid
     */
    void endPacket(boolean success);

    /**
     * Called when the connection has been closed.
     * @param success false if the connection was terminated during a packet or data transmission
     */
    void endConnection(boolean success);

    default void addPacketListener(PacketListener listener)
    {
        throw new UnsupportedOperationException("Adding listeners not supported");
    }
    default void removePacketListener(PacketListener listener)
    {
        throw new UnsupportedOperationException("Removing listeners not supported");
    }
}
