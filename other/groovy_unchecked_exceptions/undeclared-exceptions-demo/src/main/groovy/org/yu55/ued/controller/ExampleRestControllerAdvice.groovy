package org.yu55.ued.controller

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
