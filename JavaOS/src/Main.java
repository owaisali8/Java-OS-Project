
/**
 *
 * @author Owais, Hamza and Yunus
 */

public class Main {
    public static void main(String[] args){
        var main = new VEnv("p0.txt");
        System.out.println(main);
        System.out.println(main.showMem(20));
        
        main = new VEnv("p1.byte");
        System.out.println(main);
        System.out.println(main.showMem(20));
    }
}
