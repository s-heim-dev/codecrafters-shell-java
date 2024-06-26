import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static boolean handleInput(String[] args) {
        String command;
        if (args.length > 0) {
            command = args[0];
            if (args.length > 1) {
                args = Arrays.copyOfRange(args, 1, args.length);
            }
            else {
                args = new String[0];
            }
        }
        else {
            command = "";
        }

        switch(command) {
            case "exit":
                return Main.handleExit(args);
            case "echo":
                return Main.handleEcho(args);
            default:
                return Main.handleNotFound(command);
        }
    }

    public static boolean handleNotFound(String command) {
        System.out.printf("%s: command not found\n", command);
        return true;
    }

    public static boolean handleExit(String[] args) {
        return !(args.length == 1 && args[0].equals("0"));
    }

    public static boolean handleEcho(String[] args) {
        System.out.println(String.join(" ", args));
        return true;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String input;
        do {
            System.out.print("$ ");
            input = scanner.nextLine();
        }
        while(Main.handleInput(input.split(" ")));

        scanner.close();
        System.exit(0);
    }
}
