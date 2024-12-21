package yuuka;

import yuuka.config.yuukaConfig;
import yuuka.cli.cli;
import yuuka.cli.help;

public class main {
  public static void main(String[] args) {
    yuukaConfig.parseConfig("build.yuuka");

    boolean askedHelp = cli.parseOptions(args);
    if (askedHelp) {return;}
    boolean ranTask = cli.parseTasks(args);
    if (!ranTask) {System.out.println(help.getHelpMessage_small());}
  }
}
