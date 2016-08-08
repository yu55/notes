public class TestClass {

    public static void main(String[] args) {

int a = 5, b = 7, k = 0;
Integer m = null;
Byte bb = null;

k = new Integer(a) + new Integer(b);  //1
k = new Integer(a) + b; //2
k = a + new Integer(b); //3
m = new Integer(a) + new Integer(b); //4

        bb = new Integer(a) + new Integer(b);
    }
}


