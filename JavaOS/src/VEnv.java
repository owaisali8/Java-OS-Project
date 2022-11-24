
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 *
 * @author Owais, Hamza and Yunus
 */
public class VEnv {

    // Intializing our arrays
    //  Special Purpose Registors(SPRs)
    /*
    Code base (CB)            - 0
    Code limit (CL)           - 1
    Code counter (CC)         - 2
    Data base (DB)            - 3
    Data limit (DL)           - 4
    Data counter (DC)         - 5
    Stack base (SB)           - 6
    Stack counter (SC)        - 7
    Stack limit (SL)          - 8
    Program counter (PC)      - 9
    Instruction Register (IR) - 10
    */
    
    // Flag Reigstors
    /* 
    Carry       - 0
    Zero        - 1
    Sign        - 2
    Overflow    - 3
    Unused      - 4-15*
    */
    
    //For Phase-1: Creating Arch
    private final short[] GPR = new short[16]; // R0-R15
    private final short[] SPR = new short[16];
    private final boolean[] flagRegistor = new boolean[16];
    private final byte[] memory = new byte[65536]; // memory size should be 64KB 2^16

    //For Phase-2
    private final PriorityQueue<PCB> readyQ1 = new PriorityQueue<>(15, new CustomComparator()); //0-15
    private final CircularQueue<PCB> readyQ2 = new CircularQueue<>(15); //16-31
    private final PriorityQueue<PCB> runQ = new PriorityQueue<>(1);
    
    private int runningPages = 0;
    private final int FRAME_SIZE = 128;
    private final boolean[] checkPages = new boolean[512];
    
    
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
    
    public String showFlags() {
        return "\nFlags:\n" + Arrays.toString(flagRegistor);
    }
    
    //showing our memory contents
    public String showMem(int n) {
        String s = "Memory:\n[";
        for (int i = 0; i <= n; i++) {
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
    
    public boolean[] getFlags(){
        return this.flagRegistor;
    }
    
    // Implentation of register to registor instructions -----------------------
    private void mov(byte r1, byte r2) {
        GPR[r1] = GPR[r2];
    }

    private void add(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] + GPR[r2]);
        int num = (GPR[r1] + GPR[r2]);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void sub(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] - GPR[r2]);
        int num = (GPR[r1] - GPR[r2]);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void mul(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] * GPR[r2]);
        int num = (GPR[r1] * GPR[r2]);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void div(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] / GPR[r2]);
        int num = (GPR[r1] / GPR[r2]);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void and(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] & GPR[r2]);
        int num = (GPR[r1] & GPR[r2]);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void or(byte r1, byte r2) {
        GPR[r1] = (short) (GPR[r1] | GPR[r2]);
        int num = (GPR[r1] | GPR[r2]);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
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
        
        int num = (GPR[r1] + this.twoBytesToShort(val1, val2));
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void subi(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] - this.twoBytesToShort(val1, val2));
        
        int num = (GPR[r1] - this.twoBytesToShort(val1, val2));
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void muli(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] * this.twoBytesToShort(val1, val2));
        
        int num = (GPR[r1] * this.twoBytesToShort(val1, val2));
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void divi(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] / this.twoBytesToShort(val1, val2));
        
        int num = (GPR[r1] / this.twoBytesToShort(val1, val2));
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void andi(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] & this.twoBytesToShort(val1, val2));
        
        int num = (GPR[r1] & this.twoBytesToShort(val1, val2));
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }

    private void ori(byte r1, byte val1, byte val2) {
        GPR[r1] = (short) (GPR[r1] | this.twoBytesToShort(val1, val2));
        
        int num = (GPR[r1] | this.twoBytesToShort(val1, val2));
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
        flagRegistor[3] = FlagCheck.checkOverflow(num);
    }
    
    private void bz(byte val1, byte val2){ //Check flag register, and jump to offset
        //if zero bit is ON: then jmp
        if(this.flagRegistor[1]) this.jmp(val1, val2);
    }
    
    private void bnz(byte val1, byte val2){
        //if zero bit is OFF: then jmp
        if(!this.flagRegistor[1]) this.jmp(val1, val2);
    }
    
    private void bc(byte val1, byte val2){
        if(!this.flagRegistor[0]) this.jmp(val1, val2);
    }
    
    private void bs(byte val1, byte val2){
        if(!this.flagRegistor[2]) this.jmp(val1, val2);
    }
    
    private void jmp(byte val1, byte val2){
        SPR[9] = this.twoBytesToShort(val1, val2);
    }
    
    private void call(byte val1, byte val2){
        SPR[7]++;
        memory[SPR[7]] = (byte) SPR[9]; 
        SPR[9] = this.twoBytesToShort(val1, val2);
    }
    
    private void act(byte val1, byte val2) { //Do the service defined by num
        //No service is mentioned 
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
        short num = (short) (GPR[r1] << 1);
        GPR[r1] = num;
        
        flagRegistor[0] = FlagCheck.checkCarry(num);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);
    }

    private void shr(byte r1) {
        short num = (short) (GPR[r1] >> 1);
        GPR[r1] = num;
                
        flagRegistor[0] = FlagCheck.checkCarry(num);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);

    }

    private void rtl(byte r1) {
        short num = (short) (Integer.rotateLeft(GPR[r1], 1));
        GPR[r1] = num;
                
        flagRegistor[0] = FlagCheck.checkCarry(num);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);

    }

    private void rtr(byte r1) {
        short num = (short) (Integer.rotateRight(GPR[r1], 1));
        GPR[r1] = num;
        
        flagRegistor[0] = FlagCheck.checkCarry(num);
        flagRegistor[1] = FlagCheck.checkZero(num);
        flagRegistor[2] = FlagCheck.checkSign(num);

    }

    private void inc(byte r1) {
        GPR[r1] = (short) (GPR[r1] + 1);
    }

    private void dec(byte r1) {
        GPR[r1] = (short) (GPR[r1] - 1);
    }

    private void push(byte r1) {
        //SPR[7] is Stack Counter (SC)
        SPR[7]++;
        memory[SPR[7]] = (byte) GPR[r1];
    }  

    private void pop(byte r1) {
        GPR[r1] = memory[SPR[7]];
        memory[SPR[7]] = 0;
        SPR[7]--;
   
    }

    //--------------------------------------------------------------------------
    
    //Implementation of No Operand Instructions---------------------------------
    
    private void returnPC() {
        SPR[9] = memory[SPR[7]];
        memory[SPR[7]] = 0;
        SPR[7]--;
    }
    
    //--------------------------------------------------------------------------

    //Load-Decode-Execute-------------------------------------------------------
    public final void loadIntoMemoryAndExecute(String line){//for testing purposes
        this.loadIntoMemory(line);
        this.execute();
    }
    
    private void readFile(String filename) {
        try {
            
            if(filename.endsWith(".bin")){
                readAndLoadBinFile(filename);
                return;
        }
            String line;
            Scanner code;

            code = new Scanner(new File(filename));
            line = code.nextLine();
            code.close();
            
            if(filename.endsWith(".byte")){
                loadByteFile(line);
            }
            
            else {
                loadIntoMemory(line);
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private void loadByteFile(String line){
        short codeCounter = 0;
        SPR[0] = codeCounter; //CB
        SPR[2] = codeCounter; //CC
        String[] byteArray = line.split(" ");
        for (String b : byteArray) {
            memory[codeCounter] = Byte.parseByte(b);
            codeCounter++;
        }
        SPR[2] = codeCounter;   
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

    
    private void readAndLoadBinFile(String filename){
        try {
            
            FileInputStream fileInputStream = new FileInputStream(new File(filename));
            byte [] fileCon = fileInputStream.readAllBytes();
            fileInputStream.close();
            
            int priority = fileCon[0];
            int pid = this.twoBytesToShort(fileCon[1], fileCon[2]);
            int dataSize = this.twoBytesToShort(fileCon[3], fileCon[4]);
            int codeSize = fileCon.length - dataSize - 8;
            
            if(priority<0 || priority >31) { //Check Priority
                System.out.println("Priority Out of Bounds");
                return;
            }
            
            int data_page = checkFreePage();
            int code_page = checkFreePage();
            
            int data_frame = data_page*FRAME_SIZE;
            int code_frame = code_page*FRAME_SIZE;
            
            int startData = 8;
            for(int i = data_frame; i<data_frame+dataSize; i++){
                memory[i] = fileCon[startData];
                startData++;
            }
            
            int startCode = startData;
            for(int i = code_frame; i<code_frame+codeSize; i++){
                memory[i] = fileCon[startCode];
                startCode++;
            }
            
            var dataPT = new PageTable();
            var codePT = new PageTable();
            
            dataPT.add(data_page, data_frame);
            codePT.add(code_page, code_frame);
           
            PCB p = new PCB(pid, priority, "P-"+code_page, codeSize, dataSize, codePT, dataPT);
            
            if(priority < 16) // Place in Appropriate Queue.
                this.readyQ1.add(p); // Priority
            else
                this.readyQ2.enqueue(p); //Round-Robin
            
        } catch (FileNotFoundException e){
            System.out.println("File Not Found");
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    private int checkFreePage(){
        for(int i = 1; i<checkPages.length; i++){
                if(!checkPages[i]){
                    checkPages[i] = true;
                    this.runningPages++;
                    return i;
                }
            }
        return -1;
    }
    
    //--------------------------------------------------------------------------
    
    private void execute() {
        System.out.println("ReadyQ1: "+this.readyQ1.toString());
        System.out.println("ReadyQ2: "+this.readyQ2.toString());
        
        
        //SPR[6] is Stack Base //1st page is alloted to stack
        SPR[6] = 0; SPR[7] = SPR[6];
        
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
                case "37":
                    this.bz(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "38":
                    this.bnz(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "39":
                    this.bc(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "3A":
                    this.bs(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
                    break;
                case "3B":
                    this.jmp(memory[SPR[9] + 2], memory[SPR[9] + 3]); // changed!!!!
                    SPR[9] += 3;
                    break;
                case "3C":
                    this.call(memory[SPR[9] + 1], memory[SPR[9] + 2]);
                    SPR[9] += 2;
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
                case "77":
                    this.push(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "78":
                    this.pop(memory[SPR[9]+1]);
                    SPR[9]++;
                    break;
                case "F1":
                    this.returnPC();
                    break;
                case "F2": //NOOP: No Operation
                    break;
                default:
                    if(SPR[10] != 0)
                        System.out.println("Invalid Opcode");
            }

            SPR[9]++; // moves to next instrcution
        }
    }
    //--------------------------------------------------------------------------

}
