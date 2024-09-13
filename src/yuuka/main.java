package yuuka;

import java.io.File;

public class main {
  public static void main(String[] args) {
    
  }

  private static void parseCLI(String[] args) {
    for (String arg : args) {
      switch(arg) {
        case "init":
          initializeProject();
          continue;
        case "build":
        case "package":
        case "run":
        case "clean":
          fileops.deleteClassFiles("src");
          continue;
      }
    }
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
      + "\n  --release [number] - sets the target Java release for compilation"
      + "\n  --ingore-lib - ignores all library JARs that are in lib"
      + "\n  --verbose - displays more information on what's happening";
  }
}