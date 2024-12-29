package yuuka.cli;

import java.io.File;
import yuuka.globalvariables;
import yuuka.stdout;
import yuuka.fileops;
import yuuka.misc;
import yuuka.jdk.tests;
import yuuka.installer;

public class cli {
  static boolean askedForHelp(String[] args, int parse_break) {
    int help_i = findArgument(args, "-h", parse_break);
    if (help_i == -1) {help_i = findArgument(args, "--help", parse_break);}
    if (help_i == -1) {return false;}
    
    String help_message;
    if (hasArgumentValue(args, help_i)) {
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
  
  public static boolean parseOptions(String[] args) {
    int parse_break = findParseBreak(args);
    
    if (askedForHelp(args, parse_break)) {return true;}
    
    if (hasArgument(args, parse_break, "-d", "--debug")) {globalvariables.PRINT_LEVEL = 3;}
    else if (hasArgument(args, parse_break, "-v", "--verbose")) {globalvariables.PRINT_LEVEL = 2;}
    else if (hasArgument(args, parse_break, "-s", "--silent")) {globalvariables.PRINT_LEVEL = 0;}
    
    if (hasArgument(args, parse_break, "-i", "--ignore-lib")) {globalvariables.INGORE_LIB = true;}
    if (hasArgument(args, parse_break, "-is", "--include-src")) {globalvariables.TESTS_INCLUDE_PROJECT = true;}
    if (hasArgument(args, parse_break, "-nw", "--no-warnings")) {globalvariables.DISABLE_WARNINGS = true;}
    
    String target;
    target = getJavaTarget(args, parse_break, 0);
    if (target != null) {globalvariables.setReleaseTarget(target);}
    
    target = getJavaTarget(args, parse_break, 1);
    if (target != null) {globalvariables.setSourceTarget(target);}
    
    target = getJavaTarget(args, parse_break, 2);
    if (target != null) {globalvariables.setClassTarget(target);}

    for (int i = 0; i < parse_break; i++) { //legacy parsing
      if
      (
        (isOption(args[i], "-m", "--main"))
        && hasArgumentValue(args, i)
        && !isArgumentTask(args[i+1])
      )
      {globalvariables.MAIN_CLASS = args[i+1];}
      
      else if
      (
        (isOption(args[i], "-o", "--output"))
        && hasArgumentValue(args, i)
        && !isArgumentTask(args[i+1])
      )
      {globalvariables.setProgramName(args[i+1]);}
      
      else if
      (
        (isOption(args[i], "-gp", "--graal-path"))
        && hasArgumentValue(args, i)
        && !isArgumentTask(args[i+1])
      )
      {
        var binaryf = new File(args[i+1]);
        if (binaryf.isFile() && binaryf.canExecute()) {globalvariables.GRAAL_PATH = args[i+1];}
      }
      else if
      (
        isOption(args[i], "-ip", "--install-path")
        && hasArgumentValue(args, i)
        && !isArgumentTask(args[i+1])
      )
      {
        var installf = new File(args[i+1]);
        if (installf.isDirectory() && installf.canWrite()) {globalvariables.INSTALL_PATH = args[i+1];}
      }
    }
    return false;
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

          if (hasArgumentValue(args, i) && new File(args[i+1]).isFile()
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
          if (hasArgumentValue(args, i)) {
            tasks.uninstallProgram(args[i+1]);
          }
          else {stdout.print("Failed to run uninstall task! You must specify the name of the program to uninstall!");}
          return true;
        case "test":
          if (!hasArgumentValue(args, i) || isArgumentTask(args[i+1])) {
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
        (isOption(args[i], target_args[mode], target_args_long[mode]))
        && hasArgumentValue(args, i) && misc.isInt(args[i+1])
      )
      {return args[i+1];}
    }
    return null;
  }
  
  static int findParseBreak(String[] args) {
    for (int i = 0; i < args.length; i++) {if (args[i].equals("--")) {return i;}}
    return args.length;
  }

  static int findArgument(String[] args, String arg, int parse_break) {
    for (int i = 0; i < parse_break; i++) {
      if (args[i].equals(arg)) {return i;}
    }
    return -1;
  }
  
  static String getArgumentValue(String[] args, String arg, int parse_break) {
    int i = findArgument(args, arg, parse_break);
    if (i == -1 || i == args.length-1) {return null;}
    String value = args[i+1];
    if (value == null || value.charAt(0) == '-') {return null;}
    return value; 
  }

  static boolean hasArgument(String[] args, String arg, int parse_break) {
    return findArgument(args, arg, parse_break) != -1;
  }
  
  static boolean hasArgument(String[] args, int parse_break, String... arg) {
    for (String a : arg) {
      if (findArgument(args, a, parse_break) != -1) {return true;}
    }
    return false;
  }
  
  static boolean hasArgumentValue(String[] args, int i) {
    return i < args.length-1 && args[i+1].length() > 0 && args[i+1].charAt(0) != '-';
  }

  static boolean isArgumentTask(String arg) {
    return
      arg.equals("init")
      || arg.equals("build")
      || arg.equals("build-native")
      || arg.equals("package")
      || arg.equals("packagelib")
      || arg.equals("run")
      || arg.equals("test")
      || arg.equals("clean")
      || arg.equals("install")
      || arg.equals("install-native")
      || arg.equals("tests")
      || arg.equals("uninstall")
    ;
  }

  static boolean isOption(String arg, String opt1, String opt2) {return arg.equals(opt1) || arg.equals(opt2);}
  static boolean isOption(String arg, String opt) {return arg.equals(opt);}
}
