class OuterClass {

  private static int outerStaticField;

  private int outerInstanceField;


  class InnerClass {
    static final int innerField = 0; // static without final is NOT LEGAL
    private int innerInstanceField = 1;
    void method() {
      outerInstanceField = 2; // full access
      outerStaticField = 3; // full access
    }
  }

  interface InnerInterface {} // this is static nested interface


  void method() {
    new InnerClass().innerInstanceField = 4; // full access
  }

}

class ClassInnerTest {

  public static void main(String[] args) {

    OuterClass.InnerClass ic = new OuterClass().new InnerClass(); // NOT LIKE THIS: new OuterClass.InnerClass();

    OuterClass.InnerInterface ii = new OuterClass.InnerInterface() {}; //NOT LIKE THIS: new OuterClass().new InnerInterface() {};

  }

}
