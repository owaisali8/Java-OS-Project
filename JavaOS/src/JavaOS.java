
/**
 *
 * @author Owais, Hamza and Yunus
 */
public class JavaOS {

    private short[] GPR, SPR;
    private byte[] memory;
    boolean[] flagRegistor;

    public JavaOS() { // INtializing our arrays
        short[] GPR = new short[16]; // R0-R15
        short[] SPR = new short[16];
        boolean[] flagRegistor = new boolean[16];
        byte[] memory = new byte[65536]; // memory size should be 64KB 2^16
    }

    // Implentation of register to registor instructions 
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

    //Implementation of Register-Immediate Instructions...
}
