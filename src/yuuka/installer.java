package yuuka;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class installer {
  public static void installProgram() {
    String home = System.getProperty("user.home");
    String install_location =
      (home.equals("/root")) ? "/usr/bin"
      : home + "/.local/bin";

    File install_file = new File(install_location);
    if (!install_file.isDirectory() || !install_file.canWrite()) {
      stdout.print("The installation path " + install_location + " does not exist or lacks write permissions! Cancelling installation.");
      return;
    }

    String jar_path = install_location + "/jars/" + globalvariables.PROGRAM_NAME;
    String script_path = install_location + "/" + removeExtension(globalvariables.PROGRAM_NAME);
    byte[] script_contents =
      ("#!/bin/sh\njava -jar " + jar_path + " \"$@\"")
      .getBytes();

    try {
      stdout.print_verbose("Installing program at " + install_location);
      Path p_script = Path.of(script_path);
      Files.createFile(p_script);
      new File(script_path).setExecutable(true);
      Files.write(p_script, script_contents);

      new File(install_location + "/jars").mkdir();
      Files.move(Path.of("build/" + globalvariables.PROGRAM_NAME), Path.of(jar_path));
    }
    catch (IOException e) {stdout.print("The installation of your program failed!");}
    stdout.print(globalvariables.PROGRAM_NAME + " has been installed at " + install_location);
  }

  private static String removeExtension(String filename) {
    int point_i = -1;
    for (int i = filename.length()-1; i >= 0; i--) {
      if (filename.charAt(i) == '.') {point_i = i; break;}
    }
    if (point_i == -1) {return filename;}
    String newname = "";
    for (int i = 0; i < point_i; i++) {
      newname += filename.charAt(i);
    }
    return newname;
  }
}
