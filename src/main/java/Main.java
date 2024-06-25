import java.util.Scanner;

public class Main {
    public static boolean handleInput(String[] args) {
        String command;
        if (args.length > 0) {
            command = args[0];
        }
        else {
            command = "";
        }

        if (command.equals("exit")) {
            return Main.handleExit(args);
        }

        System.out.printf("%s: command not found\n", command);
        return false;
    }

    public static boolean handleExit(String[] args) {
        return args.length == 2 && args[0].equals("exit")&& args[1].equals("0");
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String input;
        do {
            System.out.print("$ ");
            input = scanner.nextLine();
        }
        while(!Main.handleInput(input.split(" ")));

        scanner.close();
        System.exit(0);
    }
}
