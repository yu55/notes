interface OuterInterface {

  default void method() {
    class LocalInnerClass {}
  }

}

class ClassLocalInnerTest {

  private int field = 0;

  void method() {
    class LocalInnerClass {
      static final int localInnerField = 0;  // static without final is ILLEGAL
    }
    // interface LocalInnerInterface {}; // ILLEGAL!
  }

  void otherMethod(int argument) {

    int variable = 1;

    class LocalInnerClass {
      void method() {
        int var1 = field;
        int var2 = argument; // "error: local variables..." when "argument = 6" is UNCOMMENTED
        int var3 = variable; // "error: local variables..." when "variable = 7" is UNCOMMENTED

        field = 2;         // this is legal
        // argument = 3;   // "error: local variables referenced from an inner class must be final or effectively final"
        // variable = 4;   // "error: local variables referenced from an inner class must be final or effectively final"
      }
    }
      field = 5;           // this is legal
      // argument = 6;
      // variable = 7;
  }


  public static void main(String[] args) {}

}
