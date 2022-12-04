
import java.util.Scanner;

public class CLI {

    public CLI() {
    }

    public void run() {
        VEnv VE = new VEnv();

        System.out.println("16-bit VEnv [Java] [Version 1.0]");
        System.out.println("Run .bin files only.\n");
        Scanner scan = new Scanner(System.in);

        String input;

        while (true) {
            System.out.print("> ");
            input = scan.nextLine();

            if (input.contains("load")) {

                if (input.strip().contentEquals("load")) {
                    System.out.println("No file given.");
                } else {
                    String filename = input.substring(5);
                    VE.readAndLoadBinFile(filename);
                }

            } else if (input.contains("run -p")) {

                int pid = Integer.parseInt(input.substring(7));
                VE.executeOnce(pid);

            } else if (input.contains("debug -p")) {
                int pid = Integer.parseInt(input.substring(9));
                VE.debugOnce(pid);

            } else if (input.contentEquals("debug -a")) {
                VE.debugAll();

            } else if (input.contentEquals("run -a")) {

                VE.executeAll();

            } else if (input.contains("kill -p")) {
                int pid = Integer.parseInt(input.substring(8));
                PCB endPCB = VE.searchAndRemovePCB(pid);
                if (endPCB == null) {
                    System.out.println("Process not found");

                } else {
                    System.out.println("Process Killed:");
                    System.out.println(endPCB);
                }

            } else if (input.contains("clone")) {
                int pid = Integer.parseInt(input.substring(6));
                VE.clone(pid);
                System.out.println("Process Cloned.");
            } else if (input.contains("unblock")) {
                int pid = Integer.parseInt(input.substring(8));
                VE.unblock(pid);

            } else if (input.contains("block")) {
                int pid = Integer.parseInt(input.substring(6));
                VE.block(pid);
                
            } else if (input.contentEquals("shutdown")) {
                System.out.println(VE.shutdown());
                break;

            } else if (input.contentEquals("hibernate")) {
                break;
            } else {
                System.out.println("Invalid Command");
            }
        }

        scan.close();
        System.gc();
    }
}
