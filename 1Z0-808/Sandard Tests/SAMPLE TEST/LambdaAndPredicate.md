 Lambda syntax:

`a -> a.canHop()`

or more verbose:

`(Animal a) -> { return a.canHop(); }`

for interface:

```
public class CheckIfHopper implements CheckTrait {
   public boolean test(Animal a) {
     return a.canHop();
   }
}
```


other working examples:

`print(() -> true);`

`print(a -> a.startsWith("test"));`

`print((String a) -> a.startsWith("test"));`

`print((a, b) -> a.startsWith("test"));`

`print((String a, String b) -> a.startsWith("test"));`


not working examples:

`print(a, b -> a.startsWith("test")); // missing parentheses`

`print(a -> {a.startsWith("test"); }); // missing return`

`print(a -> { return a.startsWith("test") }); //missing semicolon`


redeclare local variable is not permitted:

`(a, b) -> { int a = 0; return 5; }`

`(a, b) -> { int c = 0; return 5;} // OK`


Predicate syntax:

```
public interface Predicate<T> {
   boolean test(T t);
}
```

Predicate in ArrayList:

```
List<String> bunnies = new ArrayList<>();
bunnies.add("long ear");
bunnies.add("floppy");
bunnies.add("hoppy");
System.out.println(bunnies); // [long ear, floppy, hoppy]
bunnies.removeIf(s -> s.chartAt(0) != 'h');
System.out.println(bunnies); // [happy]
```
