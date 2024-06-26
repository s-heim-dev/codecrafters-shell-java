import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;

public class Shell {
    String command;
    String[] args;
    String[] paths;
    Path pwd;
    Builtin buildinCommand;

    private enum Builtin {
        cd,
        echo,
        exit,
        pwd,
        type
    }

    public Shell() {
        this.command = "";
        this.args = new String[0];
        this.paths = System.getenv("PATH").split(":");
        this.pwd = Path.of("").toAbsolutePath();
    }

    public boolean handle(String[] args) {
        this.args = args;

        if (args.length > 0) {
            this.command = args[0];
        }
        else {
            this.command = "";
        }

        switch(this.command) {
            case "exit":
                return this.handleExit();
            case "echo":
                return this.handleEcho();
            case "type":
                return this.handleType();
            case "pwd":
                return this.handlePwd();
            case "cd":
                return this.handleCD();
            default:
                return this.execute();
        }
    }

    private String findPath(String command) {
        for (String path : this.paths) {
            path = path + "/" + command;
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                return path;
            }
        }

        return null;
    }

    private boolean handleNotFound() {
        System.out.printf("%s: command not found\n", this.command);
        return true;
    }

    private boolean handleExit() {
        return !(this.args.length == 2 && this.args[1].equals("0"));
    }

    private boolean handleEcho() {
        System.out.println(String.join(" ", Arrays.copyOfRange(this.args, 1, this.args.length)));
        return true;
    }

    private boolean handleType() {
        if (this.args.length < 2) return true;

        String arg = this.args[1];

        try {
            Builtin type = Builtin.valueOf(arg);
            System.out.printf("%s is a shell builtin\n", type);
            return true;
        }
        catch(IllegalArgumentException ex) { }

        String path = this.findPath(arg);
        if (path != null) {
            System.out.printf("%s is %s\n", arg, path);
            return true;
        }

        System.out.printf("%s: not found\n", arg);
        return true;
    }

    private boolean execute() {
        String path = this.findPath(this.command);
        if (path == null) return this.handleNotFound();

        this.args[0] = path;

        Process process;

        try {
            process = new ProcessBuilder(this.args).start();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        InputStream inputStream = process.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean handlePwd() {
        System.out.println(this.pwd);
        return true;
    }

    private boolean handleCD() {
        if (this.args.length < 2) return true;

        File f = new File(this.args[1]);

        if (!f.exists()) {
            System.out.printf("cd: %s: No such file or directory\n", this.args[1]);
            return true;
        }

        this.pwd = Path.of(this.args[1]);
        return true;
    }
}
