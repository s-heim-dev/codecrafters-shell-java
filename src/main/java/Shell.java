import java.io.File;
import java.util.Arrays;

public class Shell {
    String command;
    String[] args;
    String[] paths;
    Builtin buildinCommand;

    public Shell() {
        this.command = "";
        this.args = new String[0];
        this.paths = System.getenv("PATH").split(":");
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
            return true;
        }
        catch(IllegalArgumentException ex) { }

        for (String path : this.paths) {
            path = path + "/" + this.args[0];
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                System.out.printf("%s is %s\n", this.args[0], path);
                return true;
            }
        }

        System.out.printf("%s: not found\n", this.args[0]);
        return true;
    }
}
