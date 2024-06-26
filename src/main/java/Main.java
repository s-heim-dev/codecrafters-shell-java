import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        String input;
        Shell shell = new Shell();
        do {
            System.out.print("$ ");
            input = scanner.nextLine();
        }
        while(shell.handle(input.split(" ")));

        scanner.close();
        System.exit(0);
    }
}
