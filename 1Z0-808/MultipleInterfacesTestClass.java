
//Which statements about the following code are correct?

interface House{
  public default String getAddress(){
     return "101 Main Str";
  }
}

interface Bungalow extends House{
  public default String getAddress(){
     return "101 Smart Str";
  }
}

class MyHouse implements Bungalow, House{

}

public class MultipleInterfacesTestClass {

  public static void main(String[] args) {
    House ci = new MyHouse();  //1
    System.out.println(ci.getAddress()); //2
  }
}

// Code for interface House will cause compilation to fail.
// Code for interface Bungalow will cause compilation to fail.
// Code for class MyHouse will cause compilation to fail.
// Line at //1 will cause compilation to fail.
// Line at //2 will cause compilation to fail.
// * The code will compile successfully.

