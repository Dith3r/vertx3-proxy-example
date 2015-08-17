package com.dither.dal;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * User data access layer is interface for accessing users data
 */
public interface UserDAL {

    /**
     * Find user in database
     *
     * @param userId        String user id to find
     * @param resultHandler Handler async result handler
     * @return UserDAL
     */
    UserDAL findUser(String userId, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Update user group name by user id
     *
     * @param userId        String
     * @param groupName     String
     * @param resultHandler Handler async result handler
     * @return UserDAL
     */
    UserDAL updateUser(String userId, String groupName, Handler<AsyncResult<Void>> resultHandler);
}
