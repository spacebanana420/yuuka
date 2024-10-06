package yuuka;

import java.io.File;
import java.util.ArrayList;

public class tests {
  public static boolean runTest(String source_file, String[] args) {    
    String[] cmd = new String[]{"java", source_file};

    cmd = process.compiler_addlib(cmd, true);
    String[] exec_args = misc.getExecArgs(args);
    cmd = process.concatArgs(cmd, exec_args);

    return process.runProcess(cmd, "test") == 0;
  }

  public static String[] getTestFiles() {
    File f = new File("test");
    if (!f.isDirectory() || !f.canRead()) {return new String[0];}
    String[] files = new File("test").list();
    if (files == null || files.length == 0) {return new String[0];}

    ArrayList<String> source_files = new ArrayList<>();
    for (String file : files) {
      if (misc.checkFileExtension(file, ".java")) {
        source_files.add(file);
      }
    }
    return source_files.toArray(new String[0]);
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
}
