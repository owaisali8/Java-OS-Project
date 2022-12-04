
import java.io.Serializable;
import java.util.Comparator;

class CustomComparator implements Comparator<PCB>, Serializable{

    @Override
    public int compare(PCB p1, PCB p2) {

        int value = p1.getPriority();
        int value2 = p2.getPriority();

        if (value > value2) {
            return 1;
        } else if (value < value2) {
            return -1;
        } else {
            return 0;
        }
    }

}
