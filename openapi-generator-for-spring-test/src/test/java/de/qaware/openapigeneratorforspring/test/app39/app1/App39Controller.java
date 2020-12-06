package de.qaware.openapigeneratorforspring.test.app39.app1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class App39Controller {

    @GetMapping("mapping1")
    public Instant mapping1() {
        return null;
    }

    @GetMapping("mapping2")
    public Instant mapping2() {
        return null;
    }
}
