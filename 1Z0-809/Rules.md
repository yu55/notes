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
    * interface may contain other methods: default, static od abstract from `Object` class
    * above rules works also for derived interfaces
```java
@FunctionalInterface
public interface Comparator<T> {
  int compare(T o1, T o2);
  boolean equals(Object obj);
  // other methods are default methods or static methods and are elided
}
```
* Generics
  * subtyping doesn't work for generic types - use wildcards instead
```java
List<Number> listOfInts = new ArrayList<Integer>(); // incompatible types - won't compile

List<?> wildcardList = new ArrayList<Integer>();    // compiles OK

// when using wildcard parameters it's not possible to modify the object
wildcardList.add(new Integer(10)); // cannot find symbol method add

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
