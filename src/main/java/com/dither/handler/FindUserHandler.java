package com.dither.handler;

import com.dither.dal.UserDAL;
import com.dither.model.UserModel;
import com.google.inject.Inject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * Handler role is to check if group is assigned for user in database
 */
public class FindUserHandler implements ProxyHandler {
    private final UserDAL userDAL;

    @Inject
    public FindUserHandler(UserDAL userDAL) {
        this.userDAL = userDAL;
    }

    @Override
    public void handle(RoutingContext context) {
        context.request().pause();
        Session session = context.session();

        String group = session.get("group");

        if (group == null) {
            String userId = context.request().getParam("id");
            if (userId == null) {
                context.fail(ST_BAD_REQUEST);
            }
            userDAL.findUser(userId, reply -> {
                if (reply.succeeded()) {
                    UserModel userModel = UserModel.createFromJson(reply.result());

                    if (userModel.isFound() && userModel.hasGroupName()) {
                        session.put("group", userModel.getGroupName());
                    }
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
