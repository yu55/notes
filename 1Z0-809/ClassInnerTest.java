class OuterClass {

  private static int outerStaticField;
  private int outerInstanceField;
  private int i = 77;

  class InnerClass {
    static final int innerField = 0;       // static without final is NOT LEGAL
    private int innerInstanceField = 1;
    private int i = 55;
    void method() {
      outerInstanceField = 2;              // full access
      outerStaticField = 3;                // full access
      
      System.out.println("i=" + i);                                 // prints 55 (or 77 if inner "private int i = 55" doesn't exists)
      System.out.println("this.i=" + this.i);                       // prints 55
      System.out.println("OuterClass.this.i" + OuterClass.this.i);  // prints 77
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
    ic.method();
    OuterClass.InnerInterface ii = new OuterClass.InnerInterface() {}; //NOT LIKE THIS: new OuterClass().new InnerInterface() {};

  }

}
