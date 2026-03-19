package yuuka.cli;

import java.io.File;
import java.util.ArrayList;

import yuuka.io.*;
import yuuka.misc;


//General CLI parsing functions for checking for specific arguments and their values
//Using the CLI parser, it sets Yuuka's configuration
public class cli {
  public static boolean askedForHelp(String[] args, int parse_break) {
    int help_i = parser.findArgument(args, parse_break, "-h", "--help");
    if (help_i == -1) return false;
    
    String help_message;
    if (parser.hasArgumentValue(args, help_i)) {
      String command = args[help_i+1];
      switch (command) {
        case "init":
          help_message = help.help_init(); break;
        case "build":
          help_message = help.help_build(); break;
        case "package":
          help_message = help.help_package(); break;
        case "packagelib":
          help_message = help.help_packagelib(); break;
        case "build-native":
          help_message = help.help_buildnative(); break;
        case "run":
          help_message = help.help_run(); break;
        case "test":
          help_message = help.help_test(); break;
        case "create-test":
          help_message = help.help_create_test(); break;
        case "tests":
          help_message = help.help_tests(); break;
        case "clean":
          help_message = help.help_clean(); break;
        case "install":
          help_message = help.help_install(); break;
        case "install-native":
          help_message = help.help_install_native(); break;
        case "uninstall":
          help_message = help.help_uninstall(); break;
        default: help_message = help.getHelpMessage();
      }
    }
    else help_message = help.getHelpMessage();
    
    stdout.print(help_message);
    return true;
  }

  //The argument -- defines the end of Yuuka CLI arguments and the start of CLI arguments to pass to the programs or tests you run
  public static int findParseBreak(String[] args) {
    for (int i = 0; i < args.length; i++) {if (args[i].equals("--")) {return i;}}
    return args.length;
  }

  //The arguments to pass to a program/test/script Yuuka executes with "yuuka run" or "yuuka test"
  public static String[] getExecArgs(String[] args, int parse_break) {
    if (parse_break == -1) {return new String[0];}
    ArrayList<String> exec_args = new ArrayList<>();
    for (int i = parse_break+1; i < args.length; i++) {exec_args.add(args[i]);}
    return exec_args.toArray(new String[0]);
  }
  
  public static boolean printVersion(String[] args, int parse_break) {
    if (parser.hasArgument(args, parse_break, "-V", "--version")) {
      stdout.print(help.title());
      return true;
    }
    return false;
  }

  public static String getMainClass(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-m", "--main");
    if (i == -1) return null;
    
    String err = args[i] + " must be followed by the path to the project's main class!\nFor example yuuka/main";
    if (checkForValue(i, args, err)) return args[i+1];
    return null;
  }

  public static String getOutputFilename(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-o", "--output");
    if (i == -1) return null;
    
    String err = args[i] + " must be followed by the name of the JAR file!\nFor example yuuka.jar";
    if (checkForValue(i, args, err)) return args[i+1];
    return null;
  }

  public static String getGraalPath(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-gp", "--graal-path");
    if (i == -1) return null;
    
    String err = args[i] + " must be followed by the path to the GraalVM's native-image binary!\nFor example /usr/local/bin/native-image";
    if (checkForValue(i, args, err)) {
      File binaryf = new File(args[i+1]);
      if (binaryf.isFile() && binaryf.canExecute()) return args[i+1];
      stdout.error("The GraalVM native-image binary at path " + args[i+1] + " does not exist or is not executable!");
      return null;
    }
    return null;
  }

  public static String getInstallPath(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-ip", "--install-path");
    if (i == -1) return null;
    
    String err = args[i] + " must be followed by the installation path!\nFor example /usr/local/bin";
    if (checkForValue(i, args, err)) {
      File installf = new File(args[i+1]);
      if (installf.isDirectory() && installf.canWrite()) return args[i+1];
      stdout.error("The installation path " + args[i+1] + " does not exist or cannot be written to!");
      return null;
    }
    return null;
  }
  
  private static boolean checkForValue(int i, String[] args, String error_message) {
    if (!parser.hasArgumentValue(args, i)) {stdout.error(error_message); return false;}
    return true;
  }
}
