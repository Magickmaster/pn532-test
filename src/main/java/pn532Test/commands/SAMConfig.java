package pn532Test.commands;

import pn532Test.Command;
import pn532Test.FrameCreator;
import pn532Test.HexUtils;

public class SAMConfig extends Command {
    byte[] frame;

    public SAMConfig() {
        this(Mode.Normal);
    }

    public SAMConfig(Mode mode) {
        this(mode, (byte) 0x00);
    }

    public SAMConfig(Mode mode, byte timeout) {
        this(mode, timeout, true);
    }

    public SAMConfig(Mode mode, byte timeout, boolean irq) {
        byte[] frame = new byte[5];
        frame[0] = (byte) 0xD4;
        frame[1] = (byte) 0x14;
        frame[2] = mode.code;
        frame[3] = timeout;
        frame[4] = (byte) (irq ? 0x01 : 0x00);
        this.frame = FrameCreator.generateFrame(frame);
    }

    @Override
    public byte[] getFrame() {
        return frame;
    }

    @Override
    protected Object transform(byte[] payload) {
        return HexUtils.getByteString(payload) + " - No answer expected";
    }

    public enum Mode {
        Normal(0x01), Virtual(0x02), Wired(0x03), Dual(0x04);

        public final byte code;

        Mode(int code) {
            this.code = (byte) code;
        }
    }

}
