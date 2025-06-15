package yuuka;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import yuuka.config.*;
import yuuka.libfetch.*;
import yuuka.io.*;

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
  
  public static void cleanProject() {
    stdout.print("Cleaning up all class files");
    fileops.deleteClassFiles("src");
    fileops.deleteBuildFiles_all("build");
    fileops.deleteClassFiles("lib");
    fileops.deleteClassFiles("test");
  }

  public static boolean build() {return build(true);}

  public static boolean build(boolean display_main) {
    if (projectHasNoSource()) {return false;}
    if (!fetchLibs()) {stdout.error("Cancelling compilation due to dependency errors"); return false;}
    fileops.deleteBeforeCompile("build");
    stdout.print("Compiling project");
    if (display_main) {stdout.print_verbose("Main class is " + global.MAIN_CLASS);}

    return compiler.compile();
  }

  public static boolean packageJAR() {
    boolean result = build(true);
    if (!result) {return false;}
    stdout.print("Creating executable JAR \"" + global.PROGRAM_NAME + "\"");
    if (!global.mainIsDefined()) {
      stdout.error
      (
        "Main class is not defined!"
        +"\nIf not automatically detected, you can set your program's main class through build.yuuka or as a command-line argument."
        +"\n\nExample: for the main file \"src/yuuka/main.java\", the main class should be \"yuuka/main\"."
      );
      return false;
    }
    
    if (!global.mainIsCorrect()) {
      stdout.error
      (
        "Main class \"" + global.MAIN_CLASS + "\" is incorrectly defined! Cancelling JAR packaging."
        +"\nIf not automatically detected, you can set your program's main class through build.yuuka or as a command-line argument."
        +"\n\nExample: for the main file \"src/yuuka/main.java\", the main class should be \"yuuka/main\"."
      );
      return false;
    }
    result = compiler.createJAR(global.PROGRAM_NAME, global.MAIN_CLASS, false);
    
    stdout.print("Cleaning up class files");
    fileops.deleteBuildFiles("build");
    fileops.deleteClassFiles("lib");
    return result;
  }

  public static boolean packageLib() {
    boolean result = build(false);
    if (!result) {return false;}

    stdout.print("Creating library JAR \"" + global.PROGRAM_NAME + "\"");
    result = compiler.createJAR(global.PROGRAM_NAME, global.MAIN_CLASS, true);

    stdout.print("Cleaning up class files");
    fileops.deleteBuildFiles("build");
    fileops.deleteClassFiles("lib");
    return result;
  }

  public static boolean buildNativeBinary() {
    if (!packageJAR()) {return false;}
    String[] nativecmd = new String[]
    {
      global.GRAAL_PATH, "--no-fallback", "--static", "-O3", "-jar",
      global.PROGRAM_NAME,
      "-o", misc.removeExtension(global.PROGRAM_NAME)
    };
    stdout.print("Building native binary with GraalVM");
    stdout.print_verbose("GraalVM command:", nativecmd);
    int exitstatus = process.runProcess(nativecmd, "build");
    if (exitstatus == -1) {stdout.error("Failed to run the GraalVM binary, GraalVM needs to be installed in order to build native binaries.");}
    else if (exitstatus > 0) {stdout.error("The compilation failed!");}
    return exitstatus == 0;
  }

  public static void runProgram(String[] args) {
    boolean result =
      (!new File("build/" + global.MAIN_CLASS + ".class").isFile())
      ? build(true)
      : true;
    if (!result) {return;}
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
      stdout.error("The file with name \"test/" + source_arg + "\" does not exist!");
      return;
    }
    
    boolean result;
    if (isJavaFile) {
      if (!projectHasNoSource() && global.TESTS_INCLUDE_PROJECT) {packageLib();}
      result = tests.runTest_java(file_java, args);
      fileops.deleteBuildFiles("build");
    }
    else {result = tests.runTest_native(f_source.getAbsolutePath(), args);}
      
    if (!result) {stdout.error("Error during building/running the test!");}
  }
  
  public static void uninstallProgram(String program_name) {
    String install_path = global.INSTALL_PATH;
    var f_script = new File(install_path + "/" + program_name);
    var f_jar = new File(install_path + "/jars/" + program_name + ".jar");
    
    boolean executable_exists = f_script.isFile();
    boolean executable_canWrite = f_script.canWrite();
    boolean jar_exists = f_jar.isFile();
    boolean jar_canWrite = f_jar.canWrite();
    
    if (!executable_exists) {
      stdout.error("Program " + program_name + " not found! Failed to uninstall!");
      return;
    }
    if (!executable_canWrite) {
      stdout.error("Could not uninstall program " + program_name + "! Make sure you have write access in " + install_path);
      return;
    }
    if (jar_exists && !jar_canWrite) {
      stdout.error("Could not uninstall the JAR file of program " + program_name + "! Make sure you have write access to it");
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
  
  public static void createTest(String class_name) {
    if (!new File("test").isDirectory()) {
      stdout.error("Directory named \"test\" does not exist! You can create it by running \"yuuka init\"");
      return;
    }
    String[] invalid_chars = new String[]{"/", "\\", ".", " ", "\t", "(", ")", "{", "}", "[", "]", ";", "#", "%", "\""};
    for (String c : invalid_chars) {
      if (class_name.contains(c)) {
        stdout.error(
          "Cancelling test file creation!"
          +"\nThe class name cannot contain special characters such as slashes, periods, spaces, tabs, parentheses, brackets and other symbols!"
        );
        return;
      }
    }
    
    String full_path = "test/" + class_name + ".java";
    if (new File(full_path).isFile()) {
      stdout.error("The test file of name " + class_name + " already exists!");
      return;
    }
    try {
      Path p = Path.of(full_path);
      String template =
        "public class " + class_name + " {"
        + "\n    public static void main(String[] args) {}"
        + "\n}"
      ;
      Files.createFile(p);
      Files.write(p, template.getBytes());
    }
    catch (IOException e) {stdout.error("Failed to create test file!");}
  }
  
  private static boolean fetchLibs() {
    int result = libconf.createConfig();
    if (result == 0) {stdout.print_verbose("File libs.yuuka not found, creating file and skipping dependency fetching"); return false;}
    else if (result < 0) {return false;}

    String[] conf = libconf.readConfig();
    result = fetchMavenLibs(conf);
    if (result != 0) {return false;}
    result = fetchCustomLibs(conf);
    return result == 0;
  }
  
  private static boolean projectHasNoSource() {
    var f = new File("src");
    if (!f.isDirectory() || f.list() == null) {
      stdout.error("Your project has no \"src\" directory or it is empty!");
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
