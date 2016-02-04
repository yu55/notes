# When Groovy (un)checked exceptions can bite

Groovy language introduced many interesting features from Java programmer point of view.
One of them is lack of differentiation between checked and unchecked exceptions - a nice quality for programmers who struggled with the requirements introduced by checked exceptions. Declaring checked exceptions in `throws` clause in method definition and handling them in calling code or declaring in `throws` clause again isn't in everyone taste.
It turns out however that using this neat Groovy feature with existing frameworks can sometimes be insidious which I experienced lately.

I was working with very straightforward microservice written in Groovy.
It was built on top of Spring Boot, among others it used `@RestController` and `@ControllerAdvice` to handle exceptions thrown by controller or its internals.
Very simplified code looks something like this:

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

When `ExampleRestControllerException` was thrown it was handled by `onExampleRestControllerException` method in `ExampleRestControllerAdvice`. This worked perfectly fine for a long time.
I had to add simple aspect after returning `get` method in `ExampleRestController` to do some additional stuff.
```groovy
@Aspect
@Component
class RestControllerAspect {

    @AfterReturning('execution(* org.yu55.ued.controller.ExampleRestController.get())')
    public void logServiceAccess(JoinPoint joinPoint) {
    }
```

Suddenly `ExampleRestControllerAdvice` started behaving strangely. It started calling `onException` method instead of `onExampleRestControllerException`. What happend?

TBD

Not blaming groovy, code in Java also possible?
Remember that still working with java libraries which may require "proper" exceptions handling.
Defend: careful tests
