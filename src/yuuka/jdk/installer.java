package yuuka.jdk;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import yuuka.global;
import yuuka.io.stdout;
import yuuka.misc;

public class installer {
  public static void installProgram() {
    String install_location = global.INSTALL_PATH;
    String jar_path = install_location + "/jars/" + global.PROGRAM_NAME;
    String script_path = install_location + "/" + misc.removeExtension(global.PROGRAM_NAME);
    
    install(global.PROGRAM_NAME, "build/" + global.PROGRAM_NAME, jar_path, script_path, install_location);
  }

  public static void installProgram(String jar) {
    String name = new File(jar).getName();
    String install_location = global.INSTALL_PATH;
    String jar_path = install_location + "/jars/" + name;   
    String script_path = install_location + "/" + misc.removeExtension(name);

    install(name, jar, jar_path, script_path, install_location);
  }
  
  public static void installProgram_native() {
    String install_location = global.INSTALL_PATH;
    String binary_name = misc.removeExtension(global.PROGRAM_NAME);
    String source_path = "build/" + binary_name;
    String install_path = install_location + "/" + binary_name;
    
    install_native(source_path, install_path, install_location);
  }

  public static String getInstallLocation() {
    String home = System.getProperty("user.home");
    String os = System.getProperty("os.name").toLowerCase();
    
    if (os.equals("haiku"))
      {return "/boot/home/config/bin";}
    if (home.equals("/root") || os.contains("mac"))
      {return "/usr/local/bin";}
    if (os.equals("freebsd"))
      {return home + "/bin";}
    if (new File(home + "/.local/bin").isDirectory())
      {return home + "/.local/bin";}
    return "/usr/local/bin";
  }

  private static void install(String name, String source_jar, String jar_path, String script_path, String install_location) {
    if (!checkInstallPath(new File(install_location), install_location)) {return;}
   
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
      stdout.error("The installation of your program failed!");
      return;
    }
    stdout.print(name + " has been installed at " + install_location);
  }
  
  private static void install_native(String source_binary, String binary_path, String install_location) {
    if (!checkInstallPath(new File(install_location), install_location)) {return;}
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
      stdout.error("The installation of your program failed!");
      return;
    }
    stdout.print(source_binary + " has been installed at " + install_location);
  }
  
  private static boolean checkInstallPath(File path, String install_location) {
    if (!path.isDirectory()) {
      stdout.error("The installation path " + install_location + " does not exist! Cancelling installation.");
      return false;
    }
    if (!path.canWrite()) {
      stdout.error("The installation path " + install_location + " lacks write permissions! Cancelling installation.");
      return false;
    }
    return true;
  }
}
