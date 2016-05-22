public class Deer {
 public Deer() {System.out.print("Deer");}
 public Deer(int age) { System.out.print("DeerAge"); }
 private boolean hasHorns() { return false; }
 public static void main(String[] args) {
  Deer deer = new Reindeer(5);
  System.out.println(","+deer.hasHorns()); // "false"

  Deer2 deer2 = new Reindeer2(5);
//  System.out.println(","+deer2.hasHorns()); //  error: hasHorns() has private access in Deer2

  Deer3 deer3 = new Reindeer3(5);
  System.out.println(","+deer3.hasHorns()); // "true"

 }
}

 class Reindeer extends Deer {
  public Reindeer(int age) { System.out.print("Reindeer"); }
  public boolean hasHorns() {return true; }
}


class Deer2 {
 public Deer2() {System.out.print("Deer2");}
 public Deer2(int age) { System.out.print("DeerAge2"); }
 private boolean hasHorns() { return false; }
}

 class Reindeer2 extends Deer2 {
  public Reindeer2(int age) { System.out.print("Reindeer2"); }
  public boolean hasHorns() {return true; }
}


class Deer3 {
 public Deer3() {System.out.print("Deer3");}
 public Deer3(int age) { System.out.print("DeerAge3"); }
 public boolean hasHorns() { return false; }
}

 class Reindeer3 extends Deer3 {
  public Reindeer3(int age) { System.out.print("Reindeer3"); }
  public boolean hasHorns() {return true; }
}


