package org.yu55.ued.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class RestControllerAspect {

    @AfterReturning('execution(* org.yu55.ued.controller.ExampleRestController.get())')
    public void logServiceAccess(JoinPoint joinPoint) {
        // empty implementation for clarity reasons
    }

}
