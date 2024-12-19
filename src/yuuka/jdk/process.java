package yuuka.jdk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import yuuka.stdout;
import yuuka.globalvariables;

public class process {

  //todo: add output directory
  public static int runProcess(String[] cmd, String working_directory) {
    try {
      stdout.print_debug("Working directory: " + working_directory +"\nExecuting the process:\n", cmd);
      var process =
        new ProcessBuilder(cmd)
        .inheritIO()
        .directory(new File(working_directory))
        .start();
      process.waitFor();
      return process.exitValue();
    }
    catch (IOException e) {
      stdout.print("Error running \"" + cmd[0] + "\": program does not exist!");
      return -1;
    }
    catch (InterruptedException e) {
      stdout.print("Error running \"" + cmd[0] + "\": process was interrupted!");
      return -2;
    }
  }

  public static String[] buildCommand(ArrayList<String> source_files, String binary_path) {
    ArrayList<String> cmd = new ArrayList<>();

    cmd.add(binary_path);
    cmd.add("-d"); cmd.add("build");
    if (globalvariables.DISABLE_WARNINGS) {cmd.add("-nowarn");}
    cmd.addAll(source_files);

    return cmd.toArray(new String[0]);
  }

  public static String[] buildCommand(ArrayList<String> source_files, String binary_path, String release) {
    ArrayList<String> cmd = new ArrayList<>();
    
    cmd.add(binary_path);
    cmd.add("-d"); cmd.add("build");
    cmd.add("--release"); cmd.add(release);
    if (globalvariables.DISABLE_WARNINGS) {cmd.add("-nowarn");}
    cmd.addAll(source_files);

    return cmd.toArray(new String[0]);
  }

  public static String[] buildJARCommand(String output_path, String main_class, String[] class_files, String binary_path) {
    String[] cmd = new String[class_files.length + 4];
    cmd[0] = binary_path;
    cmd[1] = "cfe";
    cmd[2] = output_path;
    cmd[3] = main_class;
    for (int i = 4; i < cmd.length; i++) {cmd[i] = class_files[i-4];}
    return cmd;
  }

  public static String[] buildJARCommand(String output_path, String[] class_files, String binary_path) {
    String[] cmd = new String[class_files.length + 3];
    cmd[0] = binary_path;
    cmd[1] = "cf";
    cmd[2] = output_path;
    for (int i = 3; i < cmd.length; i++) {cmd[i] = class_files[i-3];}
    return cmd;
  }

  public static String[] buildExtractCommand(String jar_file, String binary_path) {
    String[] cmd = new String[]{binary_path, "-x", "-f", jar_file};
    return cmd;
  }  
  
  public static String[] concatArgs(String[] args1, String[] args2) {
    if (args1.length == 0) {return args2;} if (args2.length == 0) {return args1;}
  
    String[] full = new String[args1.length + args2.length];
    for (int i = 0; i < args1.length; i++) {full[i] = args1[i];}
    for (int i = 0; i < args2.length; i++) {full[i+args1.length] = args2[i];}
    return full;
  }

  public static String[] addLibArgs(String[] cmd, boolean change_base_directory) {
    if (globalvariables.INGORE_LIB || !lib.projectHasLibraries()) {return cmd;}
    
    var jars = lib.getLibraryJars();
    if (change_base_directory) {jars = lib.changeBaseDirectory(jars);}
    String[] libargs = lib.getLibArgs(jars);
    
    String[] finalcmd = new String[cmd.length + libargs.length];
    finalcmd[0] = cmd[0];
    for (int i = 0; i < libargs.length; i++) {finalcmd[i+1] = libargs[i];}
    for (int i = 1; i < cmd.length; i++) {finalcmd[i+libargs.length] = cmd[i];}
    return finalcmd;
  }
}
