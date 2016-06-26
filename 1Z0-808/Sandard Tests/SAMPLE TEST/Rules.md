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
```java
int s = 5;
s += s + mx(s) + ++s;

// s += (expression) will be converted to s =��s + expression
// s = s + s + method(s) + ++s;
// s = 5 + 5 + method(5) + 6;
// s = 5 + 5 + 8 + 6;
// s = 24;

int s = 5;
s += s + ++s;
// s = 5 + 5 + 6
// s = 16

int s = 5;
s += ++s + s;
// s = 5 + 6 + 6
// s = 17

int x = 2;
x += x++ * x++ * x++;

// 2 + (2 * 3 * 4)
```
* Numeric Promotion Rules
  * If two values have different data types, Java will automatically promote one of the values to the larger of the two data types.
  * If one of the values is integral and other is floating-point, Java will automatically promote the integral value to the floating-point value's data type.
  * Smaller data types, namely `byte`, `short`, and `char`, are first promoted to `int` any time they're used with a Java binary arithmetic operator, even if neither of the operands is `int`.
    * Compound assignment operators (`+=`, `-=` etc.): if `E1 op= E2` then `E1 = (T)((E1) op (E2))`, where `T` is type of `E1`, except that E1 is evaluated only once.
  * After all promotion has occurred and the operands have the same data type, the resulting value will have the same data type as its promoted operands.
```java
int * long = long
double + float = double
short/short = int
short * float / double = double
```
* Assignment conversion if the expression is a constant expression of type `byte`, `short`, `char`, or `int` (JSL 5.2)
  * A narrowing primitive conversion may be used if the type of the variable is `byte`, `short`, or `char`, and the value of the constant expression is representable in the type of the variable. `byte theAnswer = 42;`
  * A narrowing primitive conversion followed by a boxing conversion may be used if the type of the variable is:
    * `Byte` and the value of the constant expression is representable in the type `byte`.
    * `Short` and the value of the constant expression is representable in the type `short`.
    * `Character` and the value of the constant expression is representable in the type `char`.
* Method invocation conversions allowed:
  * an identity conversion (JLS 5.1.1)
  * a widening primitive conversion (JLS 5.1.2)
  * a widening reference conversion (JLS 5.1.5)
  * a boxing conversion (JLS 5.1.7) optionally followed by widening reference conversion
  * an unboxing conversion (JLS 5.1.8) optionally followed by a widening primitive conversion.
* Method invocation with `null` arguments
  * when candidate methods have arguments types from the same hierarchy the most specific is chosen
  * compilation error occurs when candidate methods arguments aren't from same hierarchy
* Equality operator `==` produces compile error in situation other than:
  * comparing numeric primitive types (if different types promotion works)
  * comparing `boolean` values (if one of the operands is of type `Boolean`, it is subjected to unboxing conversion)
  * comparing objects including `null` and `String`
    * objects have to have a IS-A relationship among themeselves
    * narrowing promotion may occure (`Integer == int` -> `Integer.intValue == int`)
* `+` is overloaded such that if any one of its two operands is a `String` then it will convert the other operand to a `String` and create a new string by concatenating the two. Therefore, in `63+"a"` and `"a"+63`, 63 is converted to "63" and `'b' +"a"` and `"a"+'b'`, `'b'` is converted to `"b"`. Note that in 'b'+ 63 , 'b' is promoted to an int i.e. 98 giving 161.
* Breaking from `if` statement is possible only when using labels (when not inside a loop):
```java
    label: if(true) {
        System.out.println("break label");
        break label; //this is valid
    }
```
* Six facts on Strings (JLS 3.10.5):
  1. Literal strings within the same class in the same package represent references to the same String object.
  2. Literal strings within different classes in the same package represent references to the same String object.
  3. Literal strings within different classes in different packages likewise represent references to the same String object.
  4. Strings computed by constant expressions are computed at compile time and then treated as if they were literals.
  5. Strings computed at run time are newly created and therefore are distinct.
  6. The result of explicitly interning a computed string is the same string as any pre-existing literal string with the same contents.
* Rules for a `switch` statement:
  1. Only `String`, `byte`, `char`, `short`, `int`, (and their wrapper classes `Byte`, `Character`, `Short`, and `Integer`), and enums can be used as types of a switch variable. (`String` is allowed only since Java 7). 
  2. The case constants must be assignable to the switch variable. For example, if your switch variable is of class `String`, your case labels must use Strings as well.
  3. The switch variable must be big enough to hold all the case constants. For example, if the switch variable is of type `char`, then none of the case constants can be greater than `65535` because a char's range is from `0` to `65535`.
  4. All case labels should be COMPILE TIME CONSTANTS.
  5. No two of the case constant expressions associated with a switch statement may have the same value.
  6. At most one default label may be associated with the same switch statement.
  7. It's legal to use constant in switch: `switch (1) { default : break; }`
```java
    public void switchTest(byte x){
        switch(x){
            case 'b':   // OK (98 - in byte's range)
            default :   // OK
            case -2:    // OK
            case 128:   // Won't compile (byte range is from -128 to 127)
        }
    }
```
* `for` loop
  * third part (i.e. the update part) of the `for` loop does not allow every kind of statement. It allows only the following statements here:  Assignment, PreIncrementExpression, PreDecrementExpression, PostIncrementExpression, PostDecrementExpression, MethodInvocation, and ClassInstanceCreationExpression.
* `try-catch-finally` and `return`:
```java
    try {
        throw new Exception();
    } catch(Exception e) {
        return 0; // 0 is NOT returned
    } finally {
        return 1; // 1 is returned; this is directly before exiting method and it supersedes 0
    }
```
* A class or interface type T will be initialized immediately before the first occurrence of any one of the following:
  * T is a class and an instance of T is created.
  * T is a class and a static method declared by T is invoked.
  * A static field declared by T is assigned.
  * A static field declared by T is used and the field is not a constant variable (§4.12.4).
  * T is a top level class (§7.6), and an assert statement (§14.10) lexically nested within T (§8.1.3) is executed.
  * A reference to a static field (§8.3.1.1) causes initialization of only the class or interface that actually declares it, even though it might be referred to through the name of a subclass, a subinterface, or a class that implements an interface.
  * Invocation of certain reflective methods in class Class and in package java.lang.reflect also causes class or interface initialization.
  * A class or interface will not be initialized under any other circumstance.
```java
class Super { static String ID = "QBANK"; }

class Sub extends Super{
   static { System.out.print("In Sub"); } // This won't execute
}
public class Test{
   public static void main(String[] args){
      System.out.println(Sub.ID); // Only "QBANK" will be printed
   }
}
```
* Overriding a method:
  * The method in the child class must have the same signature (mthd name + parameters) as the method in the parent class.
  * The method in the child class must be at least as acessible or more accessible than the method in the parent class.
  * The method in the child class may not throw a checked exception that is new or broader than the class of any exception thrown in the parent class method (child method may NOT throw any exception at all; child may declare throwing any runtime exception).
  * If the method returns a value, it must be the same or a subclass of the method in the parent class, known as covariant return types. For primitives return types must be exactly the same.
  * Overriding method may be `abstract` (class must be `abstract` too)
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
  * Invoking default super methods:
```java
    interface A {
        default void hello() {
        }
    }

    interface B extends A {
        default void hello() {
            super.hello();    //This is NOT valid.
            A.super.hello();    //This is valid.
        }
    }

    public class TestClass implements B {
        public void hello() {
            super.hello();    //This is NOT valid.
            A.super.hello();  //This is NOT valid because TestClass does not implement A directly.
            B.super.hello();  //This is valid.
        }
    }
```
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
