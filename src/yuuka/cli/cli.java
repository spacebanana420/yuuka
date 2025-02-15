package yuuka.cli;

import java.io.File;

import yuuka.globalvariables;
import yuuka.stdout;
import yuuka.fileops;
import yuuka.misc;
import yuuka.jdk.tests;
import yuuka.jdk.installer;

public class cli {
  public static boolean askedForHelp(String[] args, int parse_break) {
    int help_i = parser.findArgument(args, parse_break, "-h");
    if (help_i == -1) {help_i = parser.findArgument(args, parse_break, "--help");}
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
  
  public static void parseOptions(String[] args, int parse_break) {
    var t1 = new Thread(() -> {
      if (parser.hasArgument(args, parse_break, "-d", "--debug")) {globalvariables.PRINT_LEVEL = 3;}
      else if (parser.hasArgument(args, parse_break, "-v", "--verbose")) {globalvariables.PRINT_LEVEL = 2;}
      else if (parser.hasArgument(args, parse_break, "-s", "--silent")) {globalvariables.PRINT_LEVEL = 0;}
      
      if (parser.hasArgument(args, parse_break, "-i", "--ignore-lib")) {globalvariables.INGORE_LIB = true;}
      if (parser.hasArgument(args, parse_break, "-is", "--include-src")) {globalvariables.TESTS_INCLUDE_PROJECT = true;}
      if (parser.hasArgument(args, parse_break, "-nw", "--no-warnings")) {globalvariables.DISABLE_WARNINGS = true;}
      
      String target = getJavaTarget(args, parse_break, 0);
      if (target != null) {globalvariables.setReleaseTarget(target);}
      target = getJavaTarget(args, parse_break, 1);
      if (target != null) {globalvariables.setSourceTarget(target);}
      target = getJavaTarget(args, parse_break, 2);
      if (target != null) {globalvariables.setClassTarget(target);}    
    });
    
    var t2 = new Thread(() -> {
      assignValue_main(args, parse_break);
      assignValue_output(args, parse_break);
      assignValue_graalvm(args, parse_break);
      assignValue_install(args, parse_break);
    });
    t1.start(); t2.start();
    try{t1.join(); t2.join();}
    catch(InterruptedException e) {e.printStackTrace();}
  }
  
  static void assignValue_main(String[] args, int parse_break) {
    int i = parser.findArguemnt(args, parse_break, "-m", "--main");
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      globalvariables.MAIN_CLASS = args[i+1];
    }
  }
  static void assignValue_output(String[] args, int parse_break) {
    int i = parser.findArguemnt(args, parse_break, "-o", "--output");
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      globalvariables.setProgramName(args[i+1]);
    }
  }
  static void assignValue_graalvm(String[] args, int parse_break) {
    int i = parser.findArguemnt(args, parse_break, "-gp", "--graal-path");
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      var binaryf = new File(args[i+1]);
      if (binaryf.isFile() && binaryf.canExecute()) {globalvariables.GRAAL_PATH = args[i+1];}
    }
  }
  static void assignValue_install(String[] args, int parse_break) {
    int i = parser.findArguemnt(args, parse_break, "-ip", "--install-path");
    if (i != -1 && parser.hasArgumentValue(args, i)) {
      var installf = new File(args[i+1]);
      if (installf.isDirectory() && installf.canWrite()) {globalvariables.INSTALL_PATH = args[i+1];}
    }
  }

  public static boolean parseTasks(String[] args) {
    for (int i = 0; i < args.length; i++) {
      switch(args[i]) {
        case "--":
          return true;
        case "init":
          tasks.initializeProject();
          return true;
        case "build":
          tasks.build();
          return true;
        case "buildnative":
          if (tasks.runTask_package() == 0)
            {tasks.buildNativeBinary();}
          return true;
        case "package":
          tasks.runTask_package();
          return true;
        case "packagelib":
          tasks.packageLib();
          return true;
        case "run":
          tasks.runProgram(args);
          return true;
        case "clean":
          stdout.print("Cleaning up all class files");
          fileops.deleteClassFiles("src");
          fileops.deleteBuildFiles("build");
          fileops.deleteClassFiles("lib");
          fileops.deleteClassFiles("test");
          return true;
        case "install":
          if (System.getProperty("os.name").contains("Windows")) {
            stdout.print("The install task is not available for Windows!");
            return true;
          }

          if (parser.hasArgumentValue(args, i) && new File(args[i+1]).isFile()
            && misc.checkFileExtension(args[i+1], ".jar"))
            {installer.installProgram(args[i+1]);}
          else {
            int result = tasks.runTask_package();
            if (result == 0) {installer.installProgram();}
          }
          return true;
        case "install-native":
          if (System.getProperty("os.name").contains("Windows")) {
            stdout.print("The install-native task is not available for Windows!");
            return true;
          }
          int result = tasks.runTask_package();
          if (result == 0) {result = tasks.buildNativeBinary();}
          if (result == 0) {installer.installProgram_native();}
          return true;
        case "uninstall":
          if (parser.hasArgumentValue(args, i)) {tasks.uninstallProgram(args[i+1]);}
          else {stdout.print("Failed to run uninstall task! You must specify the name of the program to uninstall!");}
          return true;
        case "test":
          if (!parser.hasArgumentValue(args, i)) {
            stdout.print
            (
              "The task \"test\" requires an argument following it!"
              + "\nExample: \"yuuka runtest filetest\" to launch the file test/filetest.java"
              + "\nYou can also run other executable files: \"yuuka runtest somescript\" to launch the file test/somescript.sh"
            );
            return true;
          }
          tasks.runTest(args, args[i+1]);
          return true;
        case "tests":
          tests.printTestFiles();
          return true;
      }
    }
    return false;
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
}
