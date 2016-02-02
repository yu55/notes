# When Groovy (un)checked exceptions can bite

Groovy language introduced many interesting features from Java programmer point of view.
One of them is lack of differentiation between checked and unchecked exceptions - a nice quality for programmers who struggled with the requirements of declaring checked exceptions in the throws clause in method definition and handling them in calling code (or declaring them in throws clause again).
It turns out that this neat feature can sometimes be insidious which I experienced lately.

I was working with very straightforward microservice written in groovy.
Build on top of Spring Boot, REST controller and ControllerAdvice to handle exceptions thrown by controller.
I wanted to add simple aspect around method in controller...

TBD

Not blaming groovy, code in Java also possible?
Remember that still working with java libraries which may require "proper" exceptions handling.
Defend: careful tests
