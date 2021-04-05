package pn532Test;

public class FrameCreator {
    public static byte[] generateFrame(byte[] payload) {
        byte[] message = new byte[payload.length + 5 + 2];
        message[0] = (byte) 0x00; // Preamble
        message[1] = (byte) 0x00; // Startcode
        message[2] = (byte) 0xff; // Startcode
        message[3] = (byte) payload.length;
        message[4] = (byte) (~message[3] + 1); // Checksum for length
        byte checksum = 0;
        for(int i = 0; i < payload.length; i++){
            message[5+i] = payload[i];
            checksum += payload[i];
        }
        message[message.length-2] = (byte)(~checksum + 1); // Checksum for data
        message[message.length-1 ] = (byte) 0x00; // postamble
  
        return message;
    }

}
