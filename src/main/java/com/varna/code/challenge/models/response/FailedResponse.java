package com.varna.code.challenge.models.response;

public class FailedResponse extends AbstractResponse
{
    public FailedResponse(Object body)
    {
        super(ResponseStatus.FAILED, body);
    }
}
