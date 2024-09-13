package yuuka;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class fileops {
  public static ArrayList<String> getSourceFiles(String root_path) {
    String[] subpaths = new File(root_path).list();

    ArrayList<String> source_files = new ArrayList<>();
    for (String p : subpaths) {
      File f = new File(root_path + "/" + p);
      if (f.isFile() && f.canRead() && isSourceFile(p)) {
        source_files.add(root_path + "/" + p);
      }
      else if (f.isDirectory() && f.canRead()) {
        var files = getSourceFiles(root_path + "/" + p);
        source_files.addAll(files); 
      }
    }
    return source_files;
  }

  public static String[] getClassFiles(ArrayList<String> source_files) {
    String[] class_files = new String[source_files.size()];
    for (int i = 0; i < source_files.size(); i++) {
      var file = source_files.get(i);
      class_files[i] = file.replaceFirst(".java", ".class");
    }
    return class_files;
  }

  public static boolean deleteClassFiles(String[] class_files) {
    for (String file : class_files) {
      try {Files.delete(Path.of(file));}
      catch(IOException e) {return false;}
    }
    return true;
  }

  public static boolean deleteClassFiles(String path) {
    var paths = new File(path).list();
    for (String p : paths) {
      var f = new File(p);
      if (f.isFile() && isClassFile(p)) {
        try {Files.delete(Path.of(p));}
        catch(IOException e) {return false;}
      }
      else if (f.isDirectory()) {
        boolean result = deleteClassFiles(path + "/" + p);
        if (!result) {return false;}
      }
    }
    return true;
  }

  public static boolean isSourceFile(String name) {return checkFileExtension(name, ".java");}
  public static boolean isClassFile(String name) {return checkFileExtension(name, ".class");}
  public static boolean isJarFile(String name) {return checkFileExtension(name, ".jar");}

  private static boolean checkFileExtension(String name, String extension) {
    if (name.length() <= extension.length()) return false;
    int nl = name.length(); int el = extension.length();
    for (int i = 0; i < el; i++) {
      if (extension.charAt(i) != name.charAt(nl-el+i)) {return false;} 
    }
    return true;
  }
}
