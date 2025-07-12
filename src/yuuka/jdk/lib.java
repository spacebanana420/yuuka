package yuuka.jdk;

import java.io.File;
import java.util.ArrayList;
import yuuka.global;

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

  public static ArrayList<String> getLibraryJars(boolean change_base_directory) {
    var jars = fileops.getJarFiles("lib");
    if (change_base_directory) {changeBaseDirectory(jars);}
    return jars;
  }
  
  //Include library JARs in the command for running or compiling code
  public static String[] addLibArgs(String[] cmd, boolean change_base_directory) {
    if (global.INGORE_LIB || !projectHasLibraries()) {return cmd;}
    
    ArrayList<String> jars = getLibraryJars(change_base_directory);
    String[] libargs = getLibArgs(jars);
    
    String[] finalcmd = new String[cmd.length + libargs.length];
    finalcmd[0] = cmd[0];
    for (int i = 0; i < libargs.length; i++) {finalcmd[i+1] = libargs[i];}
    for (int i = 1; i < cmd.length; i++) {finalcmd[i+libargs.length] = cmd[i];}
    return finalcmd;
  }
  
  private static String[] getLibArgs(ArrayList<String> jar_files) { //should return an arraylist instead
    String[] cli_args = new String[jar_files.size() * 2];
    int files_i = 0;
    
    for (int i = 0; i < cli_args.length; i+=2) {
      cli_args[i] = "--class-path";
      cli_args[i+1] = jar_files.get(files_i);
      files_i++;
    }
    return cli_args;
  }
  
  //For executing library-related commands with build/ as the working directory
  //Some commands are executed at the root of the project, but others are executed using build/ as the working directory
  private static void changeBaseDirectory(ArrayList<String> jar_files) {
    for (int i = 0; i < jar_files.size(); i++) {
      jar_files.set(i, "../" + jar_files.get(i));
    }
  }
}
