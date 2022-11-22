
/**
 *
 * @author Owais, Hamza and Yunus
 */
public class PCB {

    private int activePage = 0;
    private int id;
    private int priority;
    private String name;
    private int codeSize;
    private int dataSize;
    private int segmentSize;
    private PageTable PT;
    private short[] GPR = new short[16];
    private short[] SPR = new short[16];
    private float waitTime;
    private float execTime;
    private int[] flagReg = new int[16];

    public boolean isTerminated() {
        return false;
    }

    public PCB() {
    }

}
