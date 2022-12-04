
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CLI {

    public CLI() {
    }

    public void run() throws IOException {
        VEnv VE = new VEnv();

        FileWriter log = new FileWriter("Log.txt");

        log.write("16-bit VEnv [Java] [Version 1.0]\n");
        log.write("Run .bin files only.\n");

        System.out.println("16-bit VEnv [Java] [Version 1.0]");
        System.out.println("Run .bin files only.\n");

        Scanner scan = new Scanner(System.in);

        String input;

        while (true) {
            System.out.print("> ");
            input = scan.nextLine();
            log.write("\n" + input + "\n");

            if (input.contains("load")) {

                if (input.strip().contentEquals("load")) {
                    System.out.println("No file given.");
                    log.write("No file given.");
                } else {
                    String filename = input.substring(5);
                    VE.readAndLoadBinFile(filename);
                    log.write(filename + " loaded");
                }

            } else if (input.contentEquals("")) {
                System.out.println();

            } else if (input.contains("run -p")) {
                int pid = Integer.parseInt(input.substring(7));
                VE.executeOnce(pid);
                log.write(VE.toString());
                log.write(VE.showFlags());

            } else if (input.contains("debug -p")) {
                int pid = Integer.parseInt(input.substring(9));
                VE.debugOnce(pid);
                log.write(VE.toString());
                log.write(VE.showFlags());

            } else if (input.contentEquals("debug -a")) {
                VE.debugAll();
                log.write("One instruction of all the loaded processes executed.");
                log.write(VE.toString());
                log.write(VE.showFlags());

            } else if (input.contentEquals("run -a")) {
                VE.executeAll();
                log.write(VE.toString());
                log.write(VE.showFlags());

            } else if (input.contains("kill -p")) {
                int pid = Integer.parseInt(input.substring(8));
                PCB endPCB = VE.searchAndRemovePCB(pid);
                if (endPCB == null) {
                    System.out.println("Process not found");

                } else {
                    System.out.println("Process Killed:");
                    System.out.println(endPCB);
                    log.write("Process Kileed");
                    log.write(endPCB.toString());
                }

            } else if (input.contains("clone")) {
                int pid = Integer.parseInt(input.substring(6));
                VE.clone(pid);
                System.out.println("Process Cloned.");
                log.write("Process Cloned.");

            } else if (input.contains("unblock")) {
                int pid = Integer.parseInt(input.substring(8));
                VE.unblock(pid);
                log.write("Process Unblocked");

            } else if (input.contains("block")) {
                int pid = Integer.parseInt(input.substring(6));
                VE.block(pid);
                log.write("Process Blocked");

            } else if (input.contentEquals("list -a")) {
                log.write(VE.showProcess());
                log.write(VE.showBlockedProcess());
                log.write(VE.showRunningProcess());

            } else if (input.contentEquals("list -b")) {
                log.write(VE.showBlockedProcess());

            } else if (input.contentEquals("list -r")) {
                log.write(VE.showProcess());

            } else if (input.contentEquals("list -e")) {
                log.write(VE.showRunningProcess());

            } else if (input.contains("display -p")) {
                int pid = Integer.parseInt(input.substring(11));
                log.write(VE.showPCB(pid).toString());

            } else if (input.contains("display -m")) {
                int pid = Integer.parseInt(input.substring(11));
                VE.showPT(pid);

            } else if (input.contains("dump")) {
                int pid = Integer.parseInt(input.substring(5));
                log.write(VE.dump(pid));

            } else if (input.contentEquals("frames -f")) {
                VE.showFreeFrames();

            } else if (input.contentEquals("mem")) {
                System.out.println(VE.showMem());
                log.write(VE.showMem());

            } else if (input.contentEquals("frames -a")) {
                VE.showAllocFrames();

            } else if (input.contentEquals("registers")) {
                System.out.println(VE);
                System.out.println(VE.showFlags());
                log.write(VE.toString());
                log.write(VE.showFlags());

            } else if (input.contentEquals("shutdown")) {
                String s = VE.shutdown();
                System.out.println(s);
                log.write(s);
                break;

            } else if (input.contentEquals("hibernate")) {
                break;

            } else if (input.contentEquals("exit")){
                System.out.println("CLI exited");
                break;
            }
            else {
                System.out.println("Invalid Command");
                log.write("Invalid Command");
            }
        }

        log.close();
        scan.close();
        System.gc();
    }
}
