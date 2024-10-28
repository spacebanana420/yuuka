package yuuka;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class fileops {
  public static ArrayList<String> getSourceFiles(String root_path) {
    return getFiles_generic(root_path, ".java");
  }

  public static ArrayList<String> getJarFiles(String root_path) {
    return getFiles_generic(root_path, ".jar");
  }

  public static ArrayList<String> getClassFiles(String root_path) {
    return getFiles_generic(root_path, ".class");
  }

  public static ArrayList<String> removeParent(ArrayList<String> files, String parent) {
    ArrayList<String> new_files = files;
    for (int i = 0; i < files.size(); i++) {
      new_files.set(i, new_files.get(i).replaceFirst(parent, ""));
    }
    return new_files;
  }

  public static boolean deleteClassFiles(String[] class_files) {
    for (String file : class_files) {
      try {Files.delete(Path.of(file));}
      catch(IOException e) {return false;}
    }
    return true;
  }

  public static boolean deleteClassFiles(String path) {
    if (!new File(path).isDirectory()) {return false;}
    var paths = new File(path).list();
    if (paths == null) {return true;}

    for (String p : paths) {
      String full_p = path + "/" + p;
      var f = new File(full_p);
      if (f.isFile() && (isClassFile(p) || p.equals("MANIFEST.MF"))) {
        try {Files.delete(Path.of(full_p));}
        catch(IOException e) {return false;}
      }
      else if (f.isDirectory()) {
        boolean result = deleteClassFiles(full_p);
        if (!result) {return false;}
        try {Files.delete(Path.of(full_p));}
        catch(IOException e) {return false;}
      }
    }
    return true;
  }

  public static boolean isSourceFile(String name) {return misc.checkFileExtension(name, ".java");}
  public static boolean isClassFile(String name) {return misc.checkFileExtension(name, ".class");}
  public static boolean isJarFile(String name) {return misc.checkFileExtension(name, ".jar");}

  public static String findMainClass(String path) {
    String file_separator = System.getProperty("file.separator");
    String root = "src"+file_separator;

    if (new File(path + "/main.java").isFile()) {
      return
        (path + file_separator + "main")
        .replaceFirst(root, "");
    }
    String[] paths = new File(path).list();
    if (paths == null) {paths = new String[0];}
    for (String p : paths) {
      String full_p = path + file_separator + p;
      var f = new File(full_p);
      if (f.isFile() && p.contains("main.java")) {
        return
          (full_p)
          .replaceFirst(root, "")
          .replaceFirst(".java", "");
      }
      else if (f.isDirectory()) {
        String result = findMainClass(full_p);
        if (!result.equals("main")) {return result;}
      }
    }
    return "main";
  }

  private static ArrayList<String> getFiles_generic(String root_path, String file_extension) {
    String[] subpaths = new File(root_path).list();

    ArrayList<String> source_files = new ArrayList<>();
    for (String p : subpaths) {
      File f = new File(root_path + "/" + p);
      if (f.isFile() && f.canRead() && misc.checkFileExtension(p, file_extension)) {
        source_files.add(root_path + "/" + p);
      }
      else if (f.isDirectory() && f.canRead()) {
        var files = getFiles_generic(root_path + "/" + p, file_extension);
        source_files.addAll(files); 
      }
    }
    return source_files;
  }
}
