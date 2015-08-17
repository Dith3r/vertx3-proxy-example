package com.dither.dal;

import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoUserDAL implements UserDAL {
    /**
     * Collection to use
     */
    private static final String COLLECTION = "users";
    /**
     * Mongo connection pool client
     */
    private final MongoClient client;

    @Inject
    public MongoUserDAL(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    /**
     * Find user in database
     *
     * @param userId        String user id to find
     * @param resultHandler Handler async result handler
     * @return UserDAL
     */
    public UserDAL findUser(String userId, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject query = new JsonObject().put("_id", userId);
        JsonObject fields = new JsonObject().put("groupName", true).put("_id", true);
        client.findOne(COLLECTION, query, fields, resultHandler);
        return this;
    }

    /**
     * Update user group name by user id
     *
     * @param userId        String
     * @param groupName     String
     * @param resultHandler Handler async result handler
     * @return UserDAL
     */
    public UserDAL updateUser(String userId, String groupName, Handler<AsyncResult<Void>> resultHandler) {
        JsonObject query = new JsonObject().put("_id", userId);
        JsonObject update = new JsonObject().put("$set", new JsonObject().put("groupName", groupName));
        client.update(COLLECTION, query, update, resultHandler);
        return this;
    }
}
