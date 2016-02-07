# When Groovy (un)checked exceptions can bite

Groovy language introduced many interesting features from Java programmer point of view.
One of them is lack of differentiation between checked and unchecked exceptions - a nice quality for programmers who struggled with checked exceptions requirements. Declaring checked exceptions in `throws` clause in method definition and handling them in calling code or declaring in `throws` clause again isn't in everyone taste.
It turns out however that using this neat Groovy feature with existing frameworks can sometimes be insidious which I experienced lately.

I was working with relatively straightforward microservice written in Groovy.
It was built on top of Spring Boot, among others it used `@RestController` to define REST endpoints and `@ControllerAdvice` to handle exceptions thrown by controller or its internals.
Very simplified example code looks something like this:

```groovy
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExampleRestController {

    @RequestMapping(value = '/get')
    String get() {
        /*
         Lets imagine this exception is thrown somewhere from deepest layers of our service code
         and we don't have to be immediately aware of this.
          */
        throw new ExampleRestControllerException()
    }
}
```
```groovy
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class ExampleRestControllerAdvice {

    @ExceptionHandler(value = ExampleRestControllerException.class)
    @ResponseBody
    String onExampleRestControllerException() {
        return 'ExampleRestControllerException handled in ControllerAdvice'
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    String onException() {
        return 'Exception handled in ControllerAdvice'
    }
}
```
```groovy
class ExampleRestControllerException extends Exception {
}
```

When `ExampleRestControllerException` was thrown it was handled by `onExampleRestControllerException` method in `ExampleRestControllerAdvice`. And this worked perfectly fine.
But I had to add simple aspect after returning of `get` method in `ExampleRestController` to do some additional stuff.
```groovy
@Aspect
@Component
class RestControllerAspect {

    @AfterReturning('execution(* org.yu55.ued.controller.ExampleRestController.get())')
    public void logServiceAccess(JoinPoint joinPoint) {
    }
```

Suddenly `ExampleRestControllerAdvice` started behaving strangely. It started calling `onException` method instead of `onExampleRestControllerException`. What happend?
When aspect for `ExampleRestController.get()` is defined Spring generates a Java proxy for `ExampleRestController`. Since `ExampleRestController` doesn't implement any interface Spring uses CGLIB library instead of JDK Proxy for proxy dynamic generation.
Generated proxy class name is something like `ExampleRestController$$EnhancerBySpringCGLIB$$8d751c8@4481` and it's `ExampleRestController` subclass which intercepts all methods calls.
There is also another generated class created: `ExampleRestController$$FastClassBySpringCGLIB$$b01891ea` which is a `ExampleRestController` class wrapper which promises faster method invocations. Spring web is invoking (via `sun.reflect` reflection classes) the `get` method on enhancer class which then invokes `get` method (via cglib `MethodProxy`) on fast class instance. When controllers `get` method throws `ExampleRestControllerException` it's rethrown by fast class to enhancer class and then enhancer class `get` method throws `java.lang.reflect.UndeclaredThrowableException` (which contains `ExampleRestControllerException` inside) and then `ExampleRestControllerAdvice` matches this exception with `Exception` class and fires handler method different than expected. Why `UndeclaredThrowableException` is thrown? Becasue indeed `get` method written in Groovy didn't declared any checked excpetions that it could eventually throw. Java Proxy don't know anything about that it's written in Groovy. According to documentation this is the default behaviour of Java Proxy.
This issue can be fixed by adding checked exceptions to `get` method definition or defining `ExampleRestControllerException` as runtime exception.

Not blaming groovy, code in Java also possible?
Remember that still working with java libraries which may require "proper" exceptions handling.
Defend: careful tests
