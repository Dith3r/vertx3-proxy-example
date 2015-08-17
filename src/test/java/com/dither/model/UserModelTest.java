package com.dither.model;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test some simple logic in UserModel class
 */
public class UserModelTest {

    /**
     * Test has group method when user model is created without data
     *
     * @throws Exception
     */
    @Test
    public void testHasGroupNameWithEmptyData() throws Exception {
        UserModel userModel = new UserModel(null, null);
        assertFalse("Empty data return no group", userModel.hasGroupName());
    }

    /**
     * Test has group method when user model is created with group data
     *
     * @throws Exception
     */
    @Test
    public void testHasGroupName() throws Exception {
        String groupName = "test";
        UserModel userModel = new UserModel(null, groupName);
        assertTrue("Should return true, because group was provided", userModel.hasGroupName());
        assertEquals("Should return true, because group was provided", groupName, userModel.getGroupName());
    }

    /**
     * Test has is found when user model is created without user id data
     *
     * @throws Exception
     */
    @Test
    public void testIsFoundWithEmptyData() throws Exception {
        UserModel userModel = new UserModel(null, null);
        assertFalse("Empty json object should return not found state", userModel.isFound());
    }

    /**
     * Test is found when user model is created with user id data
     *
     * @throws Exception
     */
    @Test
    public void testIsFoundWithData() throws Exception {
        UserModel userModel = new UserModel("myUser", null);
        assertTrue("Should return true, because userId was provided", userModel.isFound());
    }

    /**
     * Test create from json with user id field
     *
     * @throws Exception
     */
    @Test
    public void testCreateFromJsonWithId() throws Exception {
        JsonObject userJson = new JsonObject();
        userJson.put("_id", "myUser");

        UserModel userModel = UserModel.createFromJson(userJson);
        assertTrue("UserId should be in result", userModel.isFound());
        assertFalse("Group should not be in result", userModel.hasGroupName());
    }

    /**
     * Test create from json with group name field
     *
     * @throws Exception
     */
    @Test
    public void testCreateFromJsonWithGroupName() throws Exception {
        JsonObject userJson = new JsonObject();
        userJson.put("groupName", "group");

        UserModel userModel = UserModel.createFromJson(userJson);
        assertFalse("UserId should not be in result", userModel.isFound());
        assertTrue("GroupName should be in result", userModel.hasGroupName());
    }

    /**
     * Test create from json without group name
     *
     * @throws Exception
     */
    @Test
    public void testCreateFromJsonGroupNameWithEmptyJsonObject() throws Exception {
        JsonObject userJson = new JsonObject();

        UserModel userModel = UserModel.createFromJson(userJson);
        assertFalse("Empty json object should return no group", userModel.hasGroupName());
    }

    /**
     * Test create from json without user id
     *
     * @throws Exception
     */
    @Test
    public void testCreateFromJsonIsFoundWithEmptyJsonObject() throws Exception {
        JsonObject userJson = new JsonObject();

        UserModel userModel = UserModel.createFromJson(userJson);
        assertFalse("Empty json object should return not found state", userModel.isFound());
    }
}