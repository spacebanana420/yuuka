package yuuka.jdk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import yuuka.io.stdout;
import yuuka.global;

public class process {
  public static int runProcess(String[] cmd, String working_directory) {
    stdout.print_debug("Working directory: " + working_directory +"\nExecuting the process:\n", cmd);
    return execute(new ProcessBuilder(cmd), cmd[0], working_directory);
  }
  
  public static int runProcess(ArrayList<String> cmd, String working_directory) {
    stdout.print_debug("Working directory: " + working_directory +"\nExecuting the process:\n", cmd);
    return execute(new ProcessBuilder(cmd), cmd.get(0), working_directory);
  }
  
  private static int execute(ProcessBuilder builder, String process_name, String working_dir) {
    builder.inheritIO();
    builder.directory(new File(working_dir));
    try {
      Process p = builder.start();
      p.waitFor();
      return p.exitValue();
    }
    catch (IOException e) {
      stdout.error("Error running \"" + process_name + "\": program does not exist!");
      return -1;
    }
    catch (InterruptedException e) {
      stdout.error("Error running \"" + process_name + "\": process was interrupted!");
      return -2;
    }
  }

  public static String[] buildCommand(ArrayList<String> source_files, String binary_path) {
    ArrayList<String> cmd = new ArrayList<>();
    String release_target = global.RELEASE_TARGET;
    String source_target = global.SOURCE_TARGET;
    String class_target = global.CLASS_TARGET;
    
    cmd.add(binary_path);
    cmd.add("-d"); cmd.add("build");
    if (release_target != null) {cmd.add("--release"); cmd.add(release_target);}
    else {
      if (source_target != null) {cmd.add("--source"); cmd.add(source_target);}
      if (class_target != null) {cmd.add("--target"); cmd.add(class_target);}
    }
    if (global.DISABLE_WARNINGS) {cmd.add("-nowarn");}
    cmd.addAll(source_files);

    return cmd.toArray(new String[0]);
  }
  
  public static ArrayList<String> buildJARCommand(String output_path, String main_class, ArrayList<String> class_files, String binary_path) {
    var cmd = new ArrayList<String>();
    cmd.add(binary_path);
    
    cmd.add("-c"); cmd.add("-f"); cmd.add(output_path); 
    if (main_class != null) {cmd.add("--main-class="+main_class);}
    if (global.DISABLE_JAR_COMPRESSION) {cmd.add("--no-compress");}
    
    cmd.addAll(class_files);
    return cmd;
  }

  public static String[] buildExtractCommand(String jar_file, String binary_path) {
    return new String[]{binary_path, "-x", "-f", jar_file};
  }  
  
  public static String[] concatArgs(String[] args1, String[] args2) {
    if (args1.length == 0) {return args2;} if (args2.length == 0) {return args1;}
  
    String[] full = new String[args1.length + args2.length];
    for (int i = 0; i < args1.length; i++) {full[i] = args1[i];}
    for (int i = 0; i < args2.length; i++) {full[i+args1.length] = args2[i];}
    return full;
  }
}
