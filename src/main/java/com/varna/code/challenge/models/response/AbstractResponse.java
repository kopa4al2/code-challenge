package com.varna.code.challenge.models.response;

/**
 * Represents abstract response with status and data
 * The body is usually an object model
 * but can be a single message (for error responses)
 */
public abstract class AbstractResponse
{
    private final ResponseStatus status;

    private final Object body;

    protected AbstractResponse(ResponseStatus status, Object body)
    {
        this.status = status;
        this.body = body;
    }

    public ResponseStatus getStatus()
    {
        return status;
    }

    public Object getBody()
    {
        return body;
    }
}
