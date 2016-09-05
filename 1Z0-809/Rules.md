* Enums
  * can be definied in top-level class only or within interface; never locally in method 
  * are always `public static final`
  * comparing can be done via `==`
  * constructor must be always `private`
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
