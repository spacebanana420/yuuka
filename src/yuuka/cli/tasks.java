package yuuka.cli;

import java.io.File;

import yuuka.config.yuukaConfig;
import yuuka.config.libconf;
import yuuka.config.confreader;

import yuuka.libfetch.MavenLibrary;
import yuuka.libfetch.CustomLibrary;

import yuuka.stdout;
import yuuka.globalvariables;
import yuuka.fileops;
import yuuka.misc;

import yuuka.jdk.process;
import yuuka.jdk.compiler;
import yuuka.jdk.tests;

public class tasks {
  public static void initializeProject() {
    new File("src").mkdir();
    new File("lib").mkdir();
    new File("build").mkdir();
    new File("test").mkdir();
    yuukaConfig.createConfig();
    libconf.createConfig();
    stdout.print("Project structure created");
  }

  public static int build() {return build(true);}

  public static int build(boolean display_main) {
    if (cli.projectHasNoSource()) {return -3;}
    int result = fetchLibs();
    if (result != 0) {stdout.print("Cancelling compilation due to dependency errors"); return -1;}
    stdout.print("Compiling project");
    if (display_main) {stdout.print_verbose("Main class is " + globalvariables.MAIN_CLASS);}

    return compiler.compile();
  }

  public static int runTask_package() {
    int result = build(true);
    if (result != 0) {return result;}
    stdout.print("Creating executable JAR \"" + globalvariables.PROGRAM_NAME + "\"");
    result = compiler.createJAR(globalvariables.PROGRAM_NAME, globalvariables.MAIN_CLASS, false);
    
    stdout.print("Cleaning up class files");
    fileops.deleteBuildFiles("build");
    fileops.deleteClassFiles("lib");
    return result;
  }

  public static int packageLib() {
    int result = build(false);
    if (result != 0) {return result;}

    stdout.print("Creating library JAR \"" + globalvariables.PROGRAM_NAME + "\"");
    result = compiler.createJAR(globalvariables.PROGRAM_NAME, globalvariables.MAIN_CLASS, true);

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

  public static void runProgram(String[] args) {
    int result =
      (!new File("build/" + globalvariables.MAIN_CLASS + ".class").isFile())
      ? build(true)
      : 0;
    if (result != 0) {return;}
    stdout.print("Running program");
    compiler.runProgram(args);
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
    fileops.deleteBuildFiles("build");
  }

  public static int fetchLibs() {
    int result = libconf.createConfig();
    if (result == 0) {stdout.print_debug("File libs.yuuka not found, creating file and skipping dependency fetching"); return 0;}
    else if (result < 0) {return result;}

    String[] conf = confreader.readConfig("libs.yuuka");
    result = fetchMavenLibs(conf);
    if (result != 0) {return result;}
    result = fetchCustomLibs(conf);
    return result;
  }

  private static int fetchMavenLibs(String[] config) {
    MavenLibrary[] libs = libconf.getMavenLibraries(config);
    if (libs.length == 0) {return 0;}

    stdout.print("Fetching Maven Dependencies");
    int result = 0;
    for (MavenLibrary lib : libs) {
      result = lib.fetchLibrary();
      if (result != 0) {return result;}
    }
    return 0;
  }

  private static int fetchCustomLibs(String[] config) {
    CustomLibrary[] libs = libconf.getCustomLibraries(config);
    if (libs.length == 0) {return 0;}

    stdout.print("Fetching Custom Dependencies");
    int result = 0;
    for (CustomLibrary lib : libs) {
      result = lib.fetchLibrary();
      if (result != 0) {return result;}
    }
    return 0;
  }
}
