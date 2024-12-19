package yuuka.cli;

import java.io.File;
import yuuka.globalvariables;
import yuuka.stdout;
import yuuka.fileops;
import yuuka.misc;
import yuuka.jdk.tests;
import yuuka.installer;

public class cli {
public static boolean parseOptions(String[] args) {
    int parse_break = findParseBreak(args);
    
    if (hasArgument(args, parse_break, "-h", "--help")) {return true;}
    
    if (hasArgument(args, parse_break, "-d", "--debug")) {globalvariables.PRINT_LEVEL = 3;}
    else if (hasArgument(args, parse_break, "-v", "--verbose")) {globalvariables.PRINT_LEVEL = 2;}
    else if (hasArgument(args, parse_break, "-s", "--silent")) {globalvariables.PRINT_LEVEL = 0;}
    
    if (hasArgument(args, parse_break, "-i", "--ignore-lib")) {globalvariables.INGORE_LIB = true;}
    if (hasArgument(args, "--include-src", parse_break)) {globalvariables.TESTS_INCLUDE_PROJECT = true;}
    if (hasArgument(args, parse_break, "-nw", "--no-warnings")) {globalvariables.DISABLE_WARNINGS = true;}

    for (int i = 0; i < parse_break; i++) { //legacy parsing
      if
      (
        (isOption(args[i], "-r", "--release"))
        && hasArgumentValue(args, i) && misc.isInt(args[i+1])
      )
      {globalvariables.RELEASE_TARGET = args[i+1];}
      
      else if
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
          tasks.runTask_package();
          tasks.buildNativeBinary();
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
        case "test":
          if (!hasArgumentValue(args, i) || isArgumentTask(args[i+1])) {
            stdout.print
            (
              "The task \"test\" requires an argument following it!"
              + "\nExample: \"yuuka runtest filetest\" to launch the file test/filetest.java"
            );
            return true;
          }
          if (!projectHasNoSource() && globalvariables.TESTS_INCLUDE_PROJECT) {tasks.packageLib();}
          tasks.runTest(args, args[i+1]);
          return true;
        case "tests":
          tests.printTestFiles();
          return true;
      }
    }
    return false;
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
      || arg.equals("buildnative")
      || arg.equals("package")
      || arg.equals("packagelib")
      || arg.equals("run")
      || arg.equals("test")
      || arg.equals("clean")
      || arg.equals("install")
      || arg.equals("tests")
    ;
  }

  static boolean isOption(String arg, String opt1, String opt2) {return arg.equals(opt1) || arg.equals(opt2);}
  static boolean isOption(String arg, String opt) {return arg.equals(opt);}

  static boolean projectHasNoSource() {
    var f = new File("src");
    if (!f.isDirectory() || f.list() == null) {
      stdout.print("Your project has no \"src\" directory or it is empty!");
      return true;
    }
    return false;
  }
}
