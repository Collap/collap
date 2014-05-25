package io.collap.controller.communication;

public enum HttpStatus {

    ok (200),

    notFound (404),

    internalServerError (500);


    private final int statusCode;

    HttpStatus (int statusCode) {
        this.statusCode = statusCode;
    }

    public int getValue () {
        return statusCode;
    }

}
