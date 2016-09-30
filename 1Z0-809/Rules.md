* Enums
  * can be definied in top-level class only or within interface; never locally in method 
  * comparing can be done via `==`
  * Enum constructor is always private. You cannot make it public or protected. If an enum type has no constructor declarations, then a private constructor that takes no parameters is automatically provided. 
  * An enum is implicitly final, which means you cannot extend it. 
  * You cannot extend an enum from another enum or class because an enum implicitly extends java.lang.Enum. But an enum can implement interfaces. 
  * Since enum maintains exactly one instance of its constants, you cannot clone it. You cannot even override the clone method in an enum because java.lang.Enum makes it final. 
  * Compiler provides an enum with two public static methods automatically -  values() and valueOf(String). The values method returns an array of its constants and valueOf method tries to match the String argument exactly (i.e. case sensitive) with an enum constant and returns that constant if successful otherwise it throws java.lang.IllegalArgumentException.
  * It implements java.lang.Comparable (thus, an enum can be added to sorted collections such as SortedSet, TreeSet, and TreeMap).
  * It has a method ordinal(), which returns the index (starting with 0) of that constant i.e. the position of that constant in its enum declaration.
  * It has a method name(), which returns the name of this enum constant, exactly as declared in its enum declaration.
* Interfaces
  * you cannot qualify default methods as `synchronized` or `final`
  * The Diamond Problem
    * if two super interfaces define (default) method with the same signature, implementing class must override this method (and invoke one of super methods: `Interface1.super.method()`)
    * if two super interfaces have the same method name and one has default implementation, the compiler will issue and error
    * if base class and base interface define method with the same signature then "the class wins"
  * functional interface (SAM - Single Abstract Method)
    * interface must contain exactly one abstrat method with signature different than methods in `Object` class
    * interface may contain other methods: default, static or abstract from `Object` class
    * above rules works also for derived interfaces
```java
@FunctionalInterface
public interface Comparator<T> {
  int compare(T o1, T o2);
  boolean equals(Object obj);
  // other methods are default methods or static methods and are elided
}
```
* Method reference
  * use when lambda is doing nothing else but calling other method
  * types:
    * reference to a static method; `ContainingClass::staticMethodName`
    * reference to an instance method of particular object; `containingObject::instanceMethodName` like `new MySorter()::compare`
    * reference to an instance method of arbitrary object of a particular type; `ContainingType::methodName` like `String[] array = ...; Arrays.sort(array, String::compareToIgnoreCase)` is equivalent to `(String a, String b) -> a.compareToIgnoreCase(b)` (http://moandjiezana.com/blog/2014/understanding-method-references/)
    * reference to a constructor; `ClassName::new`
```java
interface SAM {
    default int guns(List<String> targets) {
        System.out.println("Guns");
        return targets.size();
    }

    int missiles(List<String> targets);
}

class Stinger implements SAM {
    public int missiles(List<String> targets) {
        System.out.println("Missiles " + targets);
        return targets.size();
    }
}

public abstract class LambdasAndRefsTests {

    public static int size(List<String> names) {
        System.out.println("Size");
        return names.size() * 2;
    }

    public static void process(List<String> names, SAM c) {
        c.missiles(names);
    }

    public static void main(String[] args) {
        List<String> targets = Arrays.asList("a", "b", "c");
        Stinger s = new Stinger();

        process(targets, s);                         // "Missiles [a, b, c]"
        process(targets, trgts -> s.missiles(trgts));// "Missiles [a, b, c]"
        process(targets, s::missiles);               // "Missiles [a, b, c]"
        process(targets, new Stinger()::missiles);   // "Missiles [a, b, c]"
        process(targets, s::guns);                   // "Guns"
        process(targets, LambdasAndRefsTests::size); // "Size"
        //process(targets, SAM::guns);     // guns can be invoked on object and not on interface; arguments are OK tough

        // Stinger::missiles is a valid method reference that can mean to refer either to a static method missiles of Stinger class or
        // to an instance method of any arbitrary instance of Stinger class. Which meaning is implied depends on the
        // context in which it is used. Here, the context does not supply any instance of Stinger class.
        // Therefore, Stinger::missiles will refer to a static method missiles. But there is no such static method in Stinger class.
        // Therefore, it is invalid in this context. To use Stinger::missiles, you need a reference to a Stinger instance, which
        // is not available here.
        //process(missiles, Stinger::missiles);

        process(targets, List::size);
        process(targets, l -> l.size());
    }
}
```
* Generics
  * subtyping doesn't work for generic types - use wildcards instead; generics are not covariant
  * wildcards
    * `List<? super Number>` - a list containing instances of Number of its super classes. It allows to `list.add(new Integer(1));`. It doesn't allow to retrive anything other than Object: `Object o = list.get(0);` (because the compiler doesn't know the exact class of objects contained by list)
    * `List<?>` is the same as `List<? extends Object>` - a list containing instances of some class that extends Object. It won't let add anything. It will only let retrive Object: `Object o = list.get(i);`
    * `List<? extends Number>` - a list containing instances of Number class or its subclasses. It won't allow to add any object to list because the compiler doesn't know the exact class of objects contained by the list so it cannot check whether whatever you are adding is eligible to be added to the list or not. It allows to retrive `Number` objects.
```java
List<Number> listOfInts = new ArrayList<Integer>(); // incompatible types - won't compile
List<Object> listOfObjs = new ArrayList<Integer>(); // incompatible types - won't compile

List<?> wildcardList = new ArrayList<Integer>();    // compiles OK

// when using wildcard parameters it's not possible to modify the object
wildcardList.add(new Integer(10)); // cannot find symbol method add

// what types are compatible with below method
ArrayList<String> in;
List result; // not even List<Object>!!!
public static <E extends CharSequence> List<? super E> doIt(List<E> nums)

// other limitations of generics
T field = new T()       // compiler error

T[] field = new T[100]; // compiler error

class X<T> {
  T instanceField;      // OK
  static T staticField  // compiler error

}

class SomeException<T> extends Throwable {} // compiler error

List<int> // generics doesn't work with primitive types
```
* Collections
  * `next()` must be called before each `remove{}` in `Iterator` (`IllegalStateException` otherwise)
  * `java.util.Arrays.asList()` returns fixed size list: unable to add or remove, but able to modify existing elements
  * `java.util.Comparable` - use for natural ordering
  * `java.util.Comparator` - use when comparing objects differently than natural, or no natural ordering is present
* Functional interfaces (https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html)
  * `Predicate<T>`: `boolean test(T t)`, `default Predicate<T> and(Predicate<? super T> other)`, `default Predicate<T> or(Predicate<? super T> other)`, `default Predicate<T> negate()`
    * used by: `default boolean Collection.removeIf(Predicate<? super E> filter)`, `Stream<T> filter(Predicate<? super T> predicate)`
    * primitive: `[Int|Long|Double]Predicate` method: `boolean test([int|long|double])`
    * binary versions: `BiPredicate<T,U>`, method: `boolean test(T t, U u)`; example: `BiPredicate<List<String>,String> bpr = List::contains; List<String> l=...; bpr.test(l,"123");`
  * `Consumer<T>`: `void accept(T t)`, `default Consumer<T> andThen(Consumer<? super T> after)`
    * used by: `void Stream.forEach(Consumer<? super T> action)`
    * primitive: `[Int|Long|Double]Consumer` method `void accept(int|long|double value)`, `Obj[Int|Long|Double]Consumer<T>` method `void accept(T t, [int|long|double] value)`
    * binary versions: `BiConsumer<T,U>`, method: `void accept(T t, U u)`
  * `Function<T,U>`: `R apply(T t)`, `default <V> Function<T,V> andThen(Function<? super R,? extends V> after)`, `default <V> Function<V,R> compose(Function<? super V,? extends T> before)`, `static <T> Function<T,T> identity()`
    * used by: `<R> Stream<R> 	map(Function<? super T,? extends R> mapper)`
    * primitive versions:
      * `[Int|Long|Double]Function<R>`, method: `R apply([int|long|double] value)`
      * `To[Int|Long|Double]Function<T>`, method: `int applyAs[Int|Long|Double](T value)`
      * `[Int|Long|Double]To[Int|Long|Double]`, method: `[int|long|double] applyAs[Int|Long|Double]([Int|Long|Double] value)`
    * binary versions: `BiFunction<T, U, R> R apply(T t, U u)`
  * `UnaryOperator<T> extends Function<T,T>`
    * used by: `default void List<E>.replaceAll(UnaryOperator<E> operator)`
    * primitive: `[Int|Long|Double]UnaryOperator`
  * `Supplier<T>`: `T get()`
    * used by: `static <T> Stream<T> generate(Supplier<T> s)`
    * primitive: `[Boolean|Int|Long|Double]Supplier [boolean|int|long|double] getAs[Boolean|Int|Long|Double]()`
* Stream API
  * https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
  * `List.forEach(Consumer<? super T> action)` (defined in `Iterable`) and `List.stream().forEach(Consumer<? super T> action)`
* Exceptions and assertions
  * In a multi-catch block, you cannot combine catch handlers for two exceptions that share a base- and derived-class relationship.
  * The resource class must implement `java.lang.AutoCloseable` interface. Many standard JDK classes such as `BufferedReader`, `BufferedWriter`) implement `java.io.Closeable` interface, which extends `java.lang.AutoCloseable`. 
  * Resources are closed at the end of the try block and before any catch or finally block.
  * Resources are not even accessible in the catch or finally block.
  * Resources are closed in the reverse order of their creation.
  * Resources are closed even if the code in the try block throws an exception.
  * `java.lang.AutoCloseable`'s `close() throws Exception` but `java.io.Closeable`'s `close() throws IOException`.
  * If code in try block throws exception and an exception also thrown while closing is resource, the exception thrown while closing the resource is suppressed. The caller gets the exception thrown in the try block
  * static initialization blocks CAN NOT throw checked exceptions
  * non-static initialization blocks CAN throw checked exceptions, but these exceptions have to be declared in `throws` in all constructors
  * if a method is declared in more than two interfaces with different `throws` the implementing version of this method must include all exceptions in `throws`
  * `Throwable.initCause(Throwable cause)` - may be called at most once (zero when constructor with cause was used)
  * `ClassNotFoundException` and `NoSuchFieldException` are both CHECKED exceptions
  * controlling asserts:
    * `-ea` / `-enableasserts` Enables assertions by default (except system classes).
    * `-ea:<class name>` Enables assertions for the given class name.
    * `-ea:<package name>...` Enables assertions in all the members of the given package <package name>.
    * `-ea:...` Enable assertions in the given unnamed package.
    * `-esa` Short for `-enablesystemsassertions;` enables assertions in system classes. This option is rarely used.
    * analogous for disabling asserts: `-da`/`-dsa`/`-disablesystemassertions`
  * Assertions the old way: method `assert` and `javac -source 1.3` (can't use `assert` keyword tough)
* Java date/time API
  * `java.time` classes are immutable -> thread-safe
* I/O fundamentals
  * `java.io.Console console = System.console();`
  * `java.io.Console` methods:
    * `char[] readPassword()`/`char[] readPassword(String fmt, Object... args)` (formatted prompt + password read without echoing)
    * `java.io.Reader reader()`
    * `java.io.PrintWriter writer()`
    * `String readLine()`/`String readLine(String fmt, Object... args)` (formatted prompt + read line)
    * `Console format(String fmt, Object... args)`
    * `Console printf(String format, Object... args)`
  * Character streams
    * `java.io.Reader`
      * `BufferedReader` - adds buffer to underlying character stream: `new BufferedReader(new FileReader(file))`
        * `LineNumberReader` - buffered stream that keeps track of line numbers
      * `FilterReader`
        * `PushbackReader`
      * `PipedReader` - "piped" reading
      * `InputStreamReader` - bridge from byte to character streams (bytes into characters in specified charset)
        * `FileReader` - reading character files
      * `StringReader` - source is `String`
    * `java.io.Writer`
      * `BufferedWriter`
      * `PrintWriter` - supports formatted printing characters to the output character stream
      * `PipedWriter`
      * `OutputStreamWriter`
        * `FileWriter`
      * `FilterWriter`
      * `StringWriter` - collects characters into string buffer -> `String`
    * `java.util.Scanner` - text scanner which can parse primitive types and strings using regular expressions
      * `s = new Scanner(new FileReader(fileName)); s.useDelimiter("\\W");`
