package com.example.mapper;

public interface Mapper<T, E> {

    public E transform (T t);

}
