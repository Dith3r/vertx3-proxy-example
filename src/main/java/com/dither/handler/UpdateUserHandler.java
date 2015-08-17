package com.dither.handler;

import com.dither.dal.UserDAL;
import com.dither.model.UserGroupModel;
import com.google.inject.Inject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * Trigger user group assignment and updates database for persisted assignment
 */
public class UpdateUserHandler implements ProxyHandler {
    private final UserGroupModel userGroupModel;
    private final UserDAL userDAL;

    @Inject
    public UpdateUserHandler(UserDAL userDAL, UserGroupModel userGroupModel) {
        this.userDAL = userDAL;
        this.userGroupModel = userGroupModel;
    }

    @Override
    public void handle(RoutingContext context) {
        Session session = context.session();
        String group = session.get("group");

        if (group == null) {
            // dont send any response to user till UserDal request failed or successed
            String userId = context.request().getParam("id");
            String groupName = userGroupModel.peek();
            session.put("group", groupName);

            userDAL.updateUser(userId, groupName, reply -> {
                if (reply.succeeded()) {
                    context.next();
                } else {
                    context.fail(ST_INTERNAL_ERROR);
                }
            });
        } else {
            context.next();
        }
    }
}
