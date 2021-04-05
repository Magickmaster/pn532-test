package pn532Test.commands;

import pn532Test.*;

public class GetFirmwareVersion extends Command {
    byte[] frame = { 0x00, 0x00, (byte) 0xFF, 0x02, (byte) 0xFE, (byte) 0xD4, 0x02, 0x2A, 0x00 };

    public GetFirmwareVersion() {
    }

    @Override
    public byte[] getFrame() {
        return frame;
    }

    @Override
    protected String transform(byte[] payload) {
        // Expected: PN532 ver.1.6!
        if (payload.length < 3)
            return "Faulty payload!";
        return "PN5" + Long.toHexString(payload[0]) + " ver." + Long.toHexString(payload[1]) + "."
                + Long.toHexString(payload[2]);
    }

}
