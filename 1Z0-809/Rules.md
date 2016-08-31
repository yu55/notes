* Enums
  * are always `public static final`
  * comparing can be done via `==`
  * constructor must be always `private`
* Interfaces
  * you cannot qualify default methods as `synchronized` or `final`
  * The Diamond Problem
    * if two super interfaces define (default) method with the same signature, implementing class must override this method (and invoke one of super methods: `Interface1.super.method()`)
    * if base class and base interface define method with the same signature then "the class wins"
