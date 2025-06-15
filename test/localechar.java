public class localechar {
    public static void main(String[] args) {
      //It's supposed to be 48 on UTF-8
      //If it is then this is a reliable way to make character-to-digit conversions and arithmetic a locale-agnostic process
      System.out.println((int)'0');
    }
}
