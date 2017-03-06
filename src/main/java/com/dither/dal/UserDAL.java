package com.dither.dal;


import com.dither.model.UserModel;
import io.vertx.core.Future;

/**
 * User data access layer is interface for accessing users data
 */
public interface UserDAL {

  /**
   * Find user in database
   *
   * @param userId String user id to find
   * @return UserDAL
   */
  Future<UserModel> findUser(String userId);

  /**
   * Update user group name by user id
   *
   * @param userId String
   * @param groupName String
   * @return UserDAL
   */
  Future<Void> updateGroupNameById(String userId, String groupName);
}
