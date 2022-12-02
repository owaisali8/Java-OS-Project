
import java.util.Scanner;

public class CLI {

    public CLI() {
    }

    public void run() {
        VEnv VE = new VEnv();

        System.out.println("16-bit VEnv [Java] [Version 1.0]\n");
        Scanner scan = new Scanner(System.in);

        String input;

        while (true) {
            System.out.print("> ");
            input = scan.nextLine();

            if (input.contains("load")) {
                String filename = input.substring(5);
                VE.readAndLoadBinFile(filename);
            } else if (input.contains("run -p")) {
                int pid = Integer.parseInt(input.substring(7));
                System.out.println(pid);
            } else if (input.contentEquals("shutdown")) {
                System.out.println(VE.shutdown());
                break;
            } else if (input.contentEquals("hibernate")) {
                break;
            } else {
                System.out.println("Invalid Command");
            }
        }
    }
}
