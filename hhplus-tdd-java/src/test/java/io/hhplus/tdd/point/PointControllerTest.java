package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootApplication
@AutoConfigureMockMvc
public class PointControllerTest {

    MockMvc mockMvc;

    @Test
    public void PathVariableTest() {
        String url = "/point/aaa";
    }
}
