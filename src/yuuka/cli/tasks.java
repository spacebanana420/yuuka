package yuuka.cli;

import java.io.File;

import yuuka.config.yuukaConfig;
import yuuka.stdout;
import yuuka.globalvariables;
import yuuka.compiler;
import yuuka.fileops;
import yuuka.misc;
import yuuka.process;
import yuuka.tests;

public class tasks {
  public static void initializeProject() {
    new File("src").mkdir();
    new File("lib").mkdir();
    new File("build").mkdir();
    new File("test").mkdir();
    yuukaConfig.createConfig();
  }

  public static int runTask_package() {
    if (parser.projectHasNoSource()) {return -3;}
    stdout.print("Compiling project");
    stdout.print_verbose("Main class is " + globalvariables.MAIN_CLASS);
    int result = 0;
    result = compiler.compile();
    if (result != 0) {return result;}

    stdout.print("Creating executable JAR \"" + globalvariables.PROGRAM_NAME + "\"");
    result = compiler.createJAR(globalvariables.PROGRAM_NAME, globalvariables.MAIN_CLASS, false);
    
    stdout.print("Cleaning up class files");
    fileops.deleteClassFiles("build");
    fileops.deleteClassFiles("lib");
    return result;
  }

  public static void buildNativeBinary() {
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
  }

  public static void runTest(String args[], String source_arg) {
    String source_file =
      (misc.checkFileExtension(source_arg, ".java"))
      ? source_arg
      : source_arg + ".java";
  
    if (!new File("test/"+source_file).isFile()) {
      stdout.print("The file \"test/" + source_file + "\" does not exist!");
      return;
    }
    new File("test").mkdir();
    boolean result = tests.runTest(source_file, args);
    if (!result) {stdout.print("Error during building/running the test!");}
  }
}
