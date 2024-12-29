package yuuka;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class installer {
  public static void installProgram() {
    String install_location = globalvariables.INSTALL_PATH;
    String jar_path = install_location + "/jars/" + globalvariables.PROGRAM_NAME;
    String script_path = install_location + "/" + misc.removeExtension(globalvariables.PROGRAM_NAME);
    
    install(globalvariables.PROGRAM_NAME, "build/" + globalvariables.PROGRAM_NAME, jar_path, script_path, install_location);
  }

  public static void installProgram(String jar) {
    String name = new File(jar).getName();
    String install_location = globalvariables.INSTALL_PATH;
    String jar_path = install_location + "/jars/" + name;   
    String script_path = install_location + "/" + misc.removeExtension(name);

    install(name, jar, jar_path, script_path, install_location);
  }
  
  public static void installProgram_native() {
    String install_location = globalvariables.INSTALL_PATH;
    String binary_name = misc.removeExtension(globalvariables.PROGRAM_NAME);
    String source_path = "build/" + binary_name;
    String install_path = install_location + "/" + binary_name;
    
    install_native(source_path, install_path, install_location);
  }

  public static String getInstallLocation() {
    String home = System.getProperty("user.home");
    String os = System.getProperty("os.name");
    if (home.equals("/root")) {return "/usr/local/bin";}
    if (os.equals("FreeBSD")) {return home + "/bin";}
    return home + "/.local/bin";
  }

  private static void install(String name, String source_jar, String jar_path, String script_path, String install_location) {
    File install_f = new File(install_location);
    boolean path_isDir = install_f.isDirectory();
    boolean path_canWrite = install_f.canWrite();
    if (!path_isDir) {
      stdout.print("The installation path " + install_location + " does not exist! Cancelling installation.");
      return;
    }
    if (!path_canWrite) {
      stdout.print("The installation path " + install_location + " lacks write permissions! Cancelling installation.");
      return;
    }
    
    byte[] script_contents =
      ("#!/bin/sh\njava -jar " + jar_path + " \"$@\"")
      .getBytes();

    try {
      stdout.print("Installing program at " + install_location);
      stdout.print_verbose
        (
          "\nJAR path: " + source_jar
          + "\nName: " + name
          + "\nOutput JAR: " + jar_path
          + "\nOutput script: " + script_path
        );
      Path p_script = Path.of(script_path);
      Path p_jar = Path.of(jar_path);

      if (new File(script_path).isFile()) {
        stdout.print_verbose("Old instance of the script found installed, deleting...");
        Files.delete(p_script);
      }
      if (new File(jar_path).isFile()) {
        stdout.print_verbose("Old instance of the JAR found installed, deleting...");
        Files.delete(p_jar);
      }

      Files.createFile(p_script);
      new File(script_path).setExecutable(true, false);
      Files.write(p_script, script_contents);

      new File(install_location + "/jars").mkdir();
      Files.move(Path.of(source_jar), p_jar);
    }
    catch (IOException e) {
      stdout.print("The installation of your program failed!");
      return;
    }
    stdout.print(name + " has been installed at " + install_location);
  }
  
  private static void install_native(String source_binary, String binary_path, String install_location) {
    File install_f = new File(install_location);
    boolean path_isDir = install_f.isDirectory();
    boolean path_canWrite = install_f.canWrite();
    if (!path_isDir) {
      stdout.print("The installation path " + install_location + " does not exist! Cancelling installation.");
      return;
    }
    if (!path_canWrite) {
      stdout.print("The installation path " + install_location + " lacks write permissions! Cancelling installation.");
      return;
    }

    try {
      stdout.print("Installing program at " + install_location);
      stdout.print_verbose
        (
          "\nBinary path: " + source_binary
          + "\nOutput path: " + binary_path
        );
      Path p_binary = Path.of(binary_path);
      Path p_source = Path.of(source_binary);

      if (new File(binary_path).isFile()) {
        stdout.print_verbose("Old instance of the script found installed, deleting...");
        Files.delete(p_binary);
      }
      Files.move(p_source, p_binary);
      new File(binary_path).setExecutable(true, false);
    }
    catch (IOException e) {
      stdout.print("The installation of your program failed!");
      return;
    }
    stdout.print(source_binary + " has been installed at " + install_location);
  }
}
