package com.dither.dal;

import com.dither.model.UserModel;
import com.google.inject.Inject;
import io.vertx.core.Future;
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
   * @param userId String user id to find
   * @return UserDAL
   */

  @Override
  public Future<UserModel> findUser(String userId) {
    Future<UserModel> result = Future.future();
    JsonObject query = new JsonObject().put("_id", userId);
    JsonObject fields = new JsonObject().put("groupName", true).put("_id", true);

    client.findOne(COLLECTION, query, fields, asyncResult -> {
      if (asyncResult.succeeded()) {
        result.complete(UserModel.fromJson(asyncResult.result()));
      } else {
        result.fail(asyncResult.cause());
      }
    });
    return result;
  }

  /**
   * Update user group name by user id
   *
   * @param userId String
   * @param groupName String
   * @return UserDAL
   */

  @Override
  public Future<Void> updateGroupNameById(String userId, String groupName) {
    Future<Void> result = Future.future();
    JsonObject query = new JsonObject().put("_id", userId);
    JsonObject update = new JsonObject().put("$set", new JsonObject().put("groupName", groupName));
    client.updateCollection(COLLECTION, query, update, asyncResult -> {
      if (asyncResult.succeeded()) {
        result.complete();
      } else {
        result.fail(asyncResult.cause());
      }
    });
    return result;
  }
}
