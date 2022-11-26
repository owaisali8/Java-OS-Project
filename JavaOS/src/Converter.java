
public class Converter {

    public static byte hexToByte(String hexString) {
        return (byte) ((toDigit(hexString.charAt(0)) << 4) + toDigit(hexString.charAt(1)));
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        var s = new String(hexDigits);
        return s.toUpperCase();
    }

}
