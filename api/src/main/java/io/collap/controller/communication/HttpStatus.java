package io.collap.controller.communication;

public enum HttpStatus {

    // TODO: Complete the list!

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
