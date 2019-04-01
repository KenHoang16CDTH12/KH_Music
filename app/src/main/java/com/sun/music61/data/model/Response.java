package com.sun.music61.data.model;

public final class Response {
    private int mStatusCode;
    private StringBuffer mResult;

    public Response(int statusCode, StringBuffer result) {
        mStatusCode = statusCode;
        mResult = result;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public StringBuffer getResult() {
        return mResult;
    }
}
