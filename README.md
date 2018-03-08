#HexCommand
HexCommand is a low level hexadecimal based communication protocol, designed for both simple hardcoded implementations on embedded systems and more complex, general purpose implementations such as this Java library.

HexCommand can be used for microcontroller to microcontroller communication, microcontroller to PC communication, or PC to PC communication over any medium (Ethernet, RS-232...)

This particular implementation was written as part of a University assignment, but the protocol is designed to be as generic as possible.

##Protocol outline
HexCommand communicates entirely with alphanumeric characters (and the newline character),
meaning any protocol that allows sending/recieving 8 or 7 bit ASCII characters can be used for HexCommand.

Characters are used as follows:
- characters A-Z (uppercase) represent channels
- characters 0-9 and a-f (lowercase) represent data in hexadecimal
- the newline character '\n' represents the end of a packet.

A HexCommand packet consists of pieces of data in various channels.
Each channel's value is communicated by sending the channel name (one character from A-Z) and then its value in hexadecimal.

A packet might look like this:

`A01B03f2c6Xffffff80\n`

This translates to:

- channel A having a vlaue of 0x01
- channel B having a value of 0x03F2C6
- channel X having a value of 0xFFFFFF80

What type these values have depend on the protocol.
Types can be bytes, ints, UTF-8 Strings, doubles, or any user type.
The transmitting and recieving side would both agree on the types stored in each channel, and what each channel represents.

##Protocol notes
This protocol is designed to be very general, and some of the details are up to a specific usage of the protocol to decide.

- The order of channels either can or cannot matter. For embedded system implementations with low memory, implementations may typically expect channels to be sent in a predefined order.
In this Java implementation's DataReader interface, the recieving side does not care about the order. However, a custom HexReader can be written that expects a certain order.
It is not recommended to encode information into the order of channels; enforcing rules on the order should only be for optimising implementations.

- An array of data can be transmitted by sending a channel multiple times.
This library's built in DataReaders do not support this and will throw an error, but a future MultiDataReader interface, or a custom HexReader can allow for this.
DataWriter will allow multiple channel writes.

- The amount of data in each channel must be a whole number of bytes.
In other words, there must be an even number of hexadecimal characters in each channel in the packet.
There is no limit on the number of bytes transmitted in a channel.


