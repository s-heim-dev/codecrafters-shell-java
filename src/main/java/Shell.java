import java.util.Arrays;
import java.util.Optional;

public class Shell {
    String command;
    String[] args;
    Builtin buildinCommand;

    public Shell() {
        this.command = "";
        this.args = new String[0];
    }

    public Shell(String[] args) {
        this();

        if (args.length > 0) {
            this.command = args[0];

            if (args.length > 1) {
                this.args = Arrays.copyOfRange(args, 1, args.length);
            }
        }
    }


    public boolean handle() {
        switch(this.command) {
            case "exit":
                return this.handleExit();
            case "echo":
                return this.handleEcho();
            case "type":
                return this.handleType();
            default:
                return this.handleNotFound();
        }
    }

    private boolean handleNotFound() {
        System.out.printf("%s: command not found\n", this.command);
        return true;
    }

    private boolean handleExit() {
        return !(this.args.length == 1 && this.args[0].equals("0"));
    }

    private boolean handleEcho() {
        System.out.println(String.join(" ", this.args));
        return true;
    }

    private boolean handleType() {
        try {
            Builtin type = Builtin.valueOf(this.args[0]);
            System.out.printf("%s is a shell builtin\n", type);
        }
        catch(IllegalArgumentException ex) {
            System.out.printf("%s: not found\n", this.args[0]);
        }
        return true;
    }
}
