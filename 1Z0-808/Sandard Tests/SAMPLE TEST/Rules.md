* Abstract Class Definition Rules:
  * cannot be instantiated directly
  * may be defined with any number, including zero, of abstract and non-abstract methods
  * may not be marked with `private` or `final`
  * an abstract class that extends an abstract class inherits all of its abstract methods as its own abstract methods
  * the first concrete class that extends an abstract class must provide an implementation for all of inherited abstract methods
* Abstract Method Definition Rules:
  * may be only defined in abstract classes
  * may not be declared `private` or `final`
  * must not provide a method body/implementation in the abstract class for which is it declared
  * implementing an abstract method in a subclass follows the same rules for overriding a method
