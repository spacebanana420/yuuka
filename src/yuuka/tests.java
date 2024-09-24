package yuuka;

import java.util.ArrayList;

//contains duplicate code, replace later maybe
public class tests {
  public static boolean runTest(String source_file) {    
    int result = compile(source_file);
    if (result != 0) {return false;}
    
    result = run(misc.removeExtension(source_file));
    fileops.deleteClassFiles("test");
    return result == 0;
  }

  private static int compile(String source_file) {
    ArrayList<String> cmd = new ArrayList<>();
    cmd.add("javac");
    if (globalvariables.DISABLE_WARNINGS) {cmd.add("-nowarn");}
    cmd.add(source_file);

    String[] command = cmd.toArray(new String[0]);
    command = process.compiler_addlib(command, true);

    return process.runProcess(command, "test");
  }

  private static int run(String main_class) {
    String[] cmd = new String[]{"java", main_class};

    cmd = process.compiler_addlib(cmd, true);
    return process.runProcess(cmd, "test");
  }
}
