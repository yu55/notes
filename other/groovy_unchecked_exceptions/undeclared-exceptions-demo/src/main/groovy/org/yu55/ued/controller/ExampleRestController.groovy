package org.yu55.ued.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExampleRestController {

    @RequestMapping(value = '/get')
    String get() {
        /*
         Lets imagine this exception is thrown somewhere from deepest layers of our service code
         and we don't have to be immediately aware of that.
          */
        throw new ExampleRestControllerException()
    }
}
