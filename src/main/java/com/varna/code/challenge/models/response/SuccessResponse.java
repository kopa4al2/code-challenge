package com.varna.code.challenge.models.response;

public class SuccessResponse extends AbstractResponse
{
    public SuccessResponse(Object body)
    {
        super(ResponseStatus.SUCCESS, body);
    }
}
