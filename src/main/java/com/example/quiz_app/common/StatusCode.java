package com.example.quiz_app.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusCode {

    S0000("Success"),
    E4000("Bad Request"),
    E4001("Unauthorized"),
    E4003("Forbidden"),
    E4004("Resource not found"),
    E4005("Not Allowed to execute"),
    E4009("Conflict"),
    E5000("Internal API Error"),
    E5003("Service Unavailable"),
    E5004("Request timeout"),
    E6000("External Service Error"),
    E7000("Api Request Validation Error"),
    E7001("Parameter Error"),
    E9000("Unexpected Server Error"),
    E4006("Not Acceptable");

    private final String message;

}