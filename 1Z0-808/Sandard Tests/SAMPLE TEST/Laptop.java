public class Laptop {
  public void start() {
    try {
      System.out.print("Starting up ");
      throw new Exception();
    } catch (Exception e) {
      System.out.print("Problem ");
      System.exit(0);
    } finally {
      System.out.print("Shutting down "); // this won't be executed - JVM is already dead
    }
  }

  public static void main(String[] args) {
    new Laptop().start();
  }
}

