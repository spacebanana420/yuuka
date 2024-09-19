package yuuka;

import java.util.ArrayList;

public class cmdbuilder {
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

  // public static String[] appendArg(String[] args, String new_arg) {
  //   String[] full = new String[args.length + 1];
  //   for (int i = 0; i < args.length; i++) {full[i] = args[i];}
  //   args[args.length-1] = new_arg;
  //   return full;
  // }
}
