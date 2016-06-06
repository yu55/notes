* Overriding a method (nonprivate):
  * The method in the child class must have the same signature as the method in the parent class.
  * The method in the child class must be at least as acessible or more accessible than the method in the parent class.
  * The method in the child class may not throw a checked exception that is new or broader than the class of any exception thrown in the parent class method.
  * If the method returns a value, it must be the same or a subclass of the method in the parent class, known as covariant return types.
* Hiding Static Methods
  * The method in the child class must have the same signature as the method in the parent class.
  * The method in the child class must be at least as accessible or more accessible than the method in the parent class.
  * The method in the child class may not throw a checked exception that is new or broader than the class of any exception thrown in the parent class method.
  * If the method returns a value, it must be the same or a subclass of the method in the parent class, known as covariant return types.
  * The method defined in the child class must be marked as `static` if it is marked as `static` in the parent class (method hiding). Likewise, the method must not be marked as `static` in the child class if it is not marked as `static` in the parent class (method overriding).
* Overriding vs. Hiding Methods
  * Unlike overriding a method, in which a child method replaces the parent method in calls defined in both the parent and child, hidden methods only replace parent methods in the calls defined in the child class
```java
public class Marsupial {
  public static boolean isBiped() {
    return false;
  }
  public void getMarsupialDescription() {
    System.out.println("Marsupial walks on two legs: "+isBiped());
  }
}

public class Kangaroo extends Marsupial {
  public static boolean isBiped() {
    return true;
  }
  public void getKangarooDescription() {
    System.out.println("Kangaroo hops on two legs: "+isBiped());
  }
  public static void main(String[] args) {
    Kangaroo joey = new Kangaroo();
    joey.getMarsupialDescription();
    joey.getKangarooDescription();
  }
}

// Marsupial walks on two legs: false
// Kangaroo hops on two legs: true
```
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
* Defining an Interface
  * Interfaces cannot be instantiated directly.
  * An interface in not required to have any methods.
  * An interface may not be marked as `final`.
  * All top-level interfaces are assumed to have `public` or default access, and they must include the `abstract` modifier in their definition. Therefore, marking an interface as `private`, `protected`, or `final` will trigger a compiler error, since this is incompatible with these assumptions.
  * All nondefault methods in an interface are assumed to have the modifiers `abstract` and `public` in their definition. Therefore, marking a method as `private`, `protected`, or `final` will trigger compiler errors as these are incompatible with the `abstract` and `public` keywords.
* Interface Variables
  * Interface variables are assumed to be `public`, `static`, and `final`. Therefore, marking a variable as `private` or `protected` will trigger a compiler error, as will marking any variable as `abstract`.
  * The value of an interface variable must be set when it is declared since it is marked as `final`.
* Default Interface Methods
  * A default method may only be declared within an interface and not within a class or abstract class.
  * A default method must be marked with the `default` keyword. If a method is marked as `default`, it must provide a method body.
  * A default method is not assumed to be `static`, `final`, or `abstract`, as it may be used or overridden by a class that implements the interface.
  * Like all methods in an interface, a default method is assumed to be `public` and will not compile if marked as `private` or `protected`.
* Static Interface Methods
  * Static method defined in an interface is not inherited in any classes that implement the interface.
  * Like all methods in an interface, a static method is assumed to be `public` and will not compile if marked as `private` or `protected`.
  * To reference the static method, a reference to the name of the interface must be used.
```java
public interface Hop {
  static int getJumpHeight() {
    return 8;
  }
}

public class Bunny implements Hop {
  public void printDetails() {
    System.out.println(Hop.getJumpHeight()); // won't compile without using 'Hop.'
  }
}
```
