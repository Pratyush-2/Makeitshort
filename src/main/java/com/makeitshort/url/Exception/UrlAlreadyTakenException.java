package com.makeitshort.url.Exception;

public class UrlAlreadyTakenException extends RuntimeException {
    public UrlAlreadyTakenException(String message){
        super(message);
    }
}
