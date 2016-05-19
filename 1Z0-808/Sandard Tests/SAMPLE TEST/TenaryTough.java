//What will the following code print?

public class TenaryTough {
    public static void main(String[] args) {
        int x = 1;
        int y = 2;
        int z = x++; //z=1 x=2
        int a = --y; //a=1 y=1
        int b = z--; //b=1 z=0
        b += ++z; // b=2 z=1

        int answ = x>a ? y>b?y:b : x>z?x:z;

        System.out.println(answ); //2
    }
}

