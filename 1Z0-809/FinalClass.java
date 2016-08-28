final class FinalClass {

    int counter;

    public static void main(String[] args) {
        FinalClass fc = new FinalClass();
        // new FinalClass() {} - error: cannot inherit from final FinalClass
        fc.counter = 1; // field is not final (only methods are final)
        System.out.println("fc.counter=" + fc.counter);
    }
}

