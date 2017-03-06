package com.dither.handler;

import com.dither.dal.UserDAL;
import com.dither.model.UserGroupModel;
import com.dither.model.UserModel;
import com.google.inject.Inject;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import java.net.HttpURLConnection;
import javax.jws.soap.SOAPBinding.Use;

/**
 * Handler role is to check if group is assigned for user in database
 */
public class GroupUserHandler implements Handler<RoutingContext> {

  private final UserDAL userDAL;
  private UserGroupModel userGroupModel;

  @Inject
  public GroupUserHandler(UserDAL userDAL, UserGroupModel userGroupModel) {
    this.userDAL = userDAL;
    this.userGroupModel = userGroupModel;
  }

  @Override
  public void handle(RoutingContext context) {
    context.request().pause();
    Session session = context.session();

    if (session.get("group") != null) {
      context.next();
    } else {
      String userId = context.request().getParam("id");
      if (userId == null) {
        context.fail(HttpURLConnection.HTTP_BAD_REQUEST);
      }
      userDAL.findUser(userId)
          .compose(user -> {
            if (!user.isPresent()) {
              return Future.failedFuture("user not found");
            }

            String groupName = user.filter(UserModel::hasGroupName)
                .map(UserModel::getGroupName)
                .orElseGet(userGroupModel::peek);

            session.put("group", groupName);

            if (!user.get().hasGroupName()) {
              return userDAL.updateGroupNameById(userId, groupName);
            } else {
              return Future.succeededFuture();
            }
          })
          .setHandler(asyncResult -> {
            if (asyncResult.succeeded()) {
              context.next();
            } else {
              context.fail(asyncResult.cause());
            }
          });
    }
  }
}
