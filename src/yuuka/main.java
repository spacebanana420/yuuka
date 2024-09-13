package yuuka;

public class main {
  public static void main(String[] args) {
    
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
      + "\n--release [number] - sets the target Java release for your software"
      + "\n--ingore-lib - ignores all library JARs that are in lib";
  }
}