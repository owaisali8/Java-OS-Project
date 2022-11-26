
/**
 *
 * @author Owais, Hamza and Yunus
 */
import java.util.Hashtable;

public class PageTable {
    // first integer is logical address, second is physical address

    private final Hashtable<Integer, Integer> pageTable = new Hashtable<>();
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

    @Override
    public String toString() {
        String s = "";
        for (Integer i : pageTable.keySet()) {
            s += "Page " + i + " -> Frame " + pageTable.get(i);
        }
        return s;
    }

    public int size() {
        return pageTable.size();
    }

    public void add(int p, int f) {
        pageTable.put(p, f);
    }

    public int[] getKeys() {
        int[] r = new int[this.pageTable.size()];
        int j = 0;
        for (Integer i : pageTable.keySet()) {
            r[j] = i;
            j++;
        }
        return r;
    }
}
