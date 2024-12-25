package yuuka.cli;

public class help {
  
    private static String title() {return "Yuuka version 1.1";}
  
    public static String getHelpMessage() {
    return
      title()
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
      + "\n  * unintsall [name] - uninstalls a program, removes its script and JAR file"
      
      + "\n\nAvailable CLI arguments:"
      + "\n  -h (--help) - opens this menu"
      + "\n  -h (--help) [command] - opens the documentation for a specific command"
      + "\n  -m (--main) [class] - sets the main class"
      + "\n  -r (--release) [number] - sets the target Java release for source reading and compilation"
      + "\n  -src (--source) [number] - sets the target Java version for reading the source code"
      + "\n  -t (--target) [number] - sets the target Java version for compilation"
      + "\n  -i (--ingore-lib) - ignores all library JARs that are in lib"
      + "\n  -s (--silent) - enables silent output, disables printing"
      + "\n  -v (--verbose) - prints more information"
      + "\n  -d (--debug) - prints even further information"
      + "\n  -o (--output) [JAR filename] - sets the name of the compiled JAR"
      + "\n  -nw (--no-warnings) - disables compiler warnings"
      + "\n  -gp (--graal-path) - sets a custom path for the GraalVM \"native-image\" binary"
      + "\n  -ip (--install-path) - sets a custom installation path for the \"install\" task"
      + "\n  -is (--include-src) - imports the project's source code when running tests"
      ;
  }

  public static String getHelpMessage_small() {
    return
      title()
      + "\nBasic usage: yuuka [command] [options]"
      + "\n\nRun \"yuuka -h\" to see what you can do";
  }
  
  public static String help_init() {
    return
      title()
      + "\ninit command"
      + "\n\nThe init command creates a new project structure:"
      + "\n * build        Your compiled class and JAR files are stored here"
      + "\n * lib          You can store library JARs here"
      + "\n * src          This directory stores your source code"
      + "\n * test         You can store java source code tests as well as any executable binary or script"
      + "\n * build.yuuka  The declarative file, overrides default Yuuka settings to suit your project"
      + "\n * libs.yuuka   If you require fetching libraries remotely, you configure them here"
    ;
  }
  
  public static String help_build() {
    return
      title()
      + "\nbuild command"
      + "\n\nThe build command compiles your source code as .class files stored in /build. It calls the \"javac\" program to build your source files, and keeps in mind all the library files you have locally or in libs.yuuka."
      + "\n\nThere are multiple CLI arguments you can pass to customize the compilation process, here's a few examples:"
      + "\n * \"yuuka build -r 11\" - builds and packages your project, targetting Java 11. Java 11's feature-set and standard library are considered for your source parsing, and the compiled class files are compatible with Java 11 or newer."
      + "\n * \"yuuka build -main projectname/mainfile\" - if you did not configure the path to the main source file in build.yuuka and Yuuka could not automatically find it, you can manually specify its path."
    ;
  }

  public static String help_package() {
    return
      title()
      + "\npackage command"
      + "\n\nThe package command compiles your source code into an executable JAR. It calls the \"jar\" program to package your program, and includes the library files you have locally or in libs.yuuka."
      + "\nThere are multiple CLI arguments you can pass to customize the compilation and packaging processes, here's a few examples:"
      + "\n * \"yuuka package -r 11\" - builds your project, targetting Java 11. Java 11's feature-set and standard library are considered for your source parsing, and the compiled class files are compatible with Java 11 or newer."
      + "\n * \"yuuka package -main projectname/mainfile -o myprogram.jar\" - if you did not configure the path to the main source file in build.yuuka and Yuuka could not automatically find it, you can manually specify its path. When the main class is not configured and automatically detected, the JAR's name defaults to program.jar, and so you can also override this."
    ;
  }
  
  
  public static String help_packagelib() {
    return
      title()
      + "\npackagelib command"
      + "\n\nThe packagelib command compiles your source code into a library JAR. It calls the \"jar\" program to package your library, and includes all other library files you have locally or in libs.yuuka."
      + "\nThis JAR file, unlike the typical JAR file you produce with the package command, is not executable. It does not have a main class assigned and is only meant to be used as a library, to be imported in other software projects."
      + "\nYou can pass all the same CLI arguments that you pass to the package and build commands."
    ;
  }
  
  public static String help_buildnative() {
    return
      title()
      + "\nbuildnative command"
      + "\n\nThe buildnative command compiles your source code into native binary executable. It depends on GraalVM being installed on your system and uses it's calls the \"native-image\" program to build your executable file."
      + "\nYou can specify a manual path to GraalVM's \"native-image\" binary with the  \"-gp\" or \"--graal-path\" argument."
      + "\nYou can pass all the same CLI arguments that you pass to the package and build commands."
    ;
  }
  
  public static String help_run() {
    return
      title()
      + "\nrun command"
      + "\n\nThe run command compiles your source code and then executes it."
      + "\nTo pass any CLI arguments to your program, you need to use the argument \"--\" to tell Yuuka where to know where they are:"
      + "\n\"yuuka run -r 11 -- -argument1 -argument2\" - All arguments after \"--\" are passed to your program."
    ;
  }
  
  public static String help_test() {
    return
      title()
      + "\ntest command"
      + "\n\nThe test command executes any tests you have inside the test directory."
      + "\nTo run tests written in Java, they must have a main function so they are executable. They can also import other source files that do not have a main function."
      + "\nYou can also import your source code from your tests, as long as you enable this feature in build.yuuka, or you use the -is or --include-src argument."
      + "\n\nFor a test file named mytest.java, you can run it with \"yuuka test mytest\" or \"yuuka test mytest.java\"."
      + "\nFor an executable test script named mytest.sh, you can run it with \"yuuka test mytest\"."
      + "\nTo pass any CLI arguments to your program, you need to use the argument \"--\" to tell Yuuka where to know where they are:"
      + "\n\"yuuka test mytest -- -argument1 -argument2\" - All arguments after \"--\" are passed to your program."
    ;
  }
  
  public static String help_tests() {
    return
      title()
      + "\ntests command"
      + "\n\nThe tests command lists all the compatible test files inside your test directory."
      + "\nFor a test file to be valid, it must either be a java source file with a main class, or an executable file of any kind (binary, script, etc)."
    ;
  }
  
  public static String help_clean() {
    return
      title()
      + "\nclean command"
      + "\n\nThe clean command deletes all .class files found in your project. It's common to use this command after running \"yuuka build\""
    ;
  }
  
  public static String help_install() {
    return
      title()
      + "\ninstall command"
      + "\n\nThe install command builds your project into an executable JAR and then intsalls it in your system, so you can run the command anywhere by writing its name, just like any other CLI application. It does this by storing your JAR program inside a newly-created \"jars\" directory, and then it creates a shell script that executes your program and passes all arguments to it. The script uses /bin/sh as its shell and interpreter."
      + "\nThis command does not work on Windows, as it specifically relies on traditional Unix paths and shell."
      + "\n\nBy default, Yuuka autodetects the installation path based on the typical Unix paths that are part of the system's $PATH:"
      + "\n * Running Yuuka as root: The default installation path is \"/usr/local/bin\". This path comes with your system, is already assigned to $PATH and will work for all users in your system, and so this is the recommended method."
      + "\n * Running Yuuka as user (Linux, OpenBSD, NetBSD, etc): if you run Yuuka without root, it assumes \"~/.local/bin\". This path normally doesn't exist and must be created and added to the $PATH by you."
      + "\n * Running Yuuka as user (FreeBSD): Yuuka will default to \"~/bin\", since this path is in the system's $PATH variable for all users you create."
      +"\n\nYou can also manually specify the installation path through build.yuuka or the \"-ip\" and \"--install-path\" arguments."
      + "\nIf you pass the path to a JAR file manually, Yuuka will install that program instead of yours:"
      +"\n\"yuuka install someprogram.jar\""
    ;
  }
  
  public static String help_uninstall() {
    return
      title()
      + "\nuninstall command"
      + "\n\nThe uninstall command uninstalls any program which you installed before. If you use multiple installation paths, remember to specify the one you want Yuuka to assume. You need to pass the name of the program to uninstall it:"
      + "\n\"yuuka uninstall myprogram\""
      + "\n\nIf you are uninstalling from a root directory, such as \"/usr/local/bin\", remember to run Yuuka as root."
    ;
  }
}
