import java.util.Scanner;

public class Main {
    public static void handleInput(String input) {
        System.out.printf("%s: command not found\n", input);
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            Main.handleInput(input);
        }
    }
}
