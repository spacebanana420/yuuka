package yuuka;

import java.io.File;
import java.util.ArrayList;

import yuuka.io.stdout;
import yuuka.io.fileops;
import yuuka.misc;
import yuuka.jdk.tests;
import yuuka.jdk.installer;
import yuuka.cli.*;
import yuuka.config.yuukaConfig;

import java.io.File;

public class main {
  public static void main(String[] args) {
    int parse_break = cli.findParseBreak(args);
    if (cli.askedForHelp(args, parse_break)) {return;}
    
    yuukaConfig.parseConfig("build.yuuka");
    cli.assignGlobalValues(args, parse_break);

    boolean ranTask = runTasks(args, parse_break);
    boolean printed_version = cli.printVersion(args, parse_break);
    if (!ranTask && !printed_version) {System.out.println(help.getHelpMessage_small());}
  }
  
  static boolean runTasks(String[] args, int parse_break) {
    for (int i = 0; i < parse_break; i++) {
      switch(args[i]) {
        case "init":
          tasks.initializeProject();
          return true;
        case "build":
          tasks.build();
          return true;
        case "build-native":
          tasks.buildNativeBinary();
          return true;
        case "package":
          tasks.packageJAR();
          return true;
        case "packagelib":
          tasks.packageLib();
          return true;
        case "run":
          tasks.runProgram(args);
          return true;
        case "clean":
          tasks.cleanProject();
          return true;
        case "install":
          if (unsupportedTask()) {return true;}
          if (parser.hasArgumentValue(args, i) && new File(args[i+1]).isFile()
            && misc.checkFileExtension(args[i+1], ".jar"))
            {installer.installProgram(args[i+1]);}
          else {
            int result = tasks.packageJAR();
            if (result == 0) {installer.installProgram();}
          }
          return true;
        case "install-native":
          if (unsupportedTask()) {return true;}
          int result = tasks.buildNativeBinary();
          if (result == 0) {installer.installProgram_native();}
          return true;
        case "uninstall":
          if (parser.hasArgumentValue(args, i)) {tasks.uninstallProgram(args[i+1]);}
          else {stdout.print("Failed to run uninstall task! You must specify the name of the program to uninstall!");}
          return true;
        case "test":
          if (!parser.hasArgumentValue(args, i)) {
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
  
  static boolean unsupportedTask() {
    if (System.getProperty("os.name").contains("Windows")) {
      stdout.print("The install task is not available for Windows!");
      return true;
    }
    return false;
  }
}
