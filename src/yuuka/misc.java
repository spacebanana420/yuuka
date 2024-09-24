package yuuka;

public class misc {
  public static String guessJARName(String main_class) {
    int first_slash = -1;
    String name = "";

    for (int i = 0; i < main_class.length(); i++) {
      char c = main_class.charAt(i);
      if (c == '/' || c == '\\') {first_slash = i; break;}
    }
    if (first_slash == -1) {return "program.jar";}
    for (int i = 0; i < first_slash; i++) {
      name += main_class.charAt(i);
    }
    return name + ".jar";
  }
  public static String removeExtension(String filename) {
    int point_i = -1;
    for (int i = filename.length()-1; i >= 0; i--) {
      if (filename.charAt(i) == '.') {point_i = i; break;}
    }
    if (point_i == -1) {return filename;}
    String newname = "";
    for (int i = 0; i < point_i; i++) {
      newname += filename.charAt(i);
    }
    return newname;
  }
}
