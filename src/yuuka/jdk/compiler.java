package yuuka.jdk;

import java.io.File;
import java.util.ArrayList;

import yuuka.io.stdout;
import yuuka.misc;
import yuuka.cli.cli;
import yuuka.global;
import yuuka.io.fileops;

public class compiler {
  public static boolean compile() {
    File build_f = new File("build");
    build_f.mkdir();
    if (!build_f.canWrite()) {
      stdout.error("Error running compiler! Build directory lacks permissions to write!");
      return false;
    }
    ArrayList<String> source_files = fileops.getSourceFiles("src");
    
    String[] cmd = process.buildCommand(source_files, "javac");
    cmd = lib.addLibArgs(cmd, false);
    return process.runProcess(cmd, ".") == 0;
  }

  public static boolean createJAR(String jarName, String main_class, boolean library_jar) {
    extractLibraries();
    boolean copyresult = fileops.copyLicensesToBuild();
    if (copyresult) {stdout.print_verbose("Found and copied license files into final JAR");}
    ArrayList<String> class_files = fileops.removeParent(fileops.getClassFiles("build", true), "build/");
      
    ArrayList<String> cmd =
      (!library_jar)
      ? process.buildJARCommand(jarName, main_class, class_files, "jar")
      : process.buildJARCommand(jarName, null, class_files, "jar");
    return process.runProcess(cmd, "build") == 0;
  }

  private static void extractLibraries() {
    if (global.INGORE_LIB || lib.projectHasNoLibraries()) {return;}
    ArrayList<String> jars = lib.getLibraryJars(true);
    for (String jar : jars) {
      stdout.print_verbose("Extracting library JAR " + jar);
      process.runProcess(process.buildExtractCommand(jar, "jar"), "build");
    }
  }

  public static int runProgram(String[] exec_args) {
    String[] cmd = new String[]{"java", global.MAIN_CLASS};
    cmd = lib.addLibArgs(cmd, true);
    
    stdout.print_verbose("Passing the following arguments to program execution:", exec_args);
    cmd = process.concatArgs(cmd, exec_args);
    return process.runProcess(cmd, "build");
  }
}
