package cz.strazovan.cvut.fel.theses.prometheustest.rest;

import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("test")
public class CustomEndpoint {

    @GetMapping
    @Timed(value = "greeting.time", description = "Time taken to return greeting")
    public String testGet() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 5 * 1000));
        return "hello there.";
    }
}
