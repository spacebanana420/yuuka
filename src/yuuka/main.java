package yuuka;

import yuuka.config.yuukaConfig;
import yuuka.cli.parser;

public class main {
  public static void main(String[] args) {
    yuukaConfig.parseConfig(yuukaConfig.readConfig());

    boolean askedHelp = parser.parseOptions(args);
    if (askedHelp) {
      System.out.println(getHelpMessage());
      return;
    }
    boolean ranTask = parser.parseTasks(args);
    if (!ranTask) {System.out.println(getHelpMessage_small());}
  }

  private static String getHelpMessage() {
    return
      "Yuuka help screen (version 0.5)"
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
      + "\n  * install [jar path] - installs an existing JAR file"
      
      + "\n\nAvailable CLI arguments:"
      + "\n  -h, --help, help - opens this menu"
      + "\n  -m (--main) [class] - sets the main class"
      + "\n  -r (--release) [number] - sets the target Java release for compilation"
      + "\n  -i (--ingore-lib) - ignores all library JARs that are in lib"
      + "\n  -s (--silent) - enables silent output, disables printing"
      + "\n  -v (--verbose) - prints more information"
      + "\n  -d (--debug) - prints even further information"
      + "\n  -o (--output) - sets the name of the compiled JAR"
      + "\n  -nw (--no-warnings) - disables compiler warnings"
      + "\n  --graal-path - sets a custom path for the GraalVM \"native-image\" binary"
      ;
  }

  private static String getHelpMessage_small() {
    return
      "Yuuka version 0.5"
      + "\nBasic usage: yuuka [command] [options]"
      + "\n\nRun \"yuuka -h\" to see what you can do";
  }
}