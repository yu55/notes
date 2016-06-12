* Identifiers
  * The name must begin with a letter or the symbol `$` or `_`.
  * Subsequent characters may also be numbers.
  * You cannot use the same name as a Java reserved word (Java is case sensitive so e.g. `Public` is OK).
* Order of operator precedence
  * post-unary operators; `expression++; expression--`
  * pre-unary operators; `++expression; --expression`
  * other unary operatos; `+,-,!`
  * multiplication/division/modulus; `*,/,%`
  * addition/substraction; `+,-`
  * shift operators; `<<,>>,>>>`
  * relational operators; `<,>,<=,>=,instanceof`
  * equal to/not equal to; `==,!=`
  * logical operators; `&,^,|`
  * short-circuit logical operators; `&&, ||`
  * ternary operators; `boolean expression ? expression1 : expression2`
  * assignment operators; `=, +=, -=, /=,%=,%=,&=,^=,!=,<<=,>>=,>>>=`
* Numeric Promotion Rules
  * If two values have different data tyupes, Java will automatically promote one of the values to the larger of the two data types.
  * If one of the values is integral and other is floating-point, Java will automatically promote the integral value to the floating-point value's data type.
  * Smaller data types, namely `byte`, `short`, and `char, are first promoted to `int` any time they're used with a Java binary arithmetic operator, even if neither of the operands is `int`.
  * After all promotion has occurred and the operands have the same data type, the resulting value will have the same data type as its promoted operands.
```java
int * long = long
double + float = double
short/short = int
short * float / double = double
```
* Data types allowed in `switch`
  * `int` and `Integer`
  * `byte` and `Byte`
  * `short` and `Short`
  * `char` and `Character`
  * `int` and `Integer`
  * `String`
  * `enum` values
  * compile-time constants: literals, `enum` constants or `final` constant variables (but NOT `final` methods arguments)
* Overriding a method:
  * The method in the child class must have the same signature as the method in the parent class.
  * The method in the child class must be at least as acessible or more accessible than the method in the parent class.
  * The method in the child class may not throw a checked exception that is new or broader than the class of any exception thrown in the parent class method (child method may NOT throw any exception at all).
  * If the method returns a value, it must be the same or a subclass of the method in the parent class, known as covariant return types.
  * Term overriding works only for nonprivate instance methods (private instance methods and static methods are referred as hidden in this situation)
* Hiding Methods
  * The method in the child class must have the same signature as the method in the parent class.
  * The method in the child class must be at least as accessible or more accessible than the method in the parent class.
  * The method in the child class may not throw a checked exception that is new or broader than the class of any exception thrown in the parent class method.
  * If the method returns a value, it must be the same or a subclass of the method in the parent class, known as covariant return types.
  * The method defined in the child class must be marked as `static` if it is marked as `static` in the parent class (method hiding). Likewise, the method must not be marked as `static` in the child class if it is not marked as `static` in the parent class (method overriding).
  * `private` instance methods are also referred as hidden rather than overriden
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
* Inheriting Variables - not overriden, just hidden
  * if referencing the variable from within the parent class, the variable from within the parent class is used
  * if referencing the variable from within a child class, the variable defined in the child class is used
  * likewise it is possible to reference the parent value with explicit use of the `super` keyword
```java
public class Rodent {
  protected int tailLength = 4;
  public void getRodentDetails() {
    System.out.println("[parentTail="+tailLength+"]");
  }
}

public class Mouse extends Rodent {
  protected int tailLength = 8;
  public void getMouseDetails() {
    System.out.println("[tail="+tailLength +",parentTail="+super.tailLength+"]");
  }

  public static void main(String[] args) {
    Mouse mouse = new Mouse();
    mouse.getRodentDetails();
    mouse.getMouseDetails();
  }
}

// [parentTail=4]
// [tail=8,parentTail=4]
```
```java
public class Animal {
  public int length = 2;
}

public class Jellyfish extends Animal {
  public int length = 5;
  public static void main(String[] args) {
    Jellyfish jellyfish = new Jellyfish();
    Animal animal = new Jellyfish();
    System.out.println(jellyfish.length);
    System.out.println(animal.length);
  }
}

// 5
// 2
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
