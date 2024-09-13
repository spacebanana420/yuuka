package yuuka;

import java.io.File;

public class main {
  public static void main(String[] args) {
    boolean askedHelp = parseOptions(args);
    if (askedHelp) {
      System.out.println(getHelpMessage());
      return;
    }
    boolean ranTask = parseTasks(args);
    if (!ranTask) {
      System.out.println(getHelpMessage_small());}
  }

  private static boolean parseOptions(String[] args) {
    boolean printed_help = false;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-h") || args[i].equals("--help") || args[i].equals("help")) {
        printed_help = true;
        break;
      }
      else if (args[i].equals("--verbose")) {
        globalvariables.VERBOSE = true;
      }
      else if (args[i].equals("--silent")) {
        globalvariables.SILENT = true;
      }
      else if (args[i].equals("--ignore-lib")) {
        globalvariables.INGORE_LIB = true;
      }
      else if (args[i].equals("--release") && i < args.length-1 && isInt(args[i+1])) {
        globalvariables.RELEASE_TARGET = args[i+1];
      }
      else if (
        args[i].equals("--output")
        && i < args.length-1
        && args[i+1].length() > 0
        && args[i+1].charAt(0) != '-'
        && !isArgumentTask(args[i+1])
        ) {
        globalvariables.PROGRAM_NAME = args[i+1];
      }
    }
    return printed_help;
  }

  private static boolean parseTasks(String[] args) { //unfinished of course
    for (int i = 0; i < args.length; i++) {
      switch(args[i]) {
        case "init":
          initializeProject();
          return true;
        case "build":
          compiler.compile();
          return true;
        case "package":
          var class_files = compiler.compile();
          compiler.createJAR("test.jar", "main", class_files); 
          fileops.deleteClassFiles("build");
          return true;
        case "run":
          return true;
        case "clean":
          fileops.deleteClassFiles("src");
          fileops.deleteClassFiles("build");
          fileops.deleteClassFiles("lib");
          return true;
      }
    }
    return false;
  }

  private static boolean isInt(String num) {
    try{
      Integer.parseInt(num);
      return true;
    }
    catch(NumberFormatException e) {return false;}

  }

  private static void initializeProject() {
    new File("src").mkdir();
    new File("lib").mkdir();
    new File("build").mkdir();
  }

  private static boolean isArgumentTask(String arg) {
    return
      arg.equals("init")
      || arg.equals("build")
      || arg.equals("package")
      || arg.equals("run")
      || arg.equals("clean");
  }

  private static String getHelpMessage() {
    return
      "Yuuka help screen"
      + "\nBasic usage: yuuka [command] [options]"
      + "\n\nAvailable commands:"
      + "\n  * init - creates a project file structure"
      + "\n  * build - compiles your project"
      + "\n  * package - compiles your project and packages it into an executable JAR"
      + "\n  * run - compiles and runs your project"
      + "\n  * clean - deletes all .class files"
      
      + "\n\nAvailable CLI arguments:"
      + "\n  -h or --help or help - opens this menu"
      + "\n  --release [number] - sets the target Java release for compilation"
      + "\n  --ingore-lib - ignores all library JARs that are in lib"
      + "\n  --silent - does not print any message during execution of a task"
      + "\n  --verbose - displays more information on what's happening"
      + "\n  --output - sets the name of the compiled JAR";
  }

  private static String getHelpMessage_small() {
    return
      "Yuuka help screen"
      + "\nBasic usage: yuuka [command] [options]"
      + "\n\nType -h, --help or help to check what you can do";
  }
}

class globalvariables {
  public static boolean VERBOSE = false;
  public static boolean SILENT = false;
  public static boolean INGORE_LIB = false;
  public static String RELEASE_TARGET = "";
  public static String PROGRAM_NAME = "program.jar";
}