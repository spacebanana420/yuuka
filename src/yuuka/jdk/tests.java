package yuuka.jdk;

import java.io.File;
import java.util.ArrayList;

import yuuka.global;
import yuuka.io.stdout;
import yuuka.cli.cli;
import yuuka.misc;
import yuuka.io.fileops;

public class tests {
  public static int runTest_java(String source_file, String[] args) {    
    String[] cmd =
      (global.TESTS_INCLUDE_PROJECT && new File("build/"+global.PROGRAM_NAME).isFile())
      ? new String[]{"java", "--class-path", "../build/"+global.PROGRAM_NAME, source_file}
      : new String[]{"java", source_file};
      

    cmd = process.addLibArgs(cmd, true);
    String[] exec_args = cli.getExecArgs(args);
    stdout.print_debug("Passing the following arguments to test execution:", exec_args);

    cmd = process.concatArgs(cmd, exec_args);

    return process.runProcess(cmd, "test");
  }
  
  public static int runTest_native(String path, String[] args) {
    String[] cmd = new String[]{path};
    String[] exec_args = cli.getExecArgs(args);
    stdout.print_debug("Passing the following arguments to test execution:", exec_args);
    
    cmd = process.concatArgs(cmd, exec_args);
    return process.runProcess(cmd, "test");
  }
  
  public static void printTestFiles() {
    String[] testfiles = tests.getTestFiles();
    if (testfiles.length == 0) {
      stdout.print("No test source files are found in your project!");
      return;
    }
    String txt = "The following source files are in \"test/\":\n";
    for (String t : testfiles) {
      txt += "  * " + t + "\n";
    }
    stdout.print(txt);
  }
  
  private static String[] getTestFiles() {
    File f = new File("test");
    if (!f.isDirectory() || !f.canRead()) {return new String[0];}
    String[] files = new File("test").list();
    if (files == null || files.length == 0) {return new String[0];}

    ArrayList<String> source_files = new ArrayList<>();
    for (String file : files)
    {
      File test_f = new File("test/"+file);
      boolean validTest = misc.checkFileExtension(file, ".java") || test_f.canExecute();
      if (f.isFile() || validTest) {source_files.add(file);}
    }
    return source_files.toArray(new String[0]);
  }
}
