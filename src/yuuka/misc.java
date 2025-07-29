package yuuka;

public class misc {
  public static boolean isInt(String num) {
    try{
      Integer.parseInt(num);
      return true;
    }
    catch(NumberFormatException e) {return false;}
  }
  
  public static byte toByte(String num) {
    try{
      return Byte.parseByte(num);
    }
    catch(NumberFormatException e) {return -1;}
  }
  
  public static String removeExtension(String filename) {
    int point_i = -1;
    for (int i = filename.length()-1; i >= 0; i--) {
      if (filename.charAt(i) == '.') {point_i = i; break;}
    }
    if (point_i == -1) {return filename;}
    StringBuilder newname = new StringBuilder();
    for (int i = 0; i < point_i; i++) {
      newname.append(filename.charAt(i));
    }
    return newname.toString();
  }

  public static boolean checkFileExtension(String name, String extension) {
    if (name.length() <= extension.length()) return false;
    int nl = name.length(); int el = extension.length();
    int name_position = nl-el;
    for (int i = 0; i < el; i++) {
      if (extension.charAt(i) != name.charAt(name_position+i)) {return false;} 
    }
    return true;
  }
}
