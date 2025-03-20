package yuuka.cli;

import java.io.File;
import java.util.ArrayList;

import yuuka.global;
import yuuka.io.stdout;
import yuuka.io.fileops;
import yuuka.misc;
import yuuka.jdk.tests;
import yuuka.jdk.installer;

public class cli {
  public static boolean askedForHelp(String[] args, int parse_break) {
    int help_i = parser.findArgument(args, parse_break, "-h", "--help");
    if (help_i == -1) {return false;}
    
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
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      global.MAIN_CLASS = args[i+1];
    }
  }
  static void assignValue_output(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-o", "--output");
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      global.setProgramName(args[i+1]);
    }
  }
  static void assignValue_graalvm(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-gp", "--graal-path");
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      var binaryf = new File(args[i+1]);
      if (binaryf.isFile() && binaryf.canExecute()) {global.GRAAL_PATH = args[i+1];}
    }
  }
  static void assignValue_install(String[] args, int parse_break) {
    int i = parser.findArgument(args, parse_break, "-ip", "--install-path");
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      var installf = new File(args[i+1]);
      if (installf.isDirectory() && installf.canWrite()) {global.INSTALL_PATH = args[i+1];}
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
  
  public static int findParseBreak(String[] args) {
    for (int i = 0; i < args.length; i++) {if (args[i].equals("--")) {return i;}}
    return args.length;
  }

  public static String[] getExecArgs(String[] args) {
    ArrayList<String> exec_args = new ArrayList<>();
    int args_start = -1;
    
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--") && i < args.length-1) {args_start = i+1; break;}
    }
    if (args_start == -1) {return new String[0];}
    
    for (int i = args_start; i < args.length; i++) {exec_args.add(args[i]);}
    return exec_args.toArray(new String[0]);
  }
}
