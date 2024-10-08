package yuuka;

import java.io.File;
import yuuka.config.yuukaConfig;

public class main {
  public static void main(String[] args) {
    yuukaConfig.parseConfig(yuukaConfig.readConfig());

    boolean askedHelp = parseOptions(args);
    if (askedHelp) {
      System.out.println(getHelpMessage());
      return;
    }
    boolean ranTask = parseTasks(args);
    if (!ranTask) {System.out.println(getHelpMessage_small());}
  }

  private static boolean parseOptions(String[] args) {
    boolean printed_help = false;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--")) {break;}
      if (args[i].equals("-h") || args[i].equals("--help") || args[i].equals("help")) {
        printed_help = true;
        break;
      }
      else if (isOption(args[i], "-v", "--verbose")) {
        globalvariables.VERBOSE = true;
      }
      else if (isOption(args[i], "-s", "--silent")) {
        globalvariables.SILENT = true;
      }
      else if (isOption(args[i], "-i", "--ignore-lib")) {
        globalvariables.INGORE_LIB = true;
      }
      else if (
        (isOption(args[i], "-r", "--release"))
        && hasArgumentValue(args, i) && misc.isInt(args[i+1])
        )
      {
        globalvariables.RELEASE_TARGET = args[i+1];
      }
      else if (
        (isOption(args[i], "-m", "--main"))
        && hasArgumentValue(args, i)
        && !isArgumentTask(args[i+1])
        )
      {
        globalvariables.MAIN_CLASS = args[i+1];
      }
      else if (
        (isOption(args[i], "-o", "--output"))
        && hasArgumentValue(args, i)
        && !isArgumentTask(args[i+1])
        )
      {
        globalvariables.PROGRAM_NAME = args[i+1];
      }
      else if (isOption(args[i], "-nw", "--no-warnings")) {
        globalvariables.DISABLE_WARNINGS = true;
      }
      else if (
        (isOption(args[i], "--graal-path"))
        && hasArgumentValue(args, i)
        && !isArgumentTask(args[i+1])
        )
      {
        var binaryf = new File(args[i+1]);
        if (binaryf.isFile() && binaryf.canExecute()) {globalvariables.GRAAL_PATH = args[i+1];}
      }
    }
    return printed_help;
  }

  private static boolean parseTasks(String[] args) {
    for (int i = 0; i < args.length; i++) {
      switch(args[i]) {
        case "--":
          return true;
        case "init":
          initializeProject();
          stdout.print("The directories src, build and lib have been created");
          return true;
        case "build":
          if (projectHasNoSource()) {return true;}
          stdout.print("Compiling project");
          stdout.print_verbose("Main class is " + globalvariables.MAIN_CLASS);
          compiler.compile();
          return true;
        case "buildnative":
          runTask_package();
          String[] nativecmd = new String[]
          {
            globalvariables.GRAAL_PATH, "--no-fallback", "--static", "-O3", "-jar",
            globalvariables.PROGRAM_NAME,
            "-o", misc.removeExtension(globalvariables.PROGRAM_NAME)
          };
          stdout.print("Building native binary with GraalVM");
          stdout.print_verbose("GraalVM command:", nativecmd);
          int exitstatus = process.runProcess(nativecmd, "build");
          if (exitstatus == -1) {stdout.print("Failed to run the GraalVM binary, GraalVM needs to be installed in order to build native binaries.");}
          else if (exitstatus > 0) {stdout.print("The compilation failed!");}
          return true;
        case "package":
          runTask_package();
          return true;
        case "packagelib":
          if (projectHasNoSource()) {return true;}
          stdout.print("Compiling project");
          compiler.compile();

          stdout.print("Creating library JAR \"" + globalvariables.PROGRAM_NAME + "\"");
          compiler.createJAR(globalvariables.PROGRAM_NAME, globalvariables.MAIN_CLASS, true);

          stdout.print("Cleaning up class files");
          fileops.deleteClassFiles("build");
          fileops.deleteClassFiles("lib");
          return true;
        case "run":
          if (!new File("build/" + globalvariables.MAIN_CLASS + ".class").isFile()) {
            if (projectHasNoSource()) {return true;}
            stdout.print("Compiling project");
            stdout.print_verbose("Main class is " + globalvariables.MAIN_CLASS);
            compiler.compile();
          }
          stdout.print("Running program");
          compiler.runProgram(args);
          return true;
        case "clean":
          stdout.print("Cleaning up all class files");
          fileops.deleteClassFiles("src");
          fileops.deleteClassFiles("build");
          fileops.deleteClassFiles("lib");
          fileops.deleteClassFiles("test");
          return true;
        case "install":
          if (System.getProperty("os.name").contains("Windows")) {
            stdout.print("The install task is not available for Windows!");
            return true;
          }
          runTask_package();
          installer.installProgram();
          return true;
        case "runtest":
          if (!hasArgumentValue(args, i) || isArgumentTask(args[i+1])) {
            stdout.print
            (
              "The task \"runtest\" requires an argument following it!"
              + "\nExample: \"yuuka runtest filetest\" to launch the file test/filetest.java"
            );
            return true;
          }
          String source_file =
            (misc.checkFileExtension(args[i+1], ".java"))
            ? args[i+1]
            : args[i+1] + ".java";
          
          if (!new File("test/"+source_file).isFile()) {
            stdout.print("The file \"test/" + source_file + "\" does not exist!");
            return true;
          }
          new File("test").mkdir();
          boolean result = tests.runTest(source_file, args);
          if (!result) {stdout.print("Error during building/running the test!");}
          return true;
        case "listtest":
          tests.printTestFiles();
          return true;
      }
    }
    return false;
  }

  private static void initializeProject() {
    new File("src").mkdir();
    new File("lib").mkdir();
    new File("build").mkdir();
    new File("test").mkdir();
    yuukaConfig.createConfig();
  }

  private static boolean hasArgumentValue(String[] args, int i) {
    return i < args.length-1 && args[i+1].length() > 0 && args[i+1].charAt(0) != '-';
  }

  private static boolean isOption(String arg, String opt1, String opt2) {return arg.equals(opt1) || arg.equals(opt2);}
  private static boolean isOption(String arg, String opt) {return arg.equals(opt);}

  private static boolean isArgumentTask(String arg) {
    return
      arg.equals("init")
      || arg.equals("build")
      || arg.equals("buildnative")
      || arg.equals("package")
      || arg.equals("packagelib")
      || arg.equals("run")
      || arg.equals("runtest")
      || arg.equals("clean")
      || arg.equals("install")
      || arg.equals("listtest")
    ;
  }

  private static String getHelpMessage() {
    return
      "Yuuka help screen (version 0.4)"
      + "\nBasic usage: yuuka [command] [options]"
      + "\nPassing arguments to program execution: yuuka run/runtest [options] -- [arguments]"
      + "\n\nAvailable commands:"
      + "\n  * init - creates a project file structure"
      + "\n  * build - compiles your project"
      + "\n  * package - compiles your project and packages it into an executable JAR"
      + "\n  * packagelib - compiles your project and packages it into a library JAR"
      + "\n  * buildnative - compiles your project into a native binary (requires GraalVM)"
      + "\n  * run - compiles and runs your project"
      + "\n  * runtest - runs a test source file"
      + "\n  * listtest - lists available test files"
      + "\n  * clean - deletes all .class files"
      + "\n  * install - builds and installs your program (Unix-like only)"
      
      + "\n\nAvailable CLI arguments:"
      + "\n  -h, --help, help - opens this menu"
      + "\n  -m (--main) [class] - sets the main class"
      + "\n  -r (--release) [number] - sets the target Java release for compilation"
      + "\n  -i (--ingore-lib) - ignores all library JARs that are in lib"
      + "\n  -s (--silent) - does not print any message during execution of a task"
      + "\n  -v (--verbose) - displays more information on what's happening"
      + "\n  -o (--output) - sets the name of the compiled JAR"
      + "\n  -nw (--no-warnings) - disables compiler warnings"
      + "\n  --graal-path - sets a custom path for the GraalVM \"native-image\" binary"
      ;
  }

  private static String getHelpMessage_small() {
    return
      "Yuuka version 0.4"
      + "\nBasic usage: yuuka [command] [options]"
      + "\n\nRun \"yuuka -h\" to see what you can do";
  }

  private static boolean projectHasNoSource() {
    var f = new File("src");
    if (!f.isDirectory() || f.list() == null) {
      stdout.print("Your project has no \"src\" directory or it is empty!");
      return true;
    }
    return false;
  }

  private static void runTask_package() {
    if (projectHasNoSource()) {return;}
    stdout.print("Compiling project");
    stdout.print_verbose("Main class is " + globalvariables.MAIN_CLASS);
    compiler.compile();

    stdout.print("Creating executable JAR \"" + globalvariables.PROGRAM_NAME + "\"");
    compiler.createJAR(globalvariables.PROGRAM_NAME, globalvariables.MAIN_CLASS, false);
    
    stdout.print("Cleaning up class files");
    fileops.deleteClassFiles("build");
    fileops.deleteClassFiles("lib");
  }
}