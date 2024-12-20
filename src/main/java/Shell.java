import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Shell {
    String command;
    String[] args;
    String[] paths;
    String pwd;
    String home;
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
        this.home = System.getenv("HOME");
        this.pwd = Path.of("").toAbsolutePath().toString();
    }

    public boolean handle(String args) {
        this.args = this.handleArgs(args);

        if (this.args.length > 0) {
            this.command = this.args[0];
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

    private String[] handleArgs(String input) {
        List<String> arguments = new ArrayList<>();

        boolean insideSingleQuote = false;
        boolean insideDoubleQuote = false;

        StringBuilder arg = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);

            if (current == '\'' && !insideDoubleQuote) {
                insideSingleQuote = !insideSingleQuote;
            }
            else if (current == '\"' && !insideSingleQuote) {
                insideDoubleQuote = !insideDoubleQuote;
            }
            else if (current == ' ' && !insideSingleQuote && !insideDoubleQuote) {
                if (arg.length() > 0) {
                    arguments.add(arg.toString());
                    arg = new StringBuilder();
                }
            }
            else if (current == '\\' && !insideSingleQuote && !insideDoubleQuote) {
                arg.append(input.charAt(++i));
            }
            else {
                arg.append(current);
            }
        }

        if (!insideSingleQuote && !insideDoubleQuote && arg.length() > 0) {
            arguments.add(arg.toString());
        }

        return arguments.toArray(String[]::new);
    }

    private String findPath(String command) {
        for (String path : this.paths) {
            path = path + "/" + command;
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                return path;
            }
        }

        File f = new File(this.pwd + "/" + command);
        if (f.exists() && !f.isDirectory()) {
            return this.pwd;
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
            ProcessBuilder processBuilder = new ProcessBuilder(this.args);
            processBuilder.directory(new File(this.pwd));
            process = processBuilder.start();
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

        String path = this.args[1].replace("~", this.home);

        if (!path.startsWith("/")) {
            path = this.changeRelativePath(this.pwd, path);
        }

        File f = new File(path);

        if (!f.exists()) {
            System.out.printf("cd: %s: No such file or directory\n", this.args[1]);
            return true;
        }

        this.pwd = path;
        return true;
    }

    private String changeRelativePath(String pwd, String path) {
        if (path.startsWith("./")) {
            return pwd + path.substring(1);
        }

        String[] currentPath = pwd.split("/");
        String[] relativePath = path.split("/");

        int i = currentPath.length, j = 0;
        while (j < relativePath.length && relativePath[j].equals("..")) {
            i--;
            j++;
        }

        currentPath = Arrays.copyOfRange(currentPath, 0, i);
        relativePath = Arrays.copyOfRange(relativePath, j, relativePath.length);

        path = "/" + String.join("/", currentPath) + "/" + String.join("/", relativePath);
        path = path.replaceAll("//", "/");

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }
}
