package com.itheima.reggie_take_out.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * global error handler
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class}) // which controller to intercept
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * the method that will handle all errors
     * @param ex
     * @return R
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "existed";
            return R.error(msg);
        }
        return R.error("failed with unknown error");
    }

    /**
     * the method that will handle all errors
     * @param ex
     * @return R
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "existed";
            return R.error(msg);
        }
        return R.error(ex.getMessage());
    }
}
