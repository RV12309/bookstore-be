package com.hust.bookstore.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/")
@Slf4j
public class TestController {

    @GetMapping("/{name}")
    public ResponseEntity<Map<String, String>> sayHello(@PathVariable String name){
        log.info("Saying hello to {}.", name);
        return ResponseEntity.ok(Map.of("msg", String.format("Welcome %s!", name)));
    }
    @GetMapping("/sellers/{name}")
    public ResponseEntity<Map<String, String>> sayHelloSeller(@PathVariable String name){
        log.info("Saying hello to seller {}.", name);
        return ResponseEntity.ok(Map.of("msg", String.format("Welcome seller %s!", name)));
    }

    @GetMapping("/admin/{name}")
    public ResponseEntity<Map<String, String>> sayHelloAdmin(@PathVariable String name){
        log.info("Saying hello to admin {}.", name);
        return ResponseEntity.ok(Map.of("msg", String.format("Welcome admin %s!", name)));
    }

    @GetMapping("/users/{name}")
    public ResponseEntity<Map<String, String>> sayHelloUser(@PathVariable String name){
        log.info("Saying hello to user {}.", name);
        return ResponseEntity.ok(Map.of("msg", String.format("Welcome user %s!", name)));
    }

}