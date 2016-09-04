class Interface1 {

  // if below not public: DiamondProblemDefaultClass.java:14: error: method() in Interface1 cannot implement method() in Interface2
  public void method() {
    // final implementation
  }
}

interface Interface2 {
  default void method() {
    // default implementation
  }
}

class DiamondProblemDefaultClass extends Interface1 implements Interface2 {
  public static void main(String[] args) {
    new DiamondProblemDefaultClass().method(); // "class wins" - method() from class is used
  }
}

