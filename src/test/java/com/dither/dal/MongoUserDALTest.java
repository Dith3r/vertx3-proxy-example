package com.dither.dal;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;


/**
 * Test for Mongo user dal
 */
public class MongoUserDALTest {
    private MongoClient mongoClient;

    @Before
    public void setUp() {
        mongoClient = mock(MongoClient.class);
    }

    /**
     * Testing Mongo user dal find one user method
     *
     * @throws Exception
     */
    @Test
    public void testFindUser() throws Exception {
        String userId = "userId";
        JsonObject query = new JsonObject().put("_id", userId);
        JsonObject fields = new JsonObject().put("groupName", true).put("_id", true);

        Handler<AsyncResult<JsonObject>> handler = event -> { /* this will never be called - only for check param pass*/};


        MongoUserDAL mongoUserDAL = new MongoUserDAL(mongoClient);
        mongoUserDAL.findUser(userId, handler);

        verify(mongoClient).findOne("users", query, fields, handler);
    }

    /**
     * Testing Mongo user dal update one user method
     *
     * @throws Exception
     */
    @Test
    public void testUpdateUser() throws Exception {
        String userId = "userId";
        String groupName = "testGroupName";
        JsonObject query = new JsonObject().put("_id", userId);
        JsonObject update = new JsonObject().put("$set", new JsonObject().put("groupName", groupName));

        Handler<AsyncResult<Void>> handler = event -> { /* this will never be called - only for check param pass*/};

        MongoUserDAL mongoUserDAL = new MongoUserDAL(mongoClient);
        mongoUserDAL.updateUser(userId, groupName, handler);
        verify(mongoClient).update("users", query, update, handler);
    }
}