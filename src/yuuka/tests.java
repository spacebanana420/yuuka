package yuuka;

public class tests {
  public static boolean runTest(String source_file, String[] args) {    
    String[] cmd = new String[]{"java", source_file};

    cmd = process.compiler_addlib(cmd, true);
    String[] exec_args = misc.getExecArgs(args);
    cmd = process.concatArgs(cmd, exec_args);

    return process.runProcess(cmd, "test") == 0;
  }
}
