package com.dither.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Common interface for all vertx handlers
 */
public interface ProxyHandler extends Handler<RoutingContext> {
    /**
     * State not found
     */
    int ST_NOT_FOUND = 404;
    /**
     * State internal error
     */
    int ST_INTERNAL_ERROR = 500;
    /**
     * State bad request
     */
    int ST_BAD_REQUEST = 400;
}
