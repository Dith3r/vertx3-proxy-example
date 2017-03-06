package com.dither.model;

import io.vertx.core.json.JsonObject;
import java.util.Optional;

/**
 * Class for interaction with json result
 */
public class UserModel {

  private static final String KEY_USER_ID = "_id";
  private static final String KEY_GROUP_NAME = "groupName";
  private static final String NONE_VALUE = null;

  private final String userId;
  private final String groupName;

  public UserModel(String userId, String groupName) {
    this.userId = userId;
    this.groupName = groupName;
  }

  public boolean hasGroupName() {
    return this.getGroupName() != null;
  }

  public String getGroupName() {
    return this.groupName;
  }


  public static Optional<UserModel> fromJson(JsonObject object) {
    if (object != null) {
      return Optional.of(new UserModel(
          object.getString(KEY_USER_ID, NONE_VALUE),
          object.getString(KEY_GROUP_NAME, NONE_VALUE))
      );
    } else {
      return Optional.empty();
    }
  }
}
