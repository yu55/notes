package org.yu55.ued.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExampleRestController {

    @RequestMapping(value = '/get')
    String get() {
        throw new ExampleRestControllerException()
    }
}
