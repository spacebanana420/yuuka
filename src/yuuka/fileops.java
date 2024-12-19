package yuuka;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class fileops {
  public static ArrayList<String> getSourceFiles(String root_path) {
    return getFiles_generic(root_path, false, ".java");
  }

  public static ArrayList<String> getJarFiles(String root_path) {
    return getFiles_generic(root_path, false, ".jar");
  }

  public static ArrayList<String> getClassFiles(String root_path, boolean addlicenses) {
    return getFiles_generic(root_path, addlicenses, ".class");
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

  public static boolean deleteClassFiles(String path) {return deleteClassFiles(path, false, false);}

  public static boolean deleteClassFiles(String path, boolean deleteAll, boolean deleteDirectory) {
    if (!new File(path).isDirectory()) {return false;}
    var paths = new File(path).list();
    if (paths == null || paths.length == 0) {return true;}

    for (String p : paths)
    {
      String full_p = path + "/" + p;
      var f = new File(full_p);
      var isfile = f.isFile();
      if (f.isFile() && deletableFile(p, deleteAll)) {
        try {Files.delete(Path.of(full_p));}
        catch(IOException e) {return false;}
      }
      else if (f.isDirectory()) {
        boolean result = deleteClassFiles(full_p, deleteAll, deleteDirectory);
        if (!result) {return false;}
        if (!deleteDirectory) {continue;}
        
        try {Files.delete(Path.of(full_p));}
        catch(IOException e) {return false;}
      }
    }
    return true;
  }

  public static boolean deleteBuildFiles(String path) {return deleteClassFiles(path, true, true);}

  private static boolean deletableFile(String name, boolean deleteAll) {
    if (isJarFile(name)) {return false;}
    if (deleteAll) {return true;}
    return isClassFile(name) || name.equals("MANIFEST.MF");
  }

  public static boolean isSourceFile(String name) {return misc.checkFileExtension(name, ".java");}
  public static boolean isClassFile(String name) {return misc.checkFileExtension(name, ".class");}
  public static boolean isJarFile(String name) {return misc.checkFileExtension(name, ".jar");}

  public static String findMainClass() {
    String file_separator = System.getProperty("file.separator");
    String root = "src"+file_separator;
    return findMainClass("src", root, file_separator);
  }
  
  private static String findMainClass(String path, String root, String file_separator) {
    String[] paths = new File(path).list();
    if (paths == null) {paths = new String[0];}
    for (String p : paths)
    {
      String full_p = path + file_separator + p;
      var f = new File(full_p);
      boolean canRead = f.canRead();
      if (f.isFile() && canRead && p.contains("main.java"))
      {
        return
          (full_p)
          .replaceFirst(root, "")
          .replaceFirst(".java", "");
      }
      else if (f.isDirectory() && canRead)
      {
        String result = findMainClass(full_p, root, file_separator);
        if (!result.equals("main")) {return result;}
      }
    }
    return "main";
  }

  public static boolean copyLicensesToBuild() {return copyLicensesToBuild("src");}
  private static boolean copyLicensesToBuild(String path) {
    String[] subpaths = new File(path).list();
    if (subpaths == null || subpaths.length == 0) {return false;}

    int copied_licenses = 0;
    for (String subp : subpaths)
    {
      String path_in = path + "/" + subp;
      var f = new File(path_in);
      if (f.isFile() && isLicense(subp)) {
        String path_out = path_in.replaceFirst("src", "build");
        var pin = Path.of(path_in);
        var pout = Path.of(path_out);
        stdout.print_debug
        (
          "License file found in source"
          +"\n  Input path: " + path_in
          +"\n  Output path: " + path_out
        );
        try {Files.copy(pin, pout); copied_licenses++;}
        catch (IOException e) {stdout.print("Error copying license file " + subp + " into build!");}
      }
      else if (f.isDirectory()) {copyLicensesToBuild(path_in);}
    }
    return copied_licenses > 0;
  }

  private static ArrayList<String> getFiles_generic(String root_path, boolean checklicenses, String... file_extension) {
    String[] subpaths = new File(root_path).list();

    ArrayList<String> source_files = new ArrayList<>();
    for (String p : subpaths)
    {
      File f = new File(root_path + "/" + p);
      if (f.isFile() && f.canRead() && (misc.checkFileExtension(p, file_extension) || isLicense(p, checklicenses)))
      {
        source_files.add(root_path + "/" + p);
      }
      else if (f.isDirectory() && f.canRead())
      {
        var files = getFiles_generic(root_path + "/" + p, checklicenses, file_extension);
        source_files.addAll(files); 
      }
    }
    return source_files;
  }

  private static boolean isLicense(String path, boolean checkLicenses) {return checkLicenses && isLicense(path);}

  private static boolean isLicense(String path) {
    String name = new File(path).getName();
    return name.equals("LICENSE");
  }
}
