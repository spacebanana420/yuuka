package yuuka.libfetch;

public class MavenLibrary {
  public String[] properties = new String[]{null, null, null};

  public String group() {return properties[0];}
  public String name() {return properties[1];}
  public String version() {return properties[2];}
  public String jar_name() {return properties[1]+"-"+properties[2]+".jar";}

  public boolean isValid() {return properties[0] != null && properties[1] != null && properties[2] != null;}
}
