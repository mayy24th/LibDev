package com.example.LibDev.global.util;

import org.springframework.validation.BindingResult;

import java.util.HashMap;

public class BindingValidError {
    public static HashMap<String, String> bindingValidError(BindingResult bindingResult){
        HashMap<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return errors;
    }
}
