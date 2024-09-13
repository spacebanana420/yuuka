package yuuka;

import java.io.File;
import java.io.IOException;

public class compiler {
  public static int runCompiler(String[] cmd, String working_directory) {
    try {
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
  public static String[] buildCommand(String source_path, String binary_path, String release) {
    var source_files = fileops.getSourceFiles(source_path);
    String[] cmd = new String[source_files.size() + 3];
    cmd[0] = binary_path;
    cmd[1] = "--release";
    cmd[2] = release;
    for (int i = 3; i < cmd.length; i++) {cmd[i] = source_files.get(i-3);}
    return cmd;
  }
}
