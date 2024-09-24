package yuuka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class process {

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
    catch (IOException e) {
      stdout.print("The process " + cmd[0] + " failed to execute!");
      return -1;
    }
    catch (InterruptedException e) {
      stdout.print("Process " + cmd[0] + " was interrupted!");
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

  public static String[] buildExtractCommand(ArrayList<String> jar_files, String binary_path) {
    String[] cmd = new String[]{binary_path, "-x"};
    String[] extract_args = lib.getExtractionArgs(jar_files);
    return concatArgs(cmd, extract_args);
  }  

  public static String[] concatArgs(String[] args1, String[] args2) {
    if (args1.length == 0) {return args2;} if (args2.length == 0) {return args1;}

    String[] full = new String[args1.length + args2.length];
    for (int i = 0; i < args1.length; i++) {full[i] = args1[i];}
    for (int i = 0; i < args2.length; i++) {full[i+args1.length] = args2[i];}
    return full;
  }

  public static String[] compiler_addlib(String[] cmd, boolean change_base_directory) {
    if (!globalvariables.INGORE_LIB && lib.projectHasLibraries()) {
      var jars = lib.getLibraryJars();
      if (change_base_directory) {jars = lib.changeBaseDirectory(jars);}
      return process.concatArgs(cmd, lib.getLibArgs(jars));
    }
    else {return cmd;}
  }

  // public static String[] appendArg(String[] args, String new_arg) {
  //   String[] full = new String[args.length + 1];
  //   for (int i = 0; i < args.length; i++) {full[i] = args[i];}
  //   args[args.length-1] = new_arg;
  //   return full;
  // }
}
