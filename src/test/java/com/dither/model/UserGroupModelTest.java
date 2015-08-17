package com.dither.model;

import io.vertx.core.json.JsonObject;

import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for user group model
 */
public class UserGroupModelTest {
    /**
     * Check if class striped from randomness will produce same and predictable result for given values
     *
     * @throws Exception
     */
    @org.junit.Test
    public void testPeek() throws Exception {
        String firstGroupName = "A";
        String secondGroupName = "B";

        int firstGroupWeight = 3;
        int secondGroupWeight = 7;
        int enrollmentSize = 10;

        int size = (firstGroupWeight + secondGroupWeight) * enrollmentSize;

        JsonObject groupsConfiguration = new JsonObject().put(firstGroupName, firstGroupWeight)
                .put(secondGroupName, secondGroupWeight);

        Random random = mock(Random.class);
        when(random.nextInt(size)).thenReturn(firstGroupWeight * enrollmentSize)
                .thenReturn(firstGroupWeight * enrollmentSize - 1);

        UserGroupModel userGroupModel = new UserGroupModel(groupsConfiguration, random);
        assertEquals("Should take second group, because 30 is not in 0-29 range", secondGroupName, userGroupModel.peek());
        assertEquals("Should return first group because of random value in range 0-29", firstGroupName, userGroupModel.peek());
    }
}

