package yuuka.jdk;

import java.io.File;
import yuuka.stdout;
import yuuka.misc;
import yuuka.globalvariables;
import yuuka.fileops;

public class compiler {
  public static int compile() {
    new File("build").mkdir();
    var source_files = fileops.getSourceFiles("src");
    
    String[] cmd =
      (globalvariables.RELEASE_TARGET.length() > 0)
      ? process.buildCommand(source_files, "javac", globalvariables.RELEASE_TARGET)
      : process.buildCommand(source_files, "javac");

    cmd = process.addLibArgs(cmd, false);
    return process.runProcess(cmd, ".");
  }

  public static int createJAR(String jarName, String main_class, boolean library_jar) {
    extractLibraries();
    stdout.print_verbose("Copying license files into final JAR");
    fileops.copyLicensesToBuild("src");
    String[] class_files =
      fileops.removeParent(fileops.getClassFiles("build", true), "build/")
      .toArray(new String[0]);
      
    String[] cmd =
      (!library_jar)
      ? process.buildJARCommand(jarName, main_class, class_files, "jar")
      : process.buildJARCommand(jarName, class_files, "jar");
    return process.runProcess(cmd, "build");
  }

  private static void extractLibraries() {
    if (globalvariables.INGORE_LIB || !lib.projectHasLibraries()) {return;}    
    var jars = lib.changeBaseDirectory(lib.getLibraryJars());
    for (String jar : jars) {
      process.runProcess(process.buildExtractCommand(jar, "jar"), "build");
    }
  }

  public static int runProgram(String[] args) {
    String[] cmd = new String[]{"java", globalvariables.MAIN_CLASS};
    cmd = process.addLibArgs(cmd, true);
    
    String[] exec_args = misc.getExecArgs(args);
    stdout.print_verbose("Passing the following arguments to program execution:", exec_args);
    
    cmd = process.concatArgs(cmd, exec_args);
    return process.runProcess(cmd, "build");
  }
}
