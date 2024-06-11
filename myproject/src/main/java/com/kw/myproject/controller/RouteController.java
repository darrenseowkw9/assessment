package com.kw.myproject.controller;

import com.kw.myproject.model.ResponseModel;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Hidden
@RestController
public class RouteController implements ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<ResponseModel> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        int statusCode = status == null? HttpStatus.BAD_REQUEST.value() : Integer.valueOf(status.toString());
        message = message == null || message.isEmpty()? "Something went wrong.": message;
        return new ResponseEntity<>(new ResponseModel(statusCode, message), HttpStatus.valueOf(statusCode));
    }
}