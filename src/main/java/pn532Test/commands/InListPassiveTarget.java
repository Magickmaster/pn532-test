package pn532Test.commands;

import pn532Test.Command;
import pn532Test.FrameCreator;
import pn532Test.HexUtils;

/**
 * InListPassiveTarget
 */
public class InListPassiveTarget extends Command {
    int targets;
    BaudRate rate;
    byte[] initiatorData;
    byte[] frame;

    public InListPassiveTarget() {
        this(1, BaudRate.ISOTypeA);
    }

    public InListPassiveTarget(int maxTargets) {
        this(maxTargets, BaudRate.ISOTypeA);
    }

    public InListPassiveTarget(int maxTargets, BaudRate rate) {
        this(maxTargets, rate, new byte[] {});
    }

    public InListPassiveTarget(int maxTargets, BaudRate rate, byte[] initiatorData) {
        if (maxTargets > 2 || maxTargets < 1) {
            throw new Error("Unsupported target amount, max 2");
        }
        this.targets = maxTargets;
        this.rate = rate;
        this.initiatorData = initiatorData;

        int length = 4 + initiatorData.length;

        byte[] message = new byte[length];
        message[0] = (byte) 0xD4;
        message[1] = (byte) 0x4A;
        message[2] = (byte) maxTargets;
        message[3] = rate.code;
        for (int i = 4; i < message.length; i++) {
            message[i] = initiatorData[i - 4];
        }
        frame = FrameCreator.generateFrame(message);
    }

    public byte[] getFrame() {
        return frame;
    }

    @Override
    protected Object transform(byte[] payload) {
        byte nbTg = payload[0];
        String message = Long.toHexString(nbTg) + " Device" + (nbTg > 1 ? "s: " : ": ");

        if (rate == BaudRate.ISOTypeA) {
            if (nbTg != 1) {
                message += "#"+payload[1]+":";
            }
            message += " sensRes: " + (payload[2]<<8+payload[3]);
            message += " selRes: " + payload[4];
            message += " id len: " + payload[5];
            message += " id: ";
            for(int i = 6; i < payload[5] + 6;i++){
                message += HexUtils.getByteString(new byte[]{payload[i]});
            }
            int end = payload[5] + 7;
            if (nbTg != 1) {
                message += "#"+payload[end]+":";
                message += " sensRes: " + (payload[end + 2]<<8+payload[end + 3]);
                message += " selRes: " + payload[end +4];
                message += " id len: " + payload[end +5];
                message += " id: ";
                for(int i = end; i <end +  payload[5 + end] + 6;i++){
                    message += HexUtils.getByteString(new byte[]{payload[i]});
                }
            }
            return message;
        }
        return message + HexUtils.getByteString(payload);
    }

    public enum BaudRate {
        ISOTypeA(0x00), FeliCa212(0x01), FeliCa414(0x02), ISOTypeB(0x03), InnovisionJewel(0x04);

        public final byte code;

        private BaudRate(int code) {
            this.code = (byte) code;
        }
    }

}
