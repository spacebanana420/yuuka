package yuuka.jdk;

import java.io.File;
import java.util.ArrayList;

import yuuka.io.fileops;

public class lib {
  public static boolean projectHasLibraries() {
    var f = new File("lib");
    if (!f.isDirectory()) {return false;}

    var file_list = f.list();
    if (file_list == null || file_list.length == 0) {return false;}

    for (String file : file_list) {if (file.contains(".jar")) {return true;}}
    return false;
  }

  public static ArrayList<String> getLibraryJars() {
    return fileops.getJarFiles("lib");
  }
  public static String[] getLibArgs(ArrayList<String> jar_files) {
    return mkArgs(jar_files, "--class-path");
  }
  
  public static ArrayList<String> changeBaseDirectory(ArrayList<String> jar_files) {
    ArrayList<String> new_files = jar_files;
    for (int i = 0; i < jar_files.size(); i++) {
      new_files.set(i, "../" + jar_files.get(i));
    }
    return new_files;
  }

  private static String[] mkArgs(ArrayList<String> jar_files, String arg) {
    String[] cli_args = new String[jar_files.size() * 2];
    int files_i = 0;
    for (int i = 0; i < cli_args.length; i+=2) {
      cli_args[i] = arg; cli_args[i+1] = jar_files.get(files_i);
      files_i++;
    }
    return cli_args;
  }
}
