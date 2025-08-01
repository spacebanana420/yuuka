package yuuka.io;

import yuuka.misc;

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

  public static boolean deleteClassFiles(String path) {return deleteClassFiles(path, false, false, false);}
  public static boolean deleteBuildFiles(String path) {return deleteClassFiles(path, true, true, false);}
  public static boolean deleteBuildFiles_all(String path) {return deleteClassFiles(path, true, true, true);}
  public static boolean deleteBeforeCompile(String path) {return deleteClassFiles(path, false, true, true);}

  private static boolean deleteClassFiles(String path, boolean deleteAll, boolean deleteDirectory, boolean deleteJars) {
    if (!new File(path).isDirectory()) {return false;}
    String[] paths = new File(path).list();
    if (paths == null || paths.length == 0) {return true;}

    try {for (String sub_path : paths)
    {
      String full_path = path + "/" + sub_path;
      Path full_p = Path.of(full_path);
      File f = new File(full_path);
      
      if (f.isFile() && deletableFile(sub_path, deleteAll, deleteJars)) {
        Files.delete(full_p);
      }
      else if (f.isDirectory()) {
        boolean result = deleteClassFiles(full_path, deleteAll, deleteDirectory, deleteJars);
        if (!result) {return false;}
        if (deleteDirectory) {Files.delete(full_p);}
      }
    }} catch(IOException e) {return false;}
    return true;
  }

  private static boolean deletableFile(String name, boolean deleteAll, boolean deleteJars) {
    boolean is_jar = isJarFile(name);
    if (is_jar && !deleteJars) {return false;}
    return deleteAll || (is_jar && deleteJars) || isClassFile(name) || name.equals("MANIFEST.MF");
  }

  public static String findMainClass() {
    char file_separator = System.getProperty("file.separator").charAt(0);
    return findMainClass("src", file_separator);
  }
  
  private static String findMainClass(String path, char file_separator) {
    String[] paths = new File(path).list();
    if (paths == null) {return null;}
    
    for (String subpath : paths)
    {
      String full_path = path + file_separator + subpath;
      File f = new File(full_path);
      if (!f.canRead()) {continue;}
      
      if (f.isFile() && subpath.equals("main.java")) {
        return
          full_path
          .replaceFirst(".java", "")
          .replaceFirst("src"+file_separator, "")
        ;
      }
      else if (f.isDirectory()) {
        String result = findMainClass(full_path, file_separator);
        if (result != null) {return result;}
      }
    }
    return null;
  }

  public static boolean copyLicensesToBuild() {return copyLicensesToBuild("src");}
  private static boolean copyLicensesToBuild(String path) {
    String[] subpaths = new File(path).list();
    if (subpaths == null || subpaths.length == 0) {return false;}

    int copied_licenses = 0;
    for (String subp : subpaths)
    {
      String path_in = path + "/" + subp;
      File f = new File(path_in);
      
      if (f.isFile() && isLicense(subp)) {
        String path_out = path_in.replaceFirst("src", "build");
        stdout.print_debug
        (
          "License file found in source"
          +"\n  Input path: " + path_in
          +"\n  Output path: " + path_out
        );
        try {Files.copy(Path.of(path_in), Path.of(path_out)); copied_licenses++;}
        catch (IOException e) {stdout.print("Error copying license file " + subp + " into build!");}
      }
      else if (f.isDirectory()) {copyLicensesToBuild(path_in);}
    }
    return copied_licenses > 0;
  }

  private static ArrayList<String> getFiles_generic(String root_path, boolean checklicenses, String file_extension) {
    String[] subpaths = new File(root_path).list();
    ArrayList<String> source_files = new ArrayList<>();
    if (subpaths == null) {return source_files;}

    for (String p : subpaths)
    {
      File f = new File(root_path + "/" + p);
      if (!f.canRead()) {continue;}
      if (f.isFile() && (misc.checkFileExtension(p, file_extension) || isLicense(p, checklicenses)))
      {
        source_files.add(root_path + "/" + p);
      }
      else if (f.isDirectory())
      {
        ArrayList<String> files = getFiles_generic(root_path + "/" + p, checklicenses, file_extension);
        source_files.addAll(files); 
      }
    }
    return source_files;
  }

  private static boolean isLicense(String path, boolean checkLicenses) {return checkLicenses && isLicense(path);}
  private static boolean isLicense(String path) {return new File(path).getName().equals("LICENSE");}
  
  private static boolean isClassFile(String name) {return misc.checkFileExtension(name, ".class");}
  private static boolean isJarFile(String name) {return misc.checkFileExtension(name, ".jar");}
}
