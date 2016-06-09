public class LastPrintedException {

  public static void main(String[] args) {
    System.out.print("a"); // will be printed
    try {
      System.out.print("b"); // will be printed
      throw new IllegalArgumentException();
    } catch (IllegalArgumentException e) {
      System.out.print("c"); // will be printed
      throw new RuntimeException("1");
    } catch (RuntimeException e) {
      System.out.print("d");
      throw new RuntimeException("2");
    } finally {
      System.out.print("e"); // will be printed
      throw new RuntimeException("3"); // will be printed

    }
  }

}

