package pn532Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// I2C
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import pn532Test.commands.GetFirmwareVersion;
import pn532Test.commands.InListPassiveTarget;
import pn532Test.commands.SAMConfig;
import pn532Test.exceptions.TransmissionException;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    public static final boolean DEBUG = true;

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     * @throws IOException
     * @throws UnsupportedBusNumberException
     * @throws TimeoutException
     * @throws TransmissionException
     */
    public static void main(String[] args) throws IOException, UnsupportedBusNumberException, InterruptedException, TransmissionException, TimeoutException {
        System.out.println("Hello World!");
        I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1); // i2c bus of device 
        I2CDevice dev = i2c.getDevice(0x24); // NFC module addr

        System.out.println("Retrieving Firmware Data");
        Command firmware = new GetFirmwareVersion();
        firmware.send(dev);
        System.out.println(firmware.getResult(dev));
        System.out.println();
        System.out.println("Sending setup signal");

        Command setup = new SAMConfig();
        setup.send(dev);
        System.out.println(setup.getResult(dev));

        Command passiveTargets = new InListPassiveTarget(2);

        System.out.println();
        System.out.println("Listening for passive targets..");
        while (true) {
            passiveTargets.send(dev);
            System.out.println(passiveTargets.getResult(dev));
            Thread.sleep(50);
        }
    }

}
