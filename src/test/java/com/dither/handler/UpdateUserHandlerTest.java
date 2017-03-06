package com.dither.handler;

import com.dither.dal.MongoUserDAL;
import com.dither.dal.UserDAL;
import com.dither.model.UserGroupModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Test update user handler
 */
public class UpdateUserHandlerTest extends HandlerTest {
    private UserDAL userDAL;
    private RoutingContext routingContext;
    private Session session;
    private HttpServerRequest httpServerRequest;
    private UserGroupModel userGroupModel;

    @Before
    public void setUp() {
        userDAL = mock(MongoUserDAL.class);
        userGroupModel = mock(UserGroupModel.class);
        routingContext = mock(RoutingContext.class);
        session = mock(Session.class);
        httpServerRequest = mock(HttpServerRequest.class);

        when(routingContext.session()).thenReturn(session);
        when(routingContext.request()).thenReturn(httpServerRequest);
    }

    /**
     * Test handler when session contains groupName should trigger only next on context
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithGroupName() throws Exception {
        when(session.get("group")).thenReturn("groupName");

        UpdateUserHandler updateUserHandler = new UpdateUserHandler(userDAL, userGroupModel);
        updateUserHandler.handle(routingContext);

        // should never touch user
        verify(userDAL, never()).updateGroupNameById(any(), any(), any());
        // main purpose of class
        verify(routingContext, times(1)).next();
    }

    /**
     * Test update handler with success async update should put selected group into session
     *
     * @throws Exception
     */
    @Test
    public void testHandleUpdateSuccess() throws Exception {
        String userId = "testUserId";
        String groupName = "testGroupName";
        AsyncResult<Void> result = getAsyncResult(true);

        when(session.get("group")).thenReturn(null);
        when(httpServerRequest.getParam("id")).thenReturn(userId);
        when(userGroupModel.peek()).thenReturn(groupName);
        when(userDAL.updateGroupNameById(eq(userId), eq(groupName), anyObject())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Handler<AsyncResult<Void>> handler = (Handler<AsyncResult<Void>>) invocation.getArguments()[2];
            handler.handle(result);
            return null;
        }).thenReturn(userDAL);

        UpdateUserHandler updateUserHandler = new UpdateUserHandler(userDAL, userGroupModel);
        updateUserHandler.handle(routingContext);

        // should put peeked group into session
        verify(session, times(1)).put(eq("group"), eq(groupName));
        // if update was success we should trigger next handler
        verify(routingContext, times(1)).next();
    }

    /**
     * Test update handler with failed async update should trigger internal server error
     *
     * @throws Exception
     */
    @Test
    public void testHandleUpdateFail() throws Exception {
        String userId = "testUserId";
        String groupName = "testGroupName";
        AsyncResult<Void> result = getAsyncResult(false);

        when(session.get("group")).thenReturn(null);
        when(httpServerRequest.getParam("id")).thenReturn(userId);
        when(userGroupModel.peek()).thenReturn(groupName);
        when(userDAL.updateGroupNameById(eq(userId), eq(groupName), anyObject())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Handler<AsyncResult<Void>> handler = (Handler<AsyncResult<Void>>) invocation.getArguments()[2];
            handler.handle(result);
            return null;
        }).thenReturn(userDAL);

        UpdateUserHandler updateUserHandler = new UpdateUserHandler(userDAL, userGroupModel);
        updateUserHandler.handle(routingContext);

        // should never put peeked group into session
        verify(session, never()).put(eq("group"), eq(groupName));
        // if update was failure we should fail with internal server error
        verify(routingContext, times(1)).fail(500);
    }
}