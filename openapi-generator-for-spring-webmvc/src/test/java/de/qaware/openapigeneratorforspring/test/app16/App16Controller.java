package de.qaware.openapigeneratorforspring.test.app16;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Server(url = "http://url1", description = "Server 1")
@RequestMapping(value = "basePath/to", consumes = MediaType.APPLICATION_JSON_VALUE)
public class App16Controller {

    @GetMapping("/mapping1")
    @Operation(servers = @Server(url = "http://url1", description = "Server 1"))
    public void mapping1get() {

    }

    @PutMapping("/mapping1")
    @Server(url = "http://url1", description = "Server 1")
    public void mapping1put() {

    }

    @DeleteMapping("/mapping2")
    public void mapping2delete() {

    }

    @PostMapping("/mapping2")
    public void mapping2post() {

    }

    @GetMapping("/mapping3")
    @Server(url = "http://url2", description = "Server 2")
    public void mapping3() {

    }
}