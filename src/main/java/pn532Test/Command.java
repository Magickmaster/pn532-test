package pn532Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.pi4j.io.i2c.I2CDevice;

import pn532Test.exceptions.ChecksumException;
import pn532Test.exceptions.TransmissionException;

public abstract class Command {
    public abstract byte[] getFrame();

    public int send(I2CDevice device) throws IOException {
        device.write(getFrame());
        int code = awaitAck(100, device);
        if (code != 0 && App.DEBUG) {
            System.out.println("ACK rejected: Code " + code);
        }
        return code;
    }

    protected abstract Object transform(byte[] payload);

    public Object getResult(I2CDevice device) throws TransmissionException, TimeoutException {
        byte[] resp = readResponse(42, 0, device);
        return transform(resp);
    }

    private static int awaitAck(int timeout, I2CDevice device) {
        byte PN532_ACK[] = new byte[] { 0, (byte) 0xFF, (byte) 0x00, (byte) 0xFF };
        byte ackbuff[] = new byte[11];
        int location = findStart(ackbuff);
        int time = 0;
        while (timeout > time || timeout == 0) {
            try {
                device.read(ackbuff, 0, 7);
                location = findStart(ackbuff);
                if (location != -1) {
                    // We received an answer
                    break;
                }
                time += 10;
                Thread.sleep(10);
            } catch (Exception e) {

            }
        }
        if (ackbuff[location + 2] == (byte) 0x01) {
            // We have an error code!
            return ackbuff[location + 5];
        }
        for (int i = 0; i < PN532_ACK.length; i++) {
            // Check if we received an ACK
            if (ackbuff[location + i] != PN532_ACK[i]) {
                return ackbuff[location + 5]; // Faulty ack
            }
        }
        return 0;
    }

    protected byte[] readResponse(int expectedLength, int timeout, I2CDevice device) throws TransmissionException, TimeoutException {
        byte response[] = new byte[expectedLength];

        int msg_start = findStart(response);
        int time = 0;
        while (timeout > time || timeout == 0) {
            try {
                device.read(response, 0, response.length);
                msg_start = findStart(response);
                if (msg_start != -1) {
                    break;
                }
                time += 10;
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
      
        if (msg_start == -1) {
           throw new TimeoutException();
        }
        byte len = response[msg_start + 2];
        if ((byte) ~len + 1 != response[msg_start + 3]) {
            throw new ChecksumException();
        }
        byte checksum = 0;
        for (int i = 0; i < len; i++) {
            checksum += response[msg_start + i + 4];
        }
        if ((byte) ~checksum + 1 != response[msg_start + len + 4]) {
            throw new ChecksumException();
        }
        byte[] buffer = new byte[len - 2];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = response[msg_start + i + 4 + 2];
        }

        return buffer;
    }

    private static int findStart(byte[] buf) {
        for (int i = 0; i < buf.length - 1; i++) {
            if (buf[i] == (byte) 0x00 && buf[i + 1] == (byte) 0xff) {
                return i;
            }
        }
        return -1;
    }

}
