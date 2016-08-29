class OuterClass {

  private static int outerField;


  static class StaticNestedClass {
    private int nestedField;
    void method() {
      outerField = 1; // access only to outer static members
    }
  }

  class InnerClass {}

  private static class PrivateStaticNestedClass {}

  static interface StaticNestedInterface {}

  interface ThisIsStaticNestedInterfaceToo {}


  void method() {
    new StaticNestedClass().nestedField = 2; // full access
  }

}

interface OuterInterface {

  static class StaticNestedClass {}

  class AgainStaticNestedClass {} // any field in interface is: public static
  
}

class ClassStaticNestedTest {

  public static void main(String[] args) {

    OuterClass.StaticNestedClass snc = new OuterClass.StaticNestedClass();

    OuterClass.InnerClass ic = new OuterClass().new InnerClass(); // NOT LIKE THIS: new OuterClass.InnerClass();

    // NO! Private modifier!
    //OuterClass.PrivateStaticNestedClass psnc = new OuterClass.PrivateStaticNestedClass();

    OuterClass.StaticNestedInterface sni = new OuterClass.StaticNestedInterface() {};

    OuterClass.ThisIsStaticNestedInterfaceToo tisnit = new OuterClass.ThisIsStaticNestedInterfaceToo() {};



    OuterInterface.StaticNestedClass oisnc = new OuterInterface.StaticNestedClass();

    OuterInterface.AgainStaticNestedClass asnc = new OuterInterface.AgainStaticNestedClass();

  }

}
