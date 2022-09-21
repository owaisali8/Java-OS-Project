
/**
 *
 * @author Owais, Hamza and Yunus
 */
public class JavaOS {
    
    private short GPR;

    public JavaOS(){ // INtializing our arrays
        short[] GPR = new short[16]; // R0-R15
        short[] SPR = new short[16];
        boolean [] flagRegistor = new boolean [16];
        byte[] memory = new byte[65536]; // memory size should be 64KB 2^16
    }
    
    public void mov(int r1, int r2){
        GPR[r1] = 10;
    }
    
    
}
