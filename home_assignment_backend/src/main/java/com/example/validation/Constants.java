package com.example.validation;

public class Constants {
    public static class ValidationConstants{
        public static final String ID_IS_REQUIRED_MESSAGE = "Id property is required";
        public static final String NO_RESERVED_ID_MESSAGE = "Given Id can not point to an existing node";
        public static final String NOT_EXISTING_PARENT_MESSAGE = "Given parent Id can not point to not existing node";
        public static final String NO_EMPTY_NAME_MESSAGE = "Name property can not be empty";
        public static final String NO_EMPTY_CONTENT_MESSAGE = "Content property can not be empty";
        public static final String NOT_EXISTING_ID_MESSAGE = "Id must point to an existing entity";
        public static final String CIRCULAR_REFERENCE_MESSAGE = "Cannot move a node under its own descendant";
    }
}
