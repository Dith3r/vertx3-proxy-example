package com.dither.model;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * Domain model for choosing group based on probability
 */
public class UserGroupModel {
    /**
     * estimated size of array list done by enrollment
     */
    private final static short DEFAULT_SIZE = 100;
    /**
     * priority multiplicator for better entropy
     */
    private final static short ENROLLMENT_SIZE = 10;
    /**
     * Holds index to group assignment. For O(1) assignment of group
     */
    private final ArrayList<String> valueToGroup;
    /**
     * wicked random number generator
     */
    private final Random generator;
    /**
     * size of enrolled groups
     */
    private final int size;

    @Inject
    public UserGroupModel(@Named("config.groups") JsonObject groups, Random generator) {
        this.generator = generator;
        this.valueToGroup = new ArrayList<>(DEFAULT_SIZE);
        this.size = enrollGroups(groups);
    }

    /**
     * Prepare ArrayList for optimized random access to groups based on probability
     *
     * @param groups JsonObject
     * @return int size of enrollment
     */
    private int enrollGroups(JsonObject groups) {
        int index = 0;

        for (Map.Entry<String, Object> entry : groups) {
            int priority = (int) entry.getValue() * ENROLLMENT_SIZE + index;
            for (/* empty */; index < priority; index++) {
                valueToGroup.add(entry.getKey());
            }
        }
        return index;
    }

    /**
     * Choose one random group from enrolled groups
     */
    public String peek() {
        return this.valueToGroup.get(generator.nextInt(size));
    }
}
