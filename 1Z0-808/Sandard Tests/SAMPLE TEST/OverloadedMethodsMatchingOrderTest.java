public class OverloadedMethodsMatchingOrderTest {

    public static void main(String[] args) {
        byte i = 1;
        byte j = 2;
        new OverloadedMethodsMatchingOrderTest().glide(i, j);
    }

    public void glide(byte i, byte j) {
        System.out.println("byte byte");
    }

    public void glide(short i, short j) {
        System.out.println("short short");
    }

    public void glide(int i, int j) {
        System.out.println("int int");
    }

    public void glide(long i, long j) {
        System.out.println("long long");
    }

    public void glide(float i, float j) {
        System.out.println("float float");
    }

    public void glide(double i, double j) {
        System.out.println("double double");
    }

    public void glide(Byte i, Byte j) {
        System.out.println("Byte Byte");
    }

    public void glide(byte... ij) {
        System.out.println("byte...");
    }

    public void glide(short... ij) {
        System.out.println("short...");
    }

    public void glide(int... ij) {
        System.out.println("short...");
    }

    public void glide(long... ij) {
        System.out.println("long...");
    }

    public void glide(float... ij) {
        System.out.println("float...");
    }

    public void glide(double... ij) {
        System.out.println("double...");
    }

    public void glide(Object i, Object j) {
        System.out.println("Object Object");
    }

    public void glide(Short i, Short j) { // won't be matched
        System.out.println("Short Short");
    }

    public void glide(boolean i, boolean j) { // won't be matched
        System.out.println("boolean boolean");
    }

    public void glide(char i, char j) { // won't be matched
        System.out.println("char char");
    }
}
