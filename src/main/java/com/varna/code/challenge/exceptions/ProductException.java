package com.varna.code.challenge.exceptions;

/**
 * Thrown from the services when there is a problem with CRUD operation on product
 * or the user inserted illegal data
 * this exception is meant to be used from the controller to return appropriate messages
 * this is why it unifies a lot of problems
 */
public class ProductException extends Exception
{
    public ProductException(String message)
    {
        super(message);
    }
}
