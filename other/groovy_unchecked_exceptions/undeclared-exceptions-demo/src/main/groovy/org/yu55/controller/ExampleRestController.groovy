package org.yu55.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
public class ExampleRestController {

    @RequestMapping(value = "/get")
    public String get() {
        throw new ExampleRestControllerException()
    }
}
