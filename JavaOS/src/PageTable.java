
/**
 *
 * @author Owais, Hamza and Yunus
 */
import java.util.Hashtable;

public class PageTable {
    // first integer is logical address, second is physical address

    private Hashtable<Integer, Integer> pageTable = new Hashtable<Integer, Integer>();
    public int activePage = 0;

    public int frameIndex(int pno) {
        return pageTable.get(pno);
    }

    public void print() {
        System.out.println();
        for (int i = 0; i < pageTable.size(); i++) {
            System.out.println("page " + i + ": frame " + pageTable.get(i));
        }
    }

    public int size() {
        return pageTable.size();
    }
}
