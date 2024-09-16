package yuuka;

public class stdout {
  public static void print(String message) {
    if (!globalvariables.SILENT) {System.out.println(message);}
  }

  public static void print(String title, String[] contents) {
    if (!globalvariables.SILENT) {printSeq(title, contents);}
  }

  public static void print_verbose(String message) {
    if (globalvariables.VERBOSE && !globalvariables.SILENT) {System.out.println(message);}
  }

  public static void print_verbose(String title, String[] contents) {
    if (globalvariables.VERBOSE && !globalvariables.SILENT) {printSeq(title, contents);}
  }

  private static void printSeq(String title, String[] contents) {
    String txt = title;
    for (String c : contents) {txt += "\n  * " + c;}
    
    System.out.println(txt);
  }
}
