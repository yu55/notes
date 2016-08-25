class A
{
   byte getStatusCode(Object obj) throws NullPointerException
   {
      if(obj != null ) return 127;
      else return -1;
   }
}
class B extends A
{
    byte getStatusCode(Object obj)
    {
        return 0;
    }
}

class OverridenNotThrowingExceptions {
    public static void main(String[] args) {
        System.out.println("I did compiled!");
    }
}
