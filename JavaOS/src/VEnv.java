
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

    //constructor for reading and loading the file
    public VEnv(String filename) {
        this.readFile(filename);
        execute();
    }

    @Override
    public String toString() {
        return "\nGPR:\n" + Arrays.toString(GPR) + "\nSPR:\n" + Arrays.toString(SPR);
    }

    //showing our memory contents
    public String showMem() {
        String s = "Memory:\n[";
        for (int i = 0; i <= 15; i++) {
            s += memory[i] + ", ";
        }
        return s + "]";
    }

    public short[] getGPR() {
        return this.GPR;
    }

    public short[] getSPR() {
        return this.SPR;
    }
    
    public byte[] getMem(){
        return this.memory;
    }

    // Implentation of register to registor instructions -----------------------
    private void mov(byte r1, byte r2) {
        GPR[r1] = GPR[r2];
    }

    private void add(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] + GPR[r2]);
    }

    private void sub(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] - GPR[r2]);
    }

    private void mul(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] * GPR[r2]);
    }

    private void div(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] / GPR[r2]);
    }

    private void and(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] & GPR[r2]);
    }

    private void or(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] | GPR[r2]);
    }
    //--------------------------------------------------------------------------

    //Implementation of Register-Immediate Instructions-------------------------
    private short twoBytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    private void movi(byte r1, byte val1, byte val2) {
        GPR[r1] = this.twoBytesToShort(val1, val2);
    }

    private void addi(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] + this.twoBytesToShort(val1, val2));
    }

    private void subi(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] - this.twoBytesToShort(val1, val2));
    }

    private void muli(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] * this.twoBytesToShort(val1, val2));
    }

    private void divi(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] / this.twoBytesToShort(val1, val2));
    }

    private void andi(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] & this.twoBytesToShort(val1, val2));
    }

    private void ori(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] | this.twoBytesToShort(val1, val2));
    }

    private void act(byte val1, byte val2) { //Do the service defined by num
        this.twoBytesToShort(val1, val2);
    }

    //--------------------------------------------------------------------------
    //Implementation of Memory Instructions-------------------------------------
    private void movl(byte r, byte val1, byte val2){
        short location = this.twoBytesToShort(val1, val2);
        GPR[r] = memory[location];
    }
    
    private void movs(byte r, byte val1, byte val2){
        short value = GPR[r];
        short location = this.twoBytesToShort(val1, val2);
        memory[location] = (byte) value;
        
    }
    //--------------------------------------------------------------------------
    //Implementation of Single Operand Instructions-----------------------------
    private void shl(byte r1) {
        GPR[r1] = (short) (GPR[r1] << 1);
    }

    private void shr(byte r1) {
        GPR[r1] = (short) (GPR[r1] >> 1);
    }

    private void rtl(byte r1) {
        GPR[r1] = (short) (Integer.rotateLeft(GPR[r1], 1));
    }

    private void rtr(byte r1) {
        GPR[r1] = (short) (Integer.rotateRight(GPR[r1], 1));
    }

    private void inc(byte r1) {
        GPR[r1] = (short) (GPR[r1] + 1);
    }

    private void dec(byte r1) {
        GPR[r1] = (short) (GPR[r1] - 1);
    }

    private void push() {
    } //will be implemented later 

    private void pop() {
    } //will be implemented later 

    //--------------------------------------------------------------------------
    
    //Implementation of No Operand Instructions---------------------------------
    
    private void returnPC() {
        
    } //will be implemented later 
    
    //--------------------------------------------------------------------------

    //Load-Decode-Execute-------------------------------------------------------
    public final void loadIntoMemoryAndExecute(String line){//for testing purposes
        this.loadIntoMemory(line);
        this.execute();
    }
    
    private void readFile(String filename) {
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
        SPR[0] = codeCounter; //CB
        SPR[2] = codeCounter; //CC
        String[] hexArray = line.split(" ");
        for (String hex : hexArray) {
            memory[codeCounter] = Converter.hexToByte(hex);
            codeCounter++;
        }
        SPR[2] = codeCounter;
    }

    private void execute() {
        SPR[9] = SPR[0]; // SPR[9] is PC and SPR[0] is CB
        while (SPR[9] <= SPR[2]) { // Checking PC with CC
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
                case "31":
                    this.addi(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
                case "32":
                    this.subi(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
                case "33":
                    this.muli(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
                case "34":
                    this.divi(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
                case "35":
                    this.andi(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
                case "36":
                    this.ori(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;

                case "3D":
                    this.act(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "51":
                    this.movl(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
                case "52":
                    this.movs(memory[SPR[9] + 1], memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9] += 3;
                    break;
                case "71": 
                    this.shl(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "72": 
                    this.shr(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "73": 
                    this.rtl(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "74": 
                    this.rtr(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "75": 
                    this.inc(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "76": 
                    this.dec(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "F2": //NOOP: No Operation
                    break;
            }

            SPR[9]++; // moves to next instrcution
        }
    }
    //--------------------------------------------------------------------------

}
