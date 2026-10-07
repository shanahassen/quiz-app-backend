package com.example.quiz_app.common;

import java.util.Map;

public class QuizException extends Exception {

    private StatusCode statusCode;
    private String responseDescription;
    private Map<String, String> translationMap;

    public QuizException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.responseDescription = statusCode.getMessage();
    }

    public QuizException(StatusCode statusCode, String responseDescription) {
        super(responseDescription);
        this.statusCode = statusCode;
        this.responseDescription = responseDescription;
    }

    public QuizException(StatusCode statusCode, String responseDescription, Throwable cause) {
        super(responseDescription, cause);
        this.statusCode = statusCode;
        this.responseDescription = responseDescription;

    }
    public QuizException(StatusCode statusCode, String responseDescription, Map<String, String> translationMap) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
        this.responseDescription = responseDescription;
        this.translationMap = translationMap;
    }

    public QuizException(Throwable e) {
        this(StatusCode.E5000, e.getMessage(), e);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public Map<String, String> getTranslationMap(){
        return translationMap;
    }
}
