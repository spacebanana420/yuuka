package yuuka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class compiler {

  public static int compile() {
    new File("build").mkdir();
    var source_files = fileops.getSourceFiles("src");
    
    String[] cmd =
      (globalvariables.RELEASE_TARGET.length() > 0)
      ? buildCommand(source_files, "javac", globalvariables.RELEASE_TARGET)
      : buildCommand(source_files, "javac");

    if (!globalvariables.INGORE_LIB && lib.projectHasLibraries()) {
      String[] libargs = lib.getLibArgs(lib.getLibraryJars());
      cmd = concatArgs(cmd, libargs);
    }
    return runProcess(cmd, ".");
  }

  public static int createJAR(String jarName, String main_class) {
    String[] class_files =
      fileops.removeParent(fileops.getClassFiles("build"), "build/")
      .toArray(new String[0]);
      
    String[] cmd = buildJARCommand(jarName, main_class, class_files, "jar");

    if (!globalvariables.INGORE_LIB && lib.projectHasLibraries()) { //remove duplicate tasks later
      var jars = lib.getLibraryJars();
      runProcess(buildExtractCommand(jars, "jar"), ".");
      cmd = concatArgs(cmd, lib.changeBaseDirectory(jars).toArray(new String[0]));
    }
    return runProcess(cmd, "build");
  }

  public static int runProgram() {
    String[] cmd = new String[]{"java", globalvariables.MAIN_CLASS};

    if (!globalvariables.INGORE_LIB && lib.projectHasLibraries()) {
      var jars = lib.changeBaseDirectory(lib.getLibraryJars());
      cmd = concatArgs(cmd, lib.getLibArgs(jars));
    }
    return runProcess(cmd, "build");
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

  private static String[] concatArgs(String[] args1, String[] args2) {
    if (args1.length == 0) {return args2;} if (args2.length == 0) {return args1;}

    String[] full = new String[args1.length + args2.length];
    for (int i = 0; i < args1.length; i++) {full[i] = args1[i];}
    for (int i = 0; i < args2.length; i++) {full[i+args1.length] = args2[i];}
    return full;
  }
}
