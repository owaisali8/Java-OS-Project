
/**
 *
 * @author Owais, Hamza and Yunus
 */

public class Main {
    public static void main(String[] args){
        /* For phase 1 Files
        
        var main = new VEnv("p0.txt");
        System.out.println(main);
        System.out.println(main.showMem(20));
        
        main = new VEnv("p1.byte");
        System.out.println(main);
        System.out.println(main.showMem(20));
        */
        
        // For phase 2
        
        VEnv main = new VEnv("p5.bin");
        main.executeAll();
        //System.out.println(main.showMem(500));
    }
}
