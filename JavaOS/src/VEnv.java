
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
    private short[] GPR = new short[16]; // R0-R15
    private short[] SPR = new short[16];
    private boolean[] flagRegistor = new boolean[16];
    private final byte[] memory = new byte[65536]; // memory size should be 64KB 2^16

    //For Phase-2
    private final PriorityQueue<PCB> readyQ1 = new PriorityQueue<>(15, new CustomComparator()); //0-15
    private final CircularQueue<PCB> readyQ2 = new CircularQueue<>(15); //16-31
    private final PriorityQueue<PCB> runQ = new PriorityQueue<>(1, new CustomComparator());

    private int allocPages = 0;
    private final int FRAME_SIZE = 128;
    private final boolean[] checkPages = new boolean[512];

    //For Phase-3
    private final PriorityQueue<PCB> blockedQ = new PriorityQueue<>(15, new CustomComparator());

    public VEnv() {
    }

    //constructor for reading and loading the file
    public VEnv(String filename) {
        this.readFile(filename);
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

    public String showMem() {
        String s = "Memory:\n[";
        int j = 0;
        for (int i = 0; i < memory.length; i++) {
            s += memory[i] + ", ";
            j++;
            if (j == 4096) {
                j = 0;
                s += "\n";
            }
        }
        return s + "]";
    }

    public short[] getGPR() {
        return this.GPR;
    }

    public short[] getSPR() {
        return this.SPR;
    }

    public byte[] getMem() {
        return this.memory;
    }

    public boolean[] getFlags() {
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

    private void bz(byte val1, byte val2) { //Check flag register, and jump to offset
        //if zero bit is ON: then jmp
        if (this.flagRegistor[1]) {
            this.jmp(val1, val2);
        }
    }

    private void bnz(byte val1, byte val2) {
        //if zero bit is OFF: then jmp
        if (!this.flagRegistor[1]) {
            this.jmp(val1, val2);
        }
    }

    private void bc(byte val1, byte val2) {
        if (!this.flagRegistor[0]) {
            this.jmp(val1, val2);
        }
    }

    private void bs(byte val1, byte val2) {
        if (!this.flagRegistor[2]) {
            this.jmp(val1, val2);
        }
    }

    private void jmp(byte val1, byte val2) {
        // Adding code base to offset
        short addr = (short) (SPR[0] + this.twoBytesToShort(val1, val2));
        SPR[9] = addr;

        if (addr < SPR[0] || addr > SPR[1]) {
            System.out.println("Using Area Outside Allocated Place");
        }
    }

    private void call(byte val1, byte val2) {
        SPR[7]++;
        memory[SPR[7]] = (byte) SPR[9];
        SPR[9] = (short) (SPR[0] + this.twoBytesToShort(val1, val2));
    }

    private void act(byte val1, byte val2) { //Do the service defined by num
        //No service is mentioned 
        this.twoBytesToShort(val1, val2);
    }

    //--------------------------------------------------------------------------
    //Implementation of Memory Instructions-------------------------------------
    private void movl(byte r, byte val1, byte val2) {
        short location = this.twoBytesToShort(val1, val2);
        GPR[r] = memory[location + SPR[3]];
        this.checkData((short) (location + SPR[3]));
    }

    private void movs(byte r, byte val1, byte val2) {
        short value = GPR[r];
        short location = this.twoBytesToShort(val1, val2);
        memory[location + SPR[3]] = (byte) value;

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
    public final void loadIntoMemoryAndExecute(String line) {//for testing purposes
        this.loadIntoMemory(line);
        this.execute();
    }

    private void readFile(String filename) {
        try {

            if (filename.endsWith(".bin")) {
                readAndLoadBinFile(filename);
                return;
            }
            String line;
            Scanner code;

            code = new Scanner(new File(filename));
            line = code.nextLine();
            code.close();

            if (filename.endsWith(".byte")) {
                loadByteFile(line);
            } else {
                loadIntoMemory(line);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private void loadByteFile(String line) {
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

    protected void readAndLoadBinFile(String filename) {
        try {

            FileInputStream fileInputStream = new FileInputStream(new File(filename));
            byte[] fileCon = fileInputStream.readAllBytes();
            fileInputStream.close();

            int priority = fileCon[0];
            int pid = this.twoBytesToShort(fileCon[1], fileCon[2]);
            int dataSize = this.twoBytesToShort(fileCon[3], fileCon[4]);
            int codeSize = fileCon.length - dataSize - 8;

            if (priority < 0 || priority > 31) { //Check Priority
                System.out.println("Priority Out of Bounds");
                return;
            }

            int reqDataPages = 1;
            int reqCodePages = 1;

            while (reqDataPages * FRAME_SIZE < dataSize) {
                reqDataPages++;
            }

            while (reqCodePages * FRAME_SIZE < codeSize) {
                reqCodePages++;
            }

            int[] data_pages = checkFreePage(reqDataPages);
            int[] code_pages = checkFreePage(reqCodePages);

            int data_frame = data_pages[0] * FRAME_SIZE;
            int code_frame = code_pages[0] * FRAME_SIZE;

            int startData = 8;
            for (int i = data_frame; i < data_frame + dataSize; i++) {
                memory[i] = fileCon[startData];
                startData++;
            }

            int startCode = startData;
            for (int i = code_frame; i < code_frame + codeSize; i++) {
                memory[i] = fileCon[startCode];
                startCode++;
            }

            PageTable dataPT = new PageTable();
            PageTable codePT = new PageTable();

            for (int i : data_pages) {
                dataPT.add(i, i * FRAME_SIZE);
            }

            for (int i : code_pages) {
                codePT.add(i, i * FRAME_SIZE);
            }

            PCB p = new PCB(pid, priority, filename, codeSize, dataSize, codePT, dataPT);

            if (priority < 16) // Place in Appropriate Queue.
            {
                this.readyQ1.add(p); // Priority
            } else {
                this.readyQ2.enqueue(p); //Round-Robin
            }
            System.out.println("Process ID: " + pid);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private int[] checkFreePage(int n) {
        int[] arr = new int[n];
        int j = 0;
        for (int i = 1; i < checkPages.length; i++) {
            if (!checkPages[i]) {
                checkPages[i] = true;
                this.allocPages++;
                arr[j] = i;
                j++;
                if (j == n) {
                    break;
                }
            }
        }
        return arr;
    }

    private boolean checkStack() {
        if (SPR[7] < 0 || SPR[7] > SPR[8]) { //if counter > limit
            System.out.println("Stack is Overflowing or Underflowing");
            return true;
        }
        return false;
    }

    private void checkData(short addr) {
        if (addr < SPR[3] || addr > SPR[4]) {
            System.out.println("Using data outside allocated place");
        }
    }

    //--------------------------------------------------------------------------
    private void execute() {
        System.out.println("ReadyQ1:\n" + this.readyQ1.toString());
        System.out.println("ReadyQ2:\n" + this.readyQ2.toString());

        //SPR[6] is Stack Base //1st page is alloted to stack
        SPR[6] = 0;
        SPR[7] = SPR[6];
        checkPages[0] = true;

        int lastExecTime = 0;
        boolean roundRobin;

        while (!readyQ1.isEmpty() || !readyQ2.isEmpty()) {
            if (!readyQ1.isEmpty()) {
                runQ.add(readyQ1.remove());
                roundRobin = false;
            } else {
                runQ.add(readyQ2.dequeue());
                roundRobin = true;
            }

            PCB currPCB = runQ.peek();
            this.setRegistors(currPCB);

            int startTime = (int) System.nanoTime();
            int quantum = 0;

            if (!currPCB.getRunTwice()) {
                SPR[9] = SPR[0]; // SPR[9] is PC and SPR[0] is CB
            }
            loop:
            while (SPR[9] <= SPR[1]) { // Checking PC with CL
                SPR[10] = memory[SPR[9]]; //SPR[10] is IR
                String instruction = Converter.byteToHex((byte) SPR[10]);
                if (instruction.contentEquals("F3")) {
                    System.out.println(currPCB.getName() + " proc has ended -> F3");
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
                        this.bz(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                        SPR[9]--;
                        break;
                    case "38":
                        this.bnz(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                        SPR[9]--;
                        break;
                    case "39":
                        this.bc(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                        SPR[9]--;
                        break;
                    case "3A":
                        this.bs(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                        SPR[9]--;
                        break;
                    case "3B":
                        this.jmp(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                        SPR[9]--;
                        break;
                    case "3C":
                        this.call(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                        SPR[9]--;
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
                        this.shl(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "72":
                        this.shr(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "73":
                        this.rtl(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "74":
                        this.rtr(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "75":
                        this.inc(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "76":
                        this.dec(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "77":
                        this.push(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "78":
                        this.pop(memory[SPR[9] + 1]);
                        SPR[9]++;
                        break;
                    case "F1":
                        this.returnPC();
                        break;
                    case "F2": //NOOP: No Operation
                        break;
                    default:
                        if (SPR[10] != 0) {
                            System.out.println("Invalid Opcode");
                            break loop;
                        }

                }

                if (checkStack()) {
                    return;
                }

                SPR[9]++; // moves to next instrcution

                quantum++;

                if (quantum == 4 && roundRobin) {
                    PCB p = runQ.remove();
                    p.setRunTwice(true);
                    p.setGPR(this.GPR);
                    p.setSPR(this.SPR);
                    p.setFlags(this.flagRegistor);
                    readyQ2.enqueue(p);
                    break;
                }

            }
            if (!runQ.isEmpty()) {
                int endTime = (int) System.nanoTime();
                int execTime = endTime - startTime;
                currPCB.setExecTime(execTime);
                currPCB.setWaitTime(lastExecTime);
                lastExecTime += execTime;
                this.terminate();
            }

        }
    }

    public void executeAll() {
        try {
            execute();
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Invalid Registor Code or Memory Address");
        } catch (ArithmeticException ex) {
            System.out.println("Error: Divide by Zero");
        }
    }

    private void terminate() {
        //Printing PCB and state of Venv
        PCB endPCB = runQ.remove();
        System.out.println("\nPCB Terminated: " + endPCB);
        System.out.println("Execution Time: " + endPCB.getExecTime() / 1000000.0 + "ms");
        System.out.println("Waiting Time: " + endPCB.getWaitTime() / 1000000.0 + "ms");
        System.out.println(this.showMem());
        System.out.println(this.toString());
        System.out.println(this.showFlags());

        //free page...
        int[] pages = endPCB.getPages();

        for (int i : pages) {
            this.checkPages[i] = false;
            this.allocPages--;
        }

        System.out.println("Allocated Pages: " + allocPages);

        try {
            FileWriter file = new FileWriter("Memory Dump(" + endPCB.getName() + ").txt");
            file.append("Memory Dump\n");
            file.append("\nExecution Time: " + endPCB.getExecTime() / 1000000.0 + "ms");
            file.append("\nWaiting Time: " + endPCB.getWaitTime() / 1000000.0 + "ms");
            file.append("\nPCB Terminated: " + endPCB + "\n");
            file.append(this.showMem());
            file.append(this.toString());
            file.append(this.showFlags());
            file.append("\nAllocated Pages: " + allocPages);
            file.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //--------------------------------------------------------------------------
    //For CLI commands:
    protected String shutdown() {
        if (readyQ1.isEmpty() && readyQ2.isEmpty()) {
            return "";
        }
        String str = "Killed Processes:\n";

        while (!readyQ1.isEmpty()) {
            str += readyQ1.remove().getID() + "\n";
        }

        while (!readyQ2.isEmpty()) {
            str += readyQ2.dequeue().getID() + "\n";
        }

        return str;
    }

    protected void executeOnce(int pid) {
        PCB p = searchAndRemovePCB(pid);
        if (p == null) {
            return;
        } else {
            runQ.add(p);
        }

        SPR[6] = 0;
        SPR[7] = SPR[6];
        checkPages[0] = true;

        int lastExecTime = 0;
        PCB currPCB = runQ.peek();
        this.setRegistors(currPCB);
        int startTime = (int) System.nanoTime();

        if (!currPCB.getRunTwice()) {
            SPR[9] = SPR[0]; // SPR[9] is PC and SPR[0] is CB
        }

        loop:
        while (SPR[9] <= SPR[1]) { // Checking PC with CL
            SPR[10] = memory[SPR[9]]; //SPR[10] is IR
            String instruction = Converter.byteToHex((byte) SPR[10]);
            if (instruction.contentEquals("F3")) {
                System.out.println(currPCB.getName() + " proc has ended -> F3");
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
                    this.bz(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "38":
                    this.bnz(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "39":
                    this.bc(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "3A":
                    this.bs(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "3B":
                    this.jmp(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "3C":
                    this.call(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
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
                    this.shl(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "72":
                    this.shr(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "73":
                    this.rtl(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "74":
                    this.rtr(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "75":
                    this.inc(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "76":
                    this.dec(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "77":
                    this.push(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "78":
                    this.pop(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "F1":
                    this.returnPC();
                    break;
                case "F2": //NOOP: No Operation
                    break;
                default:
                    if (SPR[10] != 0) {
                        System.out.println("Invalid Opcode");
                        break loop;
                    }

            }

            if (checkStack()) {
                return;
            }

            SPR[9]++; // moves to next instrcution
        }

        if (!runQ.isEmpty()) {
            int endTime = (int) System.nanoTime();
            int execTime = endTime - startTime;
            currPCB.setExecTime(execTime);
            currPCB.setWaitTime(lastExecTime);
            this.terminate();
        }

    }

    protected PCB searchAndRemovePCB(int pid) {
        for (PCB p : readyQ1) {
            if (p.getID() == pid) {
                readyQ1.remove(p);
                return p;
            }
        }

        for (PCB p : readyQ2.toPCBArray()) {
            if (p != null) {
                if (p.getID() == pid) {
                    readyQ2.remove((PCB) p);
                    return (PCB) p;
                }
            }
        }

        return null;
    }

    private void setRegistors(PCB currPCB) {

        if (currPCB.getRunTwice()) {
            this.GPR = currPCB.getGPR();
            this.flagRegistor = currPCB.getFlags();
            this.SPR = currPCB.getSPR();
        } else {
            SPR[0] = (short) (currPCB.getcodePage()[0] * FRAME_SIZE); //CB
            SPR[1] = (short) (currPCB.getCodeSize() + SPR[0]);      //CL
            SPR[2] = (short) currPCB.getCodeSize();                 //CC

            SPR[3] = (short) (currPCB.getdataPage()[0] * FRAME_SIZE);//DB
            SPR[4] = (short) (currPCB.getdataSize() + SPR[3]);      //DL
            SPR[5] = (short) currPCB.getdataSize();                 //DC

            SPR[6] = 0;             //SB
            SPR[7] = SPR[6];        //SC
            SPR[8] = FRAME_SIZE - 1;   //SL
        }

    }

    protected void clone(int pid) {
        for (PCB p : readyQ1) {
            if (p.getID() == pid) {
                readyQ1.add(p);
                return;
            }
        }

        for (PCB p : readyQ2.toPCBArray()) {
            if (p.getID() == pid) {
                readyQ2.enqueue((PCB) p);
                return;
            }
        }
        System.out.println("Process not found.");
    }

    protected void block(int pid) {
        PCB p = searchAndRemovePCB(pid);
        if (p != null) {
            blockedQ.add(p);
            System.out.println("Process " + pid + " added to Blocked Queue");
        } else {
            System.out.println("Process not found");
        }
    }

    protected void unblock(int pid) {
        PCB ubPCB = null;
        for (PCB p : blockedQ) {
            if (p.getID() == pid) {
                blockedQ.remove(p);
                ubPCB = p;
                break;
            }
        }
        if (ubPCB == null) {
            System.out.println("Process not found.");
            return;
        }
        if (ubPCB.getPriority() < 16) // Place in Appropriate Queue.
        {
            this.readyQ1.add(ubPCB); // Priority
        } else {
            this.readyQ2.enqueue(ubPCB); //Round-Robin
        }
        System.out.println("Process " + pid + " added to Ready Queue");
    }

    protected void debugOnce(int pid) {
        PCB p = searchAndRemovePCB(pid);
        if (p == null) {
            return;
        } else {
            runQ.add(p);
        }

        SPR[6] = 0;
        SPR[7] = SPR[6];
        checkPages[0] = true;

        PCB currPCB = runQ.peek();
        this.setRegistors(currPCB);

        if (!currPCB.getRunTwice()) {
            SPR[9] = SPR[0]; // SPR[9] is PC and SPR[0] is CB
        }

        wloop:
        while (SPR[9] <= SPR[1]) { // Checking PC with CL
            SPR[10] = memory[SPR[9]]; //SPR[10] is IR
            String instruction = Converter.byteToHex((byte) SPR[10]);
            if (instruction.contentEquals("F3")) {
                System.out.println(currPCB.getName() + " proc has ended -> F3");
                this.terminate();
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
                    this.bz(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "38":
                    this.bnz(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "39":
                    this.bc(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "3A":
                    this.bs(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "3B":
                    this.jmp(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
                    break;
                case "3C":
                    this.call(memory[SPR[9] + 2], memory[SPR[9] + 3]);
                    SPR[9]--;
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
                    this.shl(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "72":
                    this.shr(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "73":
                    this.rtl(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "74":
                    this.rtr(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "75":
                    this.inc(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "76":
                    this.dec(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "77":
                    this.push(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "78":
                    this.pop(memory[SPR[9] + 1]);
                    SPR[9]++;
                    break;
                case "F1":
                    this.returnPC();
                    break;
                case "F2": //NOOP: No Operation
                    break;
                default:
                    if (SPR[10] != 0) {
                        System.out.println("Invalid Opcode");
                        break wloop;
                    }

            }

            if (checkStack()) {
                return;
            }

            SPR[9]++; // moves to next instrcution
            System.out.println("One instruction executed:\n" + this.toString());
            System.out.println("Instruction: " + instruction);
            currPCB.setSPR(SPR);
            currPCB.setGPR(GPR);
            currPCB.setFlags(this.flagRegistor);
            currPCB.setRunTwice(true);
            runQ.remove();

            if (currPCB.getPriority() < 16) // Place in Appropriate Queue.
            {
                this.readyQ1.add(currPCB); // Priority
            } else {
                this.readyQ2.enqueue(currPCB); //Round-Robin
            }
            break;
        }

    }

    protected void debugAll() {
        for (PCB p : readyQ1.toArray(new PCB[5])) {
            if (p != null) {
                this.debugOnce(p.getID());
            }
        }

        for (PCB p : readyQ2.toPCBArray()) {
            if (p != null) {
                this.debugOnce(p.getID());
            }
        }

        System.out.println("One instruction of all the loaded processes executed.");

    }

    protected void showProcess() {
        System.out.println("ReadyQ1:\n" + this.readyQ1.toString());
        System.out.println("ReadyQ2:\n" + this.readyQ2.toString());
    }

    protected void showBlockedProcess() {
        System.out.println("BlockedQ:\n" + this.blockedQ.toString());
    }

    protected void showRunningProcess() {
        System.out.println("RunQ:\n" + this.runQ.toString());
    }

    private PCB searchPCB(int pid) {
        for (PCB p : readyQ1) {
            if (p.getID() == pid) {
                return p;
            }
        }

        for (PCB p : readyQ2.toPCBArray()) {
            if (p != null) {
                if (p.getID() == pid) {
                    return (PCB) p;
                }
            }
        }

        return null;
    }

    protected void showPCB(int pid) {
        System.out.println(searchPCB(pid));
    }

    protected void showPT(int pid) {
        System.out.println(searchPCB(pid).getDataPT());
        System.out.println(searchPCB(pid).getCodePT());
    }

    protected void dump(int pid) {
        PCB p = searchPCB(pid);
        int data_frame = p.getdataPage()[0] * FRAME_SIZE;
        int code_frame_end = p.getcodePage()[0] * FRAME_SIZE + p.getCodeSize();

        String s = "Memory Dump:\n";

        for (int i = data_frame; i <= code_frame_end; i++) {
            s += memory[i] + ",";
        }
        System.out.println(s);
    }

    protected void showFreeFrames() {
        int i = 0;
        for (boolean b : this.checkPages) {
            if (!b) {
                System.out.println(i + " -> " + i * FRAME_SIZE + " ");
            }
            i++;
        }
    }

    protected void showAllocFrames() {
        for (PCB p : readyQ1) {
            {
                System.out.println("ID: " + p.getID());
                System.out.println(p.getCodePT());
                System.out.println(p.getDataPT());
            }
        }

        for (PCB p : readyQ2.toPCBArray()) {
            if (p != null) {
                System.out.println("ID: " + p.getID());
                System.out.println(p.getCodePT());
                System.out.println(p.getDataPT());
            }
        }

    }
}

//--------------------------------------------------------------------------

