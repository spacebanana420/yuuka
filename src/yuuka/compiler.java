package yuuka;

import java.io.File;
import java.io.IOException;

public class compiler {

  public static int compile() {
    new File("build").mkdir();
    var source_files = fileops.getSourceFiles("src");
    
    String[] cmd =
      (globalvariables.RELEASE_TARGET.length() > 0)
      ? cmdbuilder.buildCommand(source_files, "javac", globalvariables.RELEASE_TARGET)
      : cmdbuilder.buildCommand(source_files, "javac");

    if (!globalvariables.INGORE_LIB && lib.projectHasLibraries()) {
      String[] libargs = lib.getLibArgs(lib.getLibraryJars());
      cmd = cmdbuilder.concatArgs(cmd, libargs);
    }
    return runProcess(cmd, ".");
  }

  public static int createJAR(String jarName, String main_class) {
    String[] class_files =
      fileops.removeParent(fileops.getClassFiles("build"), "build/")
      .toArray(new String[0]);
      
    String[] cmd = cmdbuilder.buildJARCommand(jarName, main_class, class_files, "jar");

    if (!globalvariables.INGORE_LIB && lib.projectHasLibraries()) { //remove duplicate tasks later
      var jars = lib.getLibraryJars();
      runProcess(cmdbuilder.buildExtractCommand(jars, "jar"), ".");
      cmd = cmdbuilder.concatArgs(cmd, lib.changeBaseDirectory(jars).toArray(new String[0]));
    }
    return runProcess(cmd, "build");
  }

  public static int runProgram() {
    String[] cmd = new String[]{"java", globalvariables.MAIN_CLASS};

    if (!globalvariables.INGORE_LIB && lib.projectHasLibraries()) {
      var jars = lib.changeBaseDirectory(lib.getLibraryJars());
      cmd = cmdbuilder.concatArgs(cmd, lib.getLibArgs(jars));
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
    catch (IOException e) {
      stdout.print("The process " + cmd[0] + " failed to execute!");
      return -1;
    }
    catch (InterruptedException e) {
      stdout.print("Process " + cmd[0] + " was interrupted!");
      return -2;
    }
  }
}
