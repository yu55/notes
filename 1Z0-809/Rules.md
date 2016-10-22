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
  * When `Interface1.method() throws IOException` and `Interface2.method() throws FileNotFoundException` the method implementing both must throw ex that satisfies both: `FileNotFoundException`
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
    * `List<? super Number>` - a list containing instances of Number or its super classes. It allows to `list.add(new Integer(1));`. It doesn't allow to retrive anything other than Object: `Object o = list.get(0);` (because the compiler doesn't know the exact class of objects contained by list)
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
  * `java.util.Comparable` - use for natural ordering; `int compareTo(T o)`
  * `java.util.Comparator` - use when comparing objects differently than natural, or no natural ordering is present; `int compare(T o1, T o2)`, `Comparator comparing(...)`, e.g. `Comparator<Person> byLastName = Comparator.comparing(Person::getLastName);`
  * `HashSet`, `HashMap`, `ArrayList`, `CopyOnWriteArrayList` permits `null` storage but `ConcurrentHashMap` prevents `null` keys or values
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
      * `To[Int|Long|Double]Function<T>`, method: `[int|long|double] applyAs[Int|Long|Double](T value)`
      * `[Int|Long|Double]To[Int|Long|Double]`, method: `[int|long|double] applyAs[Int|Long|Double]([Int|Long|Double] value)`
    * binary versions: `BiFunction<T, U, R> R apply(T t, U u)`
  * `UnaryOperator<T> extends Function<T,T>`
    * used by: `default void List<E>.replaceAll(UnaryOperator<E> operator)`
    * primitive: `[Int|Long|Double]UnaryOperator`
  * `BinaryOperator<T> extends BiFunction<T,T,T>`
  * `Supplier<T>`: `T get()`
    * used by: `static <T> Stream<T> generate(Supplier<T> s)`
    * primitive: `[Boolean|Int|Long|Double]Supplier [boolean|int|long|double] getAs[Boolean|Int|Long|Double]()`
* Stream API
  * https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
  * `Stream`
    * `sorted()`, `sorted(Comparator<? super T> comparator)`
    * `Optional<T> max(Comparator<? super T> comparator)`
  * `IntStream`
    * `int sum()` for empty stream returns 0
    * `OptionalDouble average()`
      * when `OptionalDouble.empty` is returned then `.getAsDouble()` will throw `java.util.NoSuchElementException`; to avoid use `.orElse(0.0)`
    * `OptionalInt findAny()`
    * `OptionalInt findFirst()`
    * `IntStream.iterate`
      * `IntStream.iterate(1, i -> 2)` 1 2 2 2 2...
      * `IntStream.iterate(2, i -> i)` 2 2 2 2 2...
      * `IntStream.iterate(3, i -> ++i)` 3 4 5 6 7 ...
    * map
      * `IntStream map(IntUnaryOperator mapper)`
      * `DoubleStream mapToDouble(IntToDoubleFunction mapper)`
      * `LongStream mapToLong(IntToLongFunction mapper)`
      * `<U> Stream<U> mapToObj(IntFunction<? extends U> mapper)`
```java
  IntStream.iterate(1, x->x++).limit(1).mapToObj(i -> String.valueOf(i)).count();          // OK
  Stream.iterate(1, x -> x++).limit(1).map(integer -> String.valueOf(integer)).count();    // OK
  IntStream.iterate(1, x -> x++).limit(1).map(integer -> String.valueOf(integer)).count(); // won't compile: incompatible types: bad return type in lambda expression java.lang.String cannot be converted to int
```
  * `List.forEach(Consumer<? super T> action)` (defined in `Iterable`) and `List.stream().forEach(Consumer<? super T> action)`
  * reduce
    * Not constrained to execute sequentially.
    * The `identity` value must be an identity for the `combiner` function. This means that for all `u`, `combiner(identity, u)` is equal to `u`.
    * The `combiner` function must be compatible with the `accumulator` function; for all `u` and `t`, the following must hold: `combiner.apply(u, accumulator.apply(identity, t)) == accumulator.apply(u, t)`
```java
  Optional<T> reduce(BinaryOperator<T> accumulator)

  // boolean foundAny = false;
  // T result = null;
  // for (T element : this stream) {
  //  if (!foundAny) {
  //    foundAny = true;
  //    result = element;
  //  }
  //  else
  //    result = accumulator.apply(result, element);
  // }
  // return foundAny ? Optional.of(result) : Optional.empty();

  //------------------------------------------------------------

  T reduce(T identity, BinaryOperator<T> accumulator)
  <U> U reduce(U identity, BiFunction<U,? super T,U> accumulator, BinaryOperator<U> combiner)

  // T result = identity;
  // for (T element : this stream)
  //   result = accumulator.apply(result, element)
  // return result;
```
  * collect
```java
  <R> R collect(Supplier<R> supplier, BiConsumer<R,? super T> accumulator, BiConsumer<R,R> combiner)

  // R result = supplier.get();
  // for (T element : this stream)
  //   accumulator.accept(result, element);
  // return result;

  // The following will accumulate strings into an ArrayList:
  List<String> asList = stringStream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

  // The following will take a stream of strings and concatenates them into a single string:
  String concat = stringStream.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
 
```
```java
  <R,A> R collect(Collector<? super T,A,R> collector)

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  String result = ohMy.collect(Collectors.joining(", "));
  System.out.println(result); // lions, tigers, bears

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Double result = ohMy.collect(Collectors.averagingInt(String::length));
  System.out.println(result); // 5.333333333333333

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  TreeSet<String> result = ohMy.filter(s -> s.startsWith("t")
    .collect(Collectors.toCollection(TreeSet::new));
  System.out.println(result); // [tigers]

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<String, Integer> map = ohMy.collect(
  Collectors.toMap(s -> s, String::length));
  System.out.println(map); // {lions=5, bears=5, tigers=6}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<Integer, List<String>> map = ohMy.collect(
  Collectors.groupingBy(String::length));
  System.out.println(map); // {5=[lions, bears], 6=[tigers]}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<Integer, Set<String>> map = ohMy.collect(
  Collectors.groupingBy(String::length, Collectors.toSet()));
  System.out.println(map); // {5=[lions, bears], 6=[tigers]}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  TreeMap<Integer, Set<String>> map = ohMy.collect(
  Collectors.groupingBy(String::length, TreeMap::new, Collectors.toSet()));
  System.out.println(map); // {5=[lions, bears], 6=[tigers]}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  TreeMap<Integer, List<String>> map = ohMy.collect(
  Collectors.groupingBy(String::length, TreeMap::new, Collectors.toList()));
  System.out.println(map);

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<Boolean, List<String>> map = ohMy.collect(
  Collectors.partitioningBy(s -> s.length() <= 5));
  System.out.println(map); // {false=[tigers], true=[lions, bears]}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<Boolean, Set<String>> map = ohMy.collect(
  Collectors.partitioningBy(s -> s.length() <= 7, Collectors.toSet()));
  System.out.println(map);// {false=[], true=[lions, tigers, bears]}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<Integer, Long> map = ohMy.collect(Collectors.groupingBy(
  String::length, Collectors.counting()));
  System.out.println(map); // {5=2, 6=1}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<Integer, Optional<Character>> map = ohMy.collect(
    Collectors.groupingBy(
      String::length,
      Collectors.mapping(s -> s.charAt(0),
        Collectors.minBy(Comparator.naturalOrder()))));
  System.out.println(map); // {5=Optional[b], 6=Optional[t]}

  Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
  Map<Integer, Optional<Character>> map = ohMy.collect(
    groupingBy(
      String::length,
      mapping(s -> s.charAt(0),
        minBy(Comparator.naturalOrder()))));
  System.out.println(map); // {5=Optional[b], 6=Optional[t]}
```
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
```java
  Period p = Period.between(LocalDate.of(2015, Month.SEPTEMBER, 2), LocalDate.of(2015, Month.SEPTEMBER, 1));
  System.out.println(p); // P-1D
  Duration d = Duration.between(LocalDateTime.of(2015, Month.SEPTEMBER, 2, 1, 0), LocalDateTime.of(2015, Month.SEPTEMBER, 2, 10, 10));
  System.out.println(d); // PT9H10M
```
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
    * `java.io.Reader extends Object`
      * `BufferedReader` - adds buffer to underlying character stream: `new BufferedReader(new FileReader(file))`
        * `LineNumberReader` - buffered stream that keeps track of line numbers
      * `FilterReader`
        * `PushbackReader`
      * `PipedReader` - "piped" reading
      * `InputStreamReader` - bridge from byte to character streams (bytes into characters in specified charset)
        * `FileReader` - reading character files; doesn't have read line method (`BufferedReader` has it)
      * `StringReader` - source is `String`
    * `java.io.Writer extends Object`
      * `BufferedWriter`
      * `PrintWriter` - supports formatted printing characters to the output character stream (`print`, `println`, `write`); methods never throw I/O exceptions (besides some constructors)
      * `PipedWriter`
      * `OutputStreamWriter`
        * `FileWriter`
      * `FilterWriter` - creates/overrides file unless constructor with `boolean append`
      * `StringWriter` - collects characters into string buffer -> `String`
    * `java.util.Scanner` - text scanner which can parse primitive types and strings using regular expressions
      * `s = new Scanner(new FileReader(fileName)); s.useDelimiter("\\W");`
  * Byte streams
    * `java.io.OutputStream extends Object`
      * `ByteArrayOutputStream`
      * `FileOutputStream`
      * `FilterOutputStream`
        * `PrintStream`
        * `DataOutputStream extends DataOutputStream`
        * `BufferedOutputStream`
      * `ObjectOutputStream`
      * `PipedOutputStream`
    * `DataOutput` (interface)
      * `ObjectOutput` (interface)
        * `ObjectOutputStream`
    * `java.io.InputStream extends Object`
      * `ByteArrayInputStream`
      * `FileInputStream`
      * `FilterInputStream`
        * `LineNumberInputStream`
        * `DataInputStream` used to read java primitive types
        * `PushbackInputStream`
        * `BufferedInputStream`
      * `ObjectInputStream`
      * `PipedInputStream` with output stream creates a communication channel
      * `SequenceInputStream`
      * `StringBufferInputStream`
    * `DataInput`
      * `ObjectInput`
        * `ObjectInputStream`
  * Serialization/deserialization
    * If an object is being deserialized, that means the class of that object implements Serializable. Therefore, its constructor will never be called. However, constructor for the super class may be invoked if the super class does not implement serializable interface (The no-argument constructor of only the first non-serializable super class is invoked. This constructor may internally invoke any constructor of its super class).
    * Multiple copies of an object may be added to a stream.
* NIO2
  * `Path.subpath(int beginIndex, int endIndex)`
    * Indexing starts from 0
    * Root (i.e. `c:\`) is not considered as the beginning.
    * name at `beginIndex` is included but name at `endIndex` is not.
    * paths do not start or end with `\`.
    * if something is wrong with indexes `IllegalArgumentException` is thrown
  * `Files`
    * `static Stream<Path> find(Path start, int maxDepth, BiPredicate<Path,BasicFileAttributes> matcher, FileVisitOption... options)` - Return a Stream that is lazily populated with Path by searching for files in a file tree rooted at a given starting file.
    * `static Stream<Path> list(Path dir)` - Return a lazily populated Stream, the elements of which are the entries in the directory.
    * `static Stream<Path> walk(Path start, [int maxDepth,] FileVisitOption... options)` - Return a Stream that is lazily populated with Path by walking the file tree rooted at a given starting file.
    * `delete(Path path) throws IOException`
      * If the file is a symbolic link then the symbolic link itself, not the final target of the link, is deleted.
      * If the file is a directory then the directory must be empty.
      * `NoSuchFileException` - if the file does not exist (optional specific exception)
      * `DirectoryNotEmptyException` - if the file is a directory and could not otherwise be deleted because the directory is not empty (optional specific exception)
      * `IOException` - if an I/O error occurs
      * `SecurityException` - In the case of the default provider, and a security manager is installed, the `SecurityManager.checkDelete(String)` method is invoked to check delete access to the file
    * `Stream<String> lines(Path path[, Charset cs]) throws IOException`
    * `List<String> readAllLines(Path path, Charset cs) throws IOException`
  * `Path`
    * `Path resolve(Path other)`
      * If the other parameter is an absolute path then this method trivially returns other.
      * If other is an empty path then this method trivially returns this path.
      * Otherwise this method considers this path to be a directory and resolves the given path against this path.
        * In the simplest case, the given path does not have a root component, in which case this method joins the given path to this path and returns a resulting path that ends with the given path.
        * Where the given path has a root component then resolution is highly implementation dependent and therefore unspecified.
        * e.g. "foo/bar".resolve("gus") -> "foo/bar/gus"
        * e.g. `Paths.get("c:\\temp\\test.txt").resolve(Paths.get("report.pdf"))` returns "c:\temp\test.txt\report.pdf" (If the argument is a relative path (i.e. if it doesn't start with a root), the argument is simply appended to the path to produce the result.)
    * `Path relativize(Path other)`
      * "c:\\personal\\.\\photos\\..\\readme.txt".relativize("c:\\personal\\index.html") -> "..\..\..\..\index.html"
      * "a/c" relativize "a/b"  is "../b"
      * Reverse operation to `resolution`: `p.relativize(p.resolve(q)).equals(q)`
      * Constructs a relative path between this path and a given path. If this path is "/a/b" and the given path is "/a/b/c/d" then the resulting relative path would be "c/d".
      * Where this path and the given path do not have a root component, then a relative path can be constructed. A relative path cannot be constructed if only one of the paths have a root component.
        * `IllegalArgumentException` - if other is not a `Path` that can be relativized against this path: `Paths.get("photos\\goa").relativize(Paths.get("\\index.html"));`
      * Where both paths have a root component then it is implementation dependent if a relative path can be constructed.
      * If this path and the given path are equal then an empty path is returned.
    * `Path resolveSibling(Path other)` - Resolves the given path against this path's parent path. This is useful where a file name needs to be replaced with another file name. For example, suppose that the name separator is "/" and a path represents "dir1/dir2/foo", then invoking this method with the Path "bar" will result in the Path "dir1/dir2/bar". If this path does not have a parent path, or other is absolute, then this method returns other. If other is an empty path then this method returns this path's parent, or where this path doesn't have a parent, the empty path.
* JDBC
  * `ResultSet` types
    * `TYPE_FORWARD_ONLY` default; go through data in order
    * `TYPE_SCROLL_INSENSITIVE` you can go through the data in any order, but you won’t see changes made in the database while you are scrolling
    * `TYPE_SCROLL_SENSITIVE` you can go through the data in any order, and you will see changes made in the database
    * `CONCUR_READ_ONLY` default; means that you can read the `ResultSet` but not write to it
    * `CONCUR_UPDATABLE` means that you can both read and write to it
* Concurrency
  * A memory consistency error occurs when two threads have inconsistent views of what should be the same data.
  * The intrinsic lock is released when the method ends. Irrespective of how it ends.
  * `List.remove(int)` may throw `IndexOutOfBoundException` but `ConcurrentModificationException` by the methods of the `Iterator` interfaces.
  * Synchronizing static methods (two equivalent methods):
```java
  public static void printDaysWork() {
    synchronized(SheepManager.class) {
      System.out.print("Finished work");
    }
  }

  public static synchronized void printDaysWork() {
    System.out.print("Finished work");
  }
```
  * Atomic classes common methods
    * `get()` retrieve the current value
    * `set()` set the given value, equivalent to the assignment `=` operator
    * `getAndSet()` atomically sets the new value and returns the old value
    * `incrementAndGet()` for numeric classes, atomic pre-increment operation equivalent to `++value`
    * `getAndIncrement()` for numeric classes, atomic post-increment operation equivalent to `value++`
    * `decrementAndGet()` for numeric classes, atomic pre-decrement operation equivalent to `--value`
    * `getAndDecrement()` for numeric classes, atomic post-decrement operation equivalent to `value--`
  * Concurrent collections
    * `ConcurrentLinkedDeque implements Deque`
    * `ConcurrentLinkedQueue implements Queue`
    * `ConcurrentSkipListMap implements ConcurrentMap, SortedMap, NavigableMap`
    * `ConcurrentSkipListSet implements SortedSet, NavigableSet`
    * `CopyOnWriteArrayList implements List`; `Iterator` will never throw `ConcurrentModificationException`, trying do modify list via iterator will throw `UnsupportedOperationException`
    * `CopyOnWriteArraySet implements Set`
    * `LinkedBlockingDeque implements BlockingQueue, BlockingDeque`
    * `LinkedBlockingQueue implements BlockingQueue`
    * `ConcurrentHashMap implements ConcurrentMap`
      * provides atomic versions of `putIfAbsent`, `remove`, `replace`
```java
 if (!map.containsKey(key))
   return map.put(key, value);
 else
   return map.get(key);
```
  * Obtaining synchronized collections when given aren't synchronized: `Collections.`:
    * `synchronizedCollection(Collection<T> c)`
    * `synchronizedList(List<T> list)`
    * `synchronizedMap(Map<K,V> m)`
    * `synchronizedNavigableMap(NavigableMap<K,V> m)`
    * `synchronizedNavigableSet(NavigableSet<T> s)`
    * `synchronizedSet(Set<T> s)`
    * `synchronizedSortedMap(SortedMap<K,V> m)`
    * `synchronizedSortedSet(SortedSet<T> s)`
  * Allow multiple concurrent reads but exclusive write: task for `ReadWriteLock`
    * If one thread is reading, other threads can read, but no thread can write.
    * If one thread is writing, no other thread can read or write.
```java
public class MultipleReadersSingleWriter {

    private final ArrayList<String> theList = new ArrayList<String>();
    private final ReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public String read() {
        r.lock();
        try {
            System.out.println("reading");
            if (theList.isEmpty()) return null;
            else return theList.get(0);
        } finally {
            r.unlock();
        }
    }

    public void write(String data) {
        w.lock();
        try {
            System.out.println("Written " + data);
            theList.add(data);
        } finally {
            w.unlock();
        }
    }
}
```
* Executors
  * `Executor` (Interface); `void execute(Runnable command)`
    * `ExecutorService` (Interface); `<T> Future<T> submit(Callable<T> task)`, `Future<?> submit(Runnable task)`, program won't shut down unless `shutdown()` is called
      * `AbstractExecutorService` (abstract class)
        * `ForkJoinPool`
        * `ThreadPoolExecutor`
          * `ScheduledThreadPoolExecutor`; `ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)`
```java
  // Factorial implements Callable so that it can be passed to a ExecutorService
  // and get executed as a task.
  class Factorial implements Callable<Long> {
    long n;
    public Factorial(long n) {
      this.n = n;
    }
    public Long call() throws Exception {
      if(n <= 0) {
        throw new Exception("for finding factorial, N should be > 0");
      }
      long fact = 1;
      for(long longVal = 1; longVal <= n; longVal++) {
        fact *= longVal;
      }
      return fact;
    }
  }

  // Illustrates how Callable, Executors, ExecutorService, and Future are related;
  // also shows how they work together to execute a task
  class CallableTest {
    public static void main(String []args) throws Exception {
    // the value for which we want to find the factorial
    long N = 20;
    // get a callable task to be submitted to the executor service
    Callable<Long> task = new Factorial(N);
    // create an ExecutorService with a fixed thread pool having one thread
    ExecutorService es = Executors.newSingleThreadExecutor();
    // submit the task to the executor service and store the Future object
    Future<Long> future = es.submit(task);
    // wait for the get() method that blocks until the computation is complete.
    System.out.printf("factorial of %d is %d", N, future.get());
    // done. shutdown the executor service since we don't need it anymore
    es.shutdown();
  }
}
```
  * Callable
```java
  @FunctionalInterface public interface Callable<V> {
    V call() throws Exception;
  }
```
* Parallel stream
  * `Stream.parallel()` or `Collection.parallelStream()`; by default amount of threads in stream is related to amount of CPUs
  * parallel reduce will join individual reduction results in original order (when functions without depending on global state)
```java
Arrays.asList("a", "b").parallelStream().reduce("_", (a, b)->a.concat(b));
// will produce "_ab" or "_a_b", but NOT "_ba" or "_b_a"
```
* Fork/Join
  * General logic for fork/join:
    * First check whether the task is small enough to be performed directly without forking. If so, perform it without forking.
      * `RecursiveAction`: just process directly, don't return anything
      * `RecursiveTask`: process and return the result
    * If no, then split the task into multiple small tasks (at least 2) and:
      * `RecursiveAction`: submit the subtasks back to the pool using `invokeAll(list of tasks)`
      * `RecursiveTask`: split into 2 tasks, fork other: `other.fork()`, compute this, wait for forked (`otherResult=other.join()`), join results and return.
  * The worker threads in the ForkJoinPool extend java.lang.Thread and are created by a factory.
  * One worker thread may steal work from another worker thread.
  * `ForkJoinPool implements Executor` and not the threads in the pool.
