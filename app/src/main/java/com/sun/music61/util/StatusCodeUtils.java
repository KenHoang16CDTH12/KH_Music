package com.sun.music61.util;

/**
 * Key for StatusCode
 */
public interface StatusCodeUtils {
    int OK = 200;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDENT = 403;
    int NOT_FOUND = 404;
    int NOT_ACCESSIBLE = 406;
    int UNPROCESSABLE_ENTITY = 422;
    int TOO_MANY_REQUESTS = 429;
    int INTERNAL_SERVER_ERROR = 500;
    int SERVICE_UNAVAILABLE = 503;
    int GATEWAY_TIMEOUT = 504;
}
