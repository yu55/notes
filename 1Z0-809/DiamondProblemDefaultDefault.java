interface Interface1 {
  default void method() {
    // default implementation 1
  }
}

interface Interface2 {
  default void method() {
    // default implementation 2
  }
}

class DiamondProblemDefaultDefault implements Interface1, Interface2 {

  // without below method: error: class DiamondProblemDefaultDefault inherits unrelated defaults for method() from types Interface1 and Interface2
  public void method() {
    Interface2.super.method();
  }

  public static void main(String[] args) {
  }
}

