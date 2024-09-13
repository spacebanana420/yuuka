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
      if (args[i] == "-h" || args[i] == "--help" || args[i] == "help") {
        printed_help = true;
        break;
      }
      else if (args[i] == "--verbose") {
        globalvariables.VERBOSE = true;
      }
      else if (args[i] == "--ignore-lib") {
        globalvariables.INGORE_LIB = true;
      }
      else if (args[i] == "--release" && i < args.length-1 && isInt(args[i+1])) {
        globalvariables.RELEASE_TARGET = args[i+1];
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
          return true;
        case "package":
          return true;
        case "run":
          return true;
        case "clean":
          fileops.deleteClassFiles("src");
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
      + "\n  --verbose - displays more information on what's happening";
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
  public static boolean INGORE_LIB = false;
  public static String RELEASE_TARGET = "";
}