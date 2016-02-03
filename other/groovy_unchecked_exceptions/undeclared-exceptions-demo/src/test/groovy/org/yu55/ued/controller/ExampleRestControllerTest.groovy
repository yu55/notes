package org.yu55.ued.controller

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext
import org.yu55.ued.UndeclaredExceptionsDemoApplication

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UndeclaredExceptionsDemoApplication.class)
@WebAppConfiguration
class ExampleRestControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext

    MockMvc mockMvc

    @Before
    void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build()
    }

    @Test
    void getShouldBeHandledByExampleRestControllerExceptionHandler(){
        mockMvc.perform(get('/get'))
                .andExpect(status().isOk())
                .andExpect(content().string('ExampleRestControllerException handled in ControllerAdvice'))
    }

}
