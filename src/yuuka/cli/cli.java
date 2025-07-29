package yuuka.cli;

import java.io.File;
import java.util.ArrayList;

import yuuka.global;
import yuuka.io.*;
import yuuka.misc;

public class cli {
  public static boolean askedForHelp(String[] args, int parse_break) {
    int help_i = parser.findArgument(args, parse_break, "-h", "--help");
    if (help_i == -1) {return false;}
    
    String help_message;
    if (parser.hasArgumentValue(args, help_i)) {
      String command = args[help_i+1];
        help_message = switch (command) {
          case "init" -> help.help_init();
          case "build" -> help.help_build();
          case "package" -> help.help_package();
          case "packagelib" -> help.help_packagelib();
          case "build-native" -> help.help_buildnative();
          case "run" -> help.help_run();
          case "test" -> help.help_test();
          case "create-test" -> help.help_create_test();
          case "tests" -> help.help_tests();
          case "clean" -> help.help_clean();
          case "install" -> help.help_install();
          case "install-native" -> help.help_install_native();
          case "uninstall" -> help.help_uninstall();
          default -> help.getHelpMessage();
        };
    }
    else {help_message = help.getHelpMessage();}
    
    System.out.println(help_message);
    return true;
  }
  
  public static boolean printVersion(String[] args, int parse_break) {
    if (parser.hasArgument(args, parse_break, "-V", "--version")) {
      System.out.println(help.title());
      return true;
    }
    return false;
  }
  
  public static void assignGlobalValues(String[] args, int parse_break) {
    var t = new Thread(() -> {
      if (parser.hasArgument(args, parse_break, "-d", "--debug")) {global.PRINT_LEVEL = 3;}
      else if (parser.hasArgument(args, parse_break, "-v", "--verbose")) {global.PRINT_LEVEL = 2;}
      else if (parser.hasArgument(args, parse_break, "-s", "--silent")) {global.PRINT_LEVEL = 0;}
      
      if (parser.hasArgument(args, parse_break, "-i", "--ignore-lib")) {global.INGORE_LIB = true;}
      if (parser.hasArgument(args, parse_break, "-is", "--include-src")) {global.TESTS_INCLUDE_PROJECT = true;}
      if (parser.hasArgument(args, parse_break, "-nw", "--no-warnings")) {global.DISABLE_WARNINGS = true;}
      if (parser.hasArgument(args, parse_break, "-0", "--no-compress")) {global.DISABLE_JAR_COMPRESSION = true;}
      
      String target = getJavaTarget(args, parse_break, 0);
      if (target != null) {global.setReleaseTarget(target);}
      target = getJavaTarget(args, parse_break, 1);
      if (target != null) {global.setSourceTarget(target);}
      target = getJavaTarget(args, parse_break, 2);
      if (target != null) {global.setClassTarget(target);}    
    });
    t.start();
    
    assignValue_main(args, parse_break);
    assignValue_output(args, parse_break);
    assignValue_graalvm(args, parse_break);
    assignValue_install(args, parse_break);

    try{t.join();}
    catch(InterruptedException e) {e.printStackTrace();}
  }
  
  static void assignValue_main(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-m", "--main");
    if (i == -1) {return;}
    
    String err = args[i] + " must be followed by the path to the project's main class!\nFor example yuuka/main";
    if (checkForValue(i, args, err)) {global.MAIN_CLASS = args[i+1];}
  }
  static void assignValue_output(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-o", "--output");
    if (i == -1) {return;}
    
    String err = args[i] + " must be followed by the name of the JAR file!\nFor example yuuka.jar";
    if (checkForValue(i, args, err)) {global.setProgramName(args[i+1]);}
  }
  
  static void assignValue_graalvm(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-gp", "--graal-path");
    if (i == -1) {return;}
    
    String err = args[i] + " must be followed by the path to the GraalVM's native-image binary!\nFor example /usr/local/bin/native-image";
    if (checkForValue(i, args, err)) {
      File binaryf = new File(args[i+1]);
      if (binaryf.isFile() && binaryf.canExecute()) {global.GRAAL_PATH = args[i+1];}
      else {stdout.error("The GraalVM native-image binary at path " + args[i+1] + " does not exist or is not executable!");}
    }
  }
  static void assignValue_install(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-ip", "--install-path");
    if (i == -1) {return;}
    
    String err = args[i] + " must be followed by the installation path!\nFor example /usr/local/bin";
    if (checkForValue(i, args, err)) {
      File installf = new File(args[i+1]);
      if (installf.isDirectory() && installf.canWrite()) {global.INSTALL_PATH = args[i+1];}
      else {stdout.error("The installation path " + args[i+1] + " does not exist or cannot be written to!");}
    }
  }
  
  //mode must be 0, 1 or 2 for release, source and target
  static String getJavaTarget(String[] args, int parse_break, int mode) {
    if (mode < 0 || mode > 2) {return null;}
    String[] target_args = new String[]{"-r", "-src", "-t"};
    String[] target_args_long = new String[]{"--release", "--source", "--target"};
    
    for (int i = 0; i < parse_break; i++) {
      if
      (
        (parser.isOption(args[i], target_args[mode], target_args_long[mode]))
        && parser.hasArgumentValue(args, i) && misc.isInt(args[i+1])
      )
      {return args[i+1];}
    }
    return null;
  }
  
  //The argument -- defines the end of Yuuka CLI arguments and the start of CLI arguments to pass to the programs or tests you run
  public static int findParseBreak(String[] args) {
    for (int i = 0; i < args.length; i++) {if (args[i].equals("--")) {return i;}}
    return args.length;
  }

  public static String[] getExecArgs(String[] args, int parse_break) {
    if (parse_break == -1) {return new String[0];}
    ArrayList<String> exec_args = new ArrayList<>();
    for (int i = parse_break+1; i < args.length; i++) {exec_args.add(args[i]);}
    return exec_args.toArray(new String[0]);
  }
  
  private static boolean checkForValue(int i, String[] args, String error_message) {
    if (!parser.hasArgumentValue(args, i)) {stdout.error(error_message); return false;}
    return true;
  }
}
