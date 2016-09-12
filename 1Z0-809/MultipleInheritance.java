interface Interface1 {
  default void method() {
    System.out.println("Instance");
  }
}

interface Interface2 {
  static void method() {
    System.out.println("Static");
  }
}

class MultipleInheritance implements Interface1, Interface2 {

  public static void main(String args[]) {
    //MultipleInheritance.method();          // WON'T work: error: non-static method method() cannot be referenced from a static context
    Interface2.method();                     // prints "Static"
    new MultipleInheritance().method();      // prints "Instance"

    _34.test();
    _6.test();
  }

}

//-----------------------------------------------------------------------------

interface Interface3 {
  static void method() {
    System.out.println("Static 3");
  }
}

interface Interface4 {
  static void method() {
    System.out.println("Static 4");
  }
}

class _34 implements Interface3, Interface4 {
  static void test() {
    //method();                              // WON'T work: error: cannot find symbol
    Interface3.method();                     // prints "Static 3"
    Interface4.method();                     // prints "Static 4"
  }
}

//-----------------------------------------------------------------------------

interface Interface5 {
  static void method() {}
  //default void method() {}                 // WON'T work: error: method method() is already defined in interface Interface5
}

//-----------------------------------------------------------------------------

interface Interface6 {
  int VALUE = 0;
  int OTHER = 1;
}

class _6_1 {
  static int VALUE = 2;
  static int UNKNOWN = 3;
}

class _6 extends _6_1 implements Interface6 {
  static int OTHER = 4;
  static void test() {
    //System.out.println("VALUE=" + VALUE);                       // error: reference to VALUE is ambiguous
    //System.out.println("_6.VALUE=" + _6.VALUE);                 // error: reference to VALUE is ambiguous
    System.out.println("_6_1.VALUE=" + _6_1.VALUE);             // prints 2
    System.out.println("Interface6.VALUE=" + Interface6.VALUE); // prints 0
    System.out.println("OTHER=" + OTHER);                       // prints 4
    System.out.println("Interface6.OTHER=" + Interface6.OTHER); // prints 1
    System.out.println("_6.OTHER=" + _6.OTHER);                 // prints 4
    System.out.println("UNKNOWN=" + UNKNOWN);                   // prints 3
    System.out.println("_6_1.UNKNOWN=" + _6_1.UNKNOWN);         // prints 3
    System.out.println("_6.UNKNOWN=" + _6.UNKNOWN);             // prints 3
  }
}
