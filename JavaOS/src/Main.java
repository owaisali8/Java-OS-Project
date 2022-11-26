
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
        // All the files are loaded into memory and then executed according to their priority.
        VEnv main = new VEnv();
        main.readAndLoadBinFile("p5.bin");
        main.readAndLoadBinFile("p5 - Copy.bin");
        main.readAndLoadBinFile("power.bin");
        main.readAndLoadBinFile("noop.bin");
        main.readAndLoadBinFile("large0.bin");
        main.readAndLoadBinFile("flags.bin");
        main.readAndLoadBinFile("sfull.bin");
        main.executeAll();
    }
}
