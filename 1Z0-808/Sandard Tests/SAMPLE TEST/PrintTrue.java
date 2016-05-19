public class PrintTrue {
    public static void main(String[] args) {
        System.out.println(Boolean.parseBoolean("true"));
        System.out.println(new Boolean(null));
//        System.out.println(new Boolean());
        System.out.println(new Boolean("true"));
        System.out.println(new Boolean("trUE"));
    }
}
