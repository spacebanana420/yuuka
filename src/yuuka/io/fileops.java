package yuuka.io;

import yuuka.misc;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

//General file I/O tasks
public class fileops {
  //Read a text file into a list of lines
  public static ArrayList<String> readFile(String filePath) {
    var fileObject = new File(filePath);
    String fileText;
    if (!fileObject.isFile() || !fileObject.canRead()) return null;
    try {
      var is = new FileInputStream(filePath);
      fileText = new String(is.readAllBytes());
      is.close();
    }
    catch (IOException e) {
      stdout.error("Failed to read file at path " + filePath + "\nYou might lack the required permissions to read it");
      return null;
    }

    var fileLines = new ArrayList<String>();
    var line = new StringBuilder();
    for (int i = 0; i < fileText.length(); i++) {
      char c = fileText.charAt(i);
      if (c == '\n') {
        if (line.length() == 0) continue;
        fileLines.add(line.toString());
        line = new StringBuilder();
        continue;
      }
      line.append(c);
    }
    if (line.length() > 0) fileLines.add(line.toString());
    return fileLines;
  }

  //Need to be refactored to not use getFiles_generic()
  public static ArrayList<String> getSourceFiles(String root_path) {
    return getFiles_generic(root_path, false, ".java");
  }
  public static ArrayList<String> getJarFiles(String root_path) {
    return getFiles_generic(root_path, false, ".jar");
  }
  public static ArrayList<String> getClassFiles(String root_path, boolean addlicenses) {
    return getFiles_generic(root_path, addlicenses, ".class");
  }

  //Used to remove the root of a relative directory, e.g. removing build from build/yuuka/main.class
  public static ArrayList<String> removeParent(ArrayList<String> files, String parent) {
    ArrayList<String> new_files = files;
    for (int i = 0; i < files.size(); i++) {
      new_files.set(i, new_files.get(i).replaceFirst(parent, ""));
    }
    return new_files;
  }

  //Delete clutter class files throughout the project, used when running "yuuka clean"
  public static void deleteClassFiles() {
    ArrayList<String> files = getFiles("src");
    files.addAll(getFiles("test"));
    files.addAll(getFiles("lib"));
    
    for (String file : files) {
      if (!misc.checkFileExtension(file, ".class")) continue;
      deleteFile(file);
    }
  }

  //Delete everything inside build directory, used when running "yuuka clean"
  public static void deleteBuild() {
    deleteDirectory("build");
    new File("build").mkdir();
  }


  //Delete old class, license and JAR files as well as executables before compiling the project in its latest state
  public static void cleanBeforeBuild() {
    ArrayList<String> files = getFiles("build");

    for (String file : files) {
      if (isClassFile(file) || isJarFile(file) || isLicense(file) || new File(file).canExecute()) deleteFile(file);
    }
  }

  //Delete the directories, class files and license files in "build" after creating a JAR to clean up clutter
  public static void cleanAfterBuild() {
    ArrayList<String> files = getFiles("build");
    ArrayList<String> directories = getDirectories("build");

    for (String file : files) { //Delete clutter files
      if (isClassFile(file) || isLicense(file)) deleteFile(file);
    }
    for (String dir : directories) { //Also (try to) delete directories after having emptied them
      deleteFile(dir);
    }
  }

  public static String findMainClass() {
    char file_separator = System.getProperty("file.separator").charAt(0);
    return findMainClass("src", file_separator);
  }

  //Retrieve all files recursively from a path
  private static ArrayList<String> getDirectories(String currentPath) {return getPaths(currentPath, false);}
  //Retrieve all directories recursively from a path
  private static ArrayList<String> getFiles(String currentPath) {return getPaths(currentPath, true);}

  //Can retrieve either files or directories from a path
  private static ArrayList<String> getPaths(String currentPath, boolean getFiles) {
    var files = new ArrayList<String>();
    String[] subpaths = new File(currentPath).list();
    
    for (String subpath : subpaths) {
      String fullPath = currentPath + "/" + subpath;
      File f = new File(fullPath);
      boolean isFile = f.isFile();
      boolean isDir = !isFile && f.isDirectory();
      boolean validPath = (getFiles && isFile) || (!getFiles && isDir);

      if (isDir) files.addAll(getPaths(fullPath, getFiles));
      if (validPath) files.add(fullPath);
    }
    return files;
  }

  //Filter the files present in a list to only have files with a certain extension
  private static void filterFiles(ArrayList<String> files,  String extension) {
    for (int i = 0; i < files.size(); i++) {
      String file = files.get(i);
      if (!misc.checkFileExtension(file, extension)) files.remove(i);
    }
  }

  private static boolean deleteFile(String filePath) {
    try {
      Path p = Path.of(filePath);
      Files.delete(p);
      return true;
    }
    catch (IOException e) {
      stdout.error("Failed to delete the file at path " + filePath);
      return false;
    }
  }

  private static boolean deleteDirectory(String dirPath) {
    String[] subpaths = new File(dirPath).list();
    boolean succeeded;

    for (String subpath : subpaths) {
      String fullPath = dirPath+"/"+subpath;
      if (new File(subpath).isDirectory()) {
        succeeded = deleteDirectory(fullPath);
        if (!succeeded) return false;
      }
      else{
        succeeded = deleteFile(fullPath);
        if (!succeeded) return false;
      }
    }
    succeeded = deleteFile(dirPath);
    return succeeded;
  }
  

  private static boolean deletableFile(String name, boolean deleteAll, boolean deleteJars) {
    boolean is_jar = isJarFile(name);
    if (is_jar && !deleteJars) return false;
    return deleteAll || (is_jar && deleteJars) || isClassFile(name) || name.equals("MANIFEST.MF");
  }

  //Autodetect project's main class by trying to find main.java, then return path
  //Path follows the sytle of e.g yuuka/main
  //Messy, should probably be replaced by getFiles() and filterFiles()
  private static String findMainClass(String path, char file_separator) {
    String[] paths = new File(path).list();
    if (paths == null) return null;
    
    for (String subpath : paths)
    {
      String full_path = path + file_separator + subpath;
      File f = new File(full_path);
      if (!f.canRead()){
        stdout.print_debug("Found unreadable path while trying to autodetect project's main class: " + full_path);
        continue;
      }
      
      if (f.isFile() && subpath.equals("main.java")) {
        return
          full_path
          .replaceFirst(".java", "")
          .replaceFirst("src"+file_separator, "")
        ;
      }
      else if (f.isDirectory()) {
        String result = findMainClass(full_path, file_separator);
        if (result != null) return result;
      }
    }
    return null;
  }

  //If license files are found inside the source code, they should also be in the build and library JAR
  public static boolean copyLicensesToBuild() {return copyLicensesToBuild("src");}
  private static boolean copyLicensesToBuild(String path) {
    String[] subpaths = new File(path).list();
    if (subpaths == null || subpaths.length == 0) return false;

    int copied_licenses = 0;
    for (String subp : subpaths)
    {
      String path_in = path + "/" + subp;
      File f = new File(path_in);
      
      if (f.isFile() && isLicense(subp)) {
        String path_out = path_in.replaceFirst("src", "build");
        String debugMessage = "License file found in source\n  Current path: " + path_in + "\n  Destination path: " + path_out;
        stdout.print_debug(debugMessage);
        try {Files.copy(Path.of(path_in), Path.of(path_out)); copied_licenses++;}
        catch (IOException e) {stdout.print("Error copying license file " + subp + " into build!");}
      }
      else if (f.isDirectory()) {copyLicensesToBuild(path_in);}
    }
    return copied_licenses > 0;
  }

  //Legacy function and messy, should be replaced by filterFiles()
  //Generic function for getting files that match an extension, optionally also get license files
  //Returns the files in relative paths
  private static ArrayList<String> getFiles_generic(String root_path, boolean checklicenses, String file_extension) {
    String[] subpaths = new File(root_path).list();
    ArrayList<String> source_files = new ArrayList<>();
    if (subpaths == null) return source_files;

    for (String p : subpaths)
    {
      File f = new File(root_path + "/" + p);
      if (!f.canRead()) continue;
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
