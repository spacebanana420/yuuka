package yuuka.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
    if (projectHasNoSource()) {return -3;}
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
    if (!globalvariables.mainIsDefined()) {
      stdout.print
      (
        "Main class is not defined or incorrectly defined! Cancelling JAR packaging."
        +"\nIf not automatically detected, you can set your program's main class through build.yuuka or as a command-line argument."
        +"\n\nExample: for the main file \"src/yuuka/main.java\", the main class should be \"yuuka/main\"."
      );
      return -1;
    }
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
    fileops.deleteBuildFiles("build");
    fileops.deleteClassFiles("lib");
    return result;
  }

  public static int buildNativeBinary() {
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
    return exitstatus;
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
    new File("test").mkdir();
    
    String file_java =
      (misc.checkFileExtension(source_arg, ".java"))
      ? source_arg
      : source_arg + ".java";
    File f_source = new File("test/"+source_arg);
    File f_java = new File("test/"+file_java);
    boolean isJavaFile = f_java.isFile();
    boolean isExecutableFile = f_source.isFile() && f_source.canExecute();
  
    if (!isJavaFile && !isExecutableFile) {
      stdout.print("The file with name \"test/" + source_arg + "\" does not exist!");
      return;
    }
    
    boolean result;
    if (isJavaFile) {
      if (!projectHasNoSource() && globalvariables.TESTS_INCLUDE_PROJECT) {packageLib();}
      result = tests.runTest_java(file_java, args);
      fileops.deleteBuildFiles("build");
    }
    else {result = tests.runTest_native(f_source.getAbsolutePath(), args);}
      
    if (!result) {stdout.print("Error during building/running the test!");}
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
  
  public static void uninstallProgram(String program_name) {
    String install_path = globalvariables.INSTALL_PATH;
    var f_script = new File(install_path + "/" + program_name);
    var f_jar = new File(install_path + "/jars/" + program_name + ".jar");
    
    boolean executable_exists = f_script.isFile();
    boolean executable_canWrite = f_script.canWrite();
    boolean jar_exists = f_jar.isFile();
    boolean jar_canWrite = f_jar.canWrite();
    
    if (!executable_exists) {
      stdout.print("Program " + program_name + " not found! Failed to uninstall!");
      return;
    }
    if (!executable_canWrite) {
      stdout.print("Could not uninstall program " + program_name + "! Make sure you have write access in " + install_path);
      return;
    }
    if (jar_exists && !jar_canWrite) {
      stdout.print("Could not uninstall the JAR file of program " + program_name + "! Make sure you have write access to it");
    }
    var p_script = Path.of(install_path + "/" + program_name);
    var p_jar = Path.of(install_path + "/jars/" + program_name + ".jar");
    try {
      Files.delete(p_script);
      if (jar_exists && jar_canWrite) {Files.delete(p_jar);}
      
      stdout.print("Successfully deleted " + program_name + " located in " + install_path);
    }
    catch (IOException e) {e.printStackTrace();} //this is not even supposed to happen so ill just print the stack trace
  }
  
  private static boolean projectHasNoSource() {
    var f = new File("src");
    if (!f.isDirectory() || f.list() == null) {
      stdout.print("Your project has no \"src\" directory or it is empty!");
      return true;
    }
    return false;
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
