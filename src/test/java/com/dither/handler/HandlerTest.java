package com.dither.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;

/**
 * Class with some general function for helping interaction with async results
 */
public class HandlerTest {
    /**
     * Prepare test async test result for test
     *
     * @param success boolean
     * @param result  result
     * @return
     */
    protected AsyncResult<JsonObject> getAsyncResult(boolean success, JsonObject result) {
        return new AsyncResult<JsonObject>() {
            @Override
            public JsonObject result() {
                return result;
            }

            @Override
            public Throwable cause() {
                return null;
            }

            @Override
            public boolean succeeded() {
                return success;
            }

            @Override
            public boolean failed() {
                return !success;
            }
        };
    }

    /**
     * Prepare test async test result for test
     *
     * @param success boolean
     * @return
     */
    protected AsyncResult<Void> getAsyncResult(boolean success) {
        return new AsyncResult<Void>() {
            @Override
            public Void result() {
                return null;
            }

            @Override
            public Throwable cause() {
                return null;
            }

            @Override
            public boolean succeeded() {
                return success;
            }

            @Override
            public boolean failed() {
                return !success;
            }
        };
    }
}
