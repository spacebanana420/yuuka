package yuuka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class compiler {

  public static String[] compile() {
    new File("build").mkdir();
    var source_files = fileops.getSourceFiles("src");
    
    String[] cmd =
      (globalvariables.RELEASE_TARGET.length() > 0)
      ? buildCommand(source_files, "javac", globalvariables.RELEASE_TARGET)
      : buildCommand(source_files, "javac");
    runProcess(cmd, ".");
    return fileops.getClassFiles(source_files, false);
  }

  public static void createJAR(String jarName, String main_class, String[] class_files) {
    String[] cmd = buildJARCommand("build", jarName, main_class, class_files, "jar");
    runProcess(cmd, "build");
  }

  //todo: add output directory
  public static int runProcess(String[] cmd, String working_directory) {
    try {
      stdout.print_verbose("Executing the process:\n", cmd);
      var process =
        new ProcessBuilder(cmd)
        .inheritIO()
        .directory(new File(working_directory))
        .start();
      process.waitFor();
      return process.exitValue();
    }
    catch (IOException e) {return -1;}
    catch (InterruptedException e) {return -2;}
  }

  public static String[] buildCommand(ArrayList<String> source_files, String binary_path) {
    String[] cmd = new String[source_files.size() + 3];
    cmd[0] = binary_path;
    cmd[1] = "-d";
    cmd[2] = "build";
    for (int i = 3; i < cmd.length; i++) {cmd[i] = source_files.get(i-3);}
    return cmd;
  }

  public static String[] buildCommand(ArrayList<String> source_files, String binary_path, String release) {
    String[] cmd = new String[source_files.size() + 5];
    cmd[0] = binary_path;
    cmd[1] = "--release";
    cmd[2] = release;
    cmd[3] = "-d";
    cmd[4] = "build";
    for (int i = 5; i < cmd.length; i++) {cmd[i] = source_files.get(i-5);}
    return cmd;
  }

  public static String[] buildJARCommand(String source_path, String output_path, String main_class, String[] class_files, String binary_path) {
    String[] cmd = new String[class_files.length + 4];
    cmd[0] = binary_path;
    cmd[1] = "cfe";
    cmd[2] = output_path;
    cmd[3] = main_class;
    for (int i = 4; i < cmd.length; i++) {cmd[i] = class_files[i-4];}
    return cmd;
  }
}
