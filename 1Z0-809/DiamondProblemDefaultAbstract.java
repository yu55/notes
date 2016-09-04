interface Interface1 {
  void method();
}

interface Interface2 {
  default void method() {
    // default implementation
  }
}

class DiamondProblemDefaultAbstract implements Interface1, Interface2 {

  // without below method: error: DiamondProblemDefaultAbstract is not abstract and does not override abstract method method() in Interface1
  public void method() {
    Interface2.super.method();
  }

  public static void main(String[] args) {
  }
}

