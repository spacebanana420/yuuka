package yuuka.cli;

public class parser {
  public static int findArgument(String[] args, int parse_break, String... arg) {
    for (String a : arg) {
      int i = findArgument(args, parse_break, a);
      if (i != -1) {return i;}
    }
    return -1;
  }
  
  public static int findArgument(String[] args, int parse_break, String arg) {
    for (int i = 0; i < parse_break; i++) {
      if (args[i].equals(arg)) {return i;}
    }
    return -1;
  }
  
  public static String getArgumentValue(String[] args, String arg, int parse_break) {
    int i = findArgument(args, parse_break, arg);
    if (i == -1 || i == args.length-1) {return null;}
    String value = args[i+1];
    if (value == null || value.charAt(0) == '-') {return null;}
    return value; 
  }

  public static boolean hasArgument(String[] args, String arg, int parse_break) {
    return findArgument(args, parse_break, arg) != -1;
  }
  
  public static boolean hasArgument(String[] args, int parse_break, String... arg) {
    for (String a : arg) {
      if (findArgument(args, parse_break, a) != -1) {return true;}
    }
    return false;
  }
  
  public static boolean hasArgumentValue(String[] args, int i) {
    return
      i < args.length-1
      && !args[i+1].isEmpty()
      && args[i+1].charAt(0) != '-'
      && !isArgumentTask(args[i+1]);
  }

  public static boolean isArgumentTask(String arg) {
    return
      arg.equals("init")
      || arg.equals("build")
      || arg.equals("build-native")
      || arg.equals("package")
      || arg.equals("packagelib")
      || arg.equals("run")
      || arg.equals("test")
      || arg.equals("tests")
      || arg.equals("create-test")
      || arg.equals("clean")
      || arg.equals("install")
      || arg.equals("install-native")
      || arg.equals("uninstall")
    ;
  }

  public static boolean isOption(String arg, String opt1, String opt2) {return arg.equals(opt1) || arg.equals(opt2);}
  public static boolean isOption(String arg, String opt) {return arg.equals(opt);}
}
