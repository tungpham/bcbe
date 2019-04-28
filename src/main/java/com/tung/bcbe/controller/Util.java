package com.tung.bcbe.controller;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.UUID;
import java.util.function.Supplier;

public class Util {

    static Supplier<ResourceNotFoundException> notFound(UUID msg) {
        return () -> new ResourceNotFoundException(msg + " not found");
    }
}
