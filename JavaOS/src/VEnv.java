
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Owais, Hamza and Yunus
 */
public class VEnv {

    // Intializing our arrays
    private final short[] GPR = new short[16]; // R0-R15
    private final short[] SPR = new short[16];
    private final boolean[] flagRegistor = new boolean[16];
    private final byte[] memory = new byte[65536]; // memory size should be 64KB 2^16

    public VEnv() {
    }

    public VEnv(String filename) {
        this.readFile(filename);
        execute();
    }

    @Override
    public String toString() {
        return "\nGPR:\n" + Arrays.toString(GPR) + "\nSPR:\n" + Arrays.toString(SPR);
    }

    public String showMem() {
        String s = "Memory:\n[";
        for (int i = 0; i <= 15; i++) {
            s += memory[i] + ", ";
        }
        return s + "]";
    }

    // Implentation of register to registor instructions --------------------
    public void mov(int r1, int r2) {
        GPR[r1] = GPR[r2];
    }

    public void add(int r1, int r2) {
        GPR[r1] = (short) (GPR[r1] + GPR[r2]);
    }

    public void sub(int r1, int r2) {
        GPR[r1] = (short) (GPR[r1] - GPR[r2]);
    }

    public void mul(int r1, int r2) {
        GPR[r1] = (short) (GPR[r1] * GPR[r2]);
    }

    public void div(int r1, int r2) {
        GPR[r1] = (short) (GPR[r1] / GPR[r2]);
    }

    public void and(int r1, int r2) {
        GPR[r1] = (short) (GPR[r1] & GPR[r2]);
    }

    public void or(int r1, int r2) {
        GPR[r1] = (short) (GPR[r1] | GPR[r2]);
    }
    //-----------------------------------------------------------------------

    //Implementation of Register-Immediate Instructions----------------------
    private short twoBytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    public void movi(int r1, byte val1, byte val2) {
        GPR[r1] = this.twoBytesToShort(val1, val2);
    }

    //-----------------------------------------------------------------------
    public final void readFile(String filename) {
        try {
            String line;
            Scanner code;

            code = new Scanner(new File(filename));
            line = code.nextLine();
            code.close();
            loadIntoMemory(line);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private void loadIntoMemory(String line) {
        short codeCounter = 0;
        SPR[0] = codeCounter;
        SPR[2] = codeCounter;
        String[] hexArray = line.split(" ");
        for (String hex : hexArray) {
            memory[codeCounter] = Converter.hexToByte(hex);
            codeCounter++;
        }
        SPR[2] = codeCounter;
    }

    private void execute() {
        SPR[9] = SPR[0]; // SPR[9] is PC and SPR[0] is CB
        while (SPR[9] <= SPR[2]) {
            SPR[10] = memory[SPR[9]]; //SPR[10] is IR
            String instruction = Converter.byteToHex((byte) SPR[10]);
            if (instruction.contentEquals("F3")) {
                break;
            }

            switch (instruction) {
                case "16":
                    this.mov(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "17":
                    this.add(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "18":
                    this.sub(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "19":
                    this.mul(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "1A":
                    this.div(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "1B":
                    this.and(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "1C":
                    this.or(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "30":
                    this.movi(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
            }

            SPR[9]++;
        }
    }

}
