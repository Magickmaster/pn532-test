package pn532Test;

public class HexUtils {
    /* s must be an even-length string. */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    public static String getByteString(byte[] arr) {
        String output = "[";
    
        if (arr != null) {
          for (int i = 0; i < arr.length; i++) {
            output += String.format("%02X ", arr[i]);
          }
        }
        return output.trim() + "]";
      }
}
