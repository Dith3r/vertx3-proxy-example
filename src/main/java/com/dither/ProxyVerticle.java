package com.dither;

import com.dither.handler.ProxyServiceHandler;
import com.dither.handler.FindUserHandler;
import com.dither.handler.UpdateUserHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;

/**
 * Main proxy verticle
 * <p>
 * Used indirectly by vertx.starter class
 * </p>
 */
@SuppressWarnings("unused")
public class ProxyVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        Injector injector = Guice.createInjector(new ProxyModule(vertx, context.config()));
        JsonObject proxyConfig = injector.getInstance(Key.get(JsonObject.class, Names.named("config.proxy")));

        Router router = Router.router(vertx);
        // handlers for all routes
        router.route().handler(injector.getInstance(CookieHandler.class));
        router.route().handler(injector.getInstance(SessionHandler.class));
        router.route().handler(injector.getInstance(FindUserHandler.class));
        router.route().handler(injector.getInstance(UpdateUserHandler.class));
        router.route().handler(injector.getInstance(ProxyServiceHandler.class));

        vertx.createHttpServer().requestHandler(router::accept).listen(proxyConfig.getInteger("port"), proxyConfig.getString("host"), result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });
    }
}