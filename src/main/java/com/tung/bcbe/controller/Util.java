package com.tung.bcbe.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class Util {

    static Supplier<ResourceNotFoundException> notFound(UUID msg) {
        log.error(msg + " not found");
        return () -> new ResourceNotFoundException(msg + " not found");
    }
}
