package yuuka;

import yuuka.config.yuukaConfig;
import yuuka.cli.cli;
import yuuka.cli.help;

public class main {
  public static void main(String[] args) {
    int parse_break = cli.findParseBreak(args);
    if (cli.askedForHelp(args, parse_break)) {return;}
    
    yuukaConfig.parseConfig("build.yuuka");
    cli.assignGlobalValues(args, parse_break);

    boolean ranTask = cli.runTasks(args, parse_break);
    if (!ranTask) {System.out.println(help.getHelpMessage_small());}
  }
}
