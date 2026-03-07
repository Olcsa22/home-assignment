package com.example.validation;

import com.example.exception.ValidationException;

public interface Validator<T> {

    void validate(T t) throws ValidationException;

}
