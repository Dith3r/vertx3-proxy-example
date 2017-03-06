package com.dither.model;

import io.vertx.core.json.JsonObject;

/**
 * Class for interaction with json result
 */
public class UserModel {

  protected static final short STATE_FOUND = 1;
  protected static final short STATE_NOT_FOUND = -1;
  private static final String KEY_USER_ID = "_id";
  private static final String KEY_GROUP_NAME = "groupName";
  private static final String NONE_VALUE = null;

  private final String userId;
  private final String groupName;
  private final int state;

  public UserModel(String userId, String groupName) {
    this.userId = userId;
    this.groupName = groupName;

    if (userId == null) {
      this.state = STATE_NOT_FOUND;
    } else {
      this.state = STATE_FOUND;
    }
  }

  public boolean hasGroupName() {
    return this.getGroupName() != null;
  }

  public boolean isFound() {
    return this.state == STATE_FOUND;
  }

  public String getGroupName() {
    return this.groupName;
  }


  public static UserModel fromJson(JsonObject object) {
    if (object != null) {
      return new UserModel(object.getString(KEY_USER_ID, NONE_VALUE),
          object.getString(KEY_GROUP_NAME, NONE_VALUE));
    } else {
      return new UserModel(null, null);
    }
  }
}
