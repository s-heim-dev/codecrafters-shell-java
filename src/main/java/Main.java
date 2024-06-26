import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String input;
        Shell shell;
        do {
            System.out.print("$ ");
            input = scanner.nextLine();
            shell = new Shell(input.split(" "));
        }
        while(shell.handle());

        scanner.close();
        System.exit(0);
    }
}
