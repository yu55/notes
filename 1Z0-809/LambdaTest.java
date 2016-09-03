@FunctionalInterface
interface SomeInterface {
  void method();
}

class LambdaTest {
  public static void main(String[] args) {
    String mosquito = "bzzz";
    SomeInterface si = () -> 
                       { 
                         System.out.println(mosquito); // "local variables referenced from a lambda expression must be final or effectively final" when "mosquito = "dead"" UNCOMMENTED
                         //mosquito = "bite";          // ILLEGAL: "error: local variables referenced from a lambda expression must be final or effectively final"
                       };
     //mosquito = "dead";
  }
}
