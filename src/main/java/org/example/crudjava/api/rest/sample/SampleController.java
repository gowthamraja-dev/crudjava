package org.example.crudjava.api.rest.sample;

import org.example.crudjava.core.dto.SampleDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SampleController {

    @GetMapping("/sample")
    public String getSample() {
        return "Hello World";
    }

    @PostMapping("/sample") 
    public String postSample(@RequestBody @Valid SampleDto sampleDto) throws Exception {
        return "Hello " + sampleDto.getName() + "!";
    }
}
