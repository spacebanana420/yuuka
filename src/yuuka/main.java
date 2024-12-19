package yuuka;

import yuuka.config.yuukaConfig;
import yuuka.cli.cli;

public class main {
  public static void main(String[] args) {
    yuukaConfig.parseConfig("build.yuuka");

    boolean askedHelp = cli.parseOptions(args);
    if (askedHelp) {
      System.out.println(getHelpMessage());
      return;
    }
    boolean ranTask = cli.parseTasks(args);
    if (!ranTask) {System.out.println(getHelpMessage_small());}
  }

  private static String getHelpMessage() {
    return
      "Yuuka help screen (version 1.0.2)"
      + "\nBasic usage: yuuka [command] [options]"
      + "\nPassing arguments to program execution: yuuka run/test [options] -- [arguments]"
      + "\n\nAvailable commands:"
      + "\n  * init - creates a project file structure"
      + "\n  * build - compiles your project"
      + "\n  * package - compiles your project into an executable JAR"
      + "\n  * packagelib - compiles your project into a library JAR"
      + "\n  * buildnative - compiles your project into a native binary (requires GraalVM)"
      + "\n  * run - compiles and runs your project"
      + "\n  * test - runs a test source file"
      + "\n  * tests - lists available test files"
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
      + "\n  -gp (--graal-path) - sets a custom path for the GraalVM \"native-image\" binary"
      + "\n  -ip (--install-path) - sets a custom installation path for the \"install\" task"
      + "\n  -is (--include-src) - imports the project's source code when running tests"
      ;
  }

  private static String getHelpMessage_small() {
    return
      "Yuuka version 1.0.2"
      + "\nBasic usage: yuuka [command] [options]"
      + "\n\nRun \"yuuka -h\" to see what you can do";
  }
}
