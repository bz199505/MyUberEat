package com.itheima.reggie_take_out.common;

/**
 * self-defined business exception
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
