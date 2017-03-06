package com.dither.handler;

import com.dither.dal.UserDAL;
import com.dither.model.UserGroupModel;
import com.google.inject.Inject;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import java.net.HttpURLConnection;

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

    String group = session.get("group");

    if (group == null) {
      String userId = context.request().getParam("id");
      if (userId == null) {
        context.fail(HttpURLConnection.HTTP_BAD_REQUEST);
      }
      Future<Void> voidFuture = userDAL.findUser(userId)
          .compose(user -> {
            if (!user.isFound()) {
              return Future.failedFuture("user not found");
            }
            if (!user.hasGroupName()) {
              String groupName = userGroupModel.peek();
              session.put("group", groupName);
              return userDAL.updateGroupNameById(userId, groupName);
            } else {
              session.put("group", user.getGroupName());
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
    } else {
      context.next();
    }
  }
}
