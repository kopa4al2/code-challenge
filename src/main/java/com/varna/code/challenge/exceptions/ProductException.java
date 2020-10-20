package com.varna.code.challenge.exceptions;

/**
 * Generic exception caught in controller to return appropriate messages
 * Thrown from the service when there is a problem with CRUD operation on product
 * or the user inserted illegal data
 */
public class ProductException extends Exception
{
    public ProductException(String message)
    {
        super(message);
    }
}
