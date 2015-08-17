package com.dither;

import com.dither.dal.MongoUserDAL;
import com.dither.dal.UserDAL;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * Definition of dependency injection container
 */
public class ProxyModule extends AbstractModule {
    private final Vertx vertx;
    private final JsonObject configuration;

    public ProxyModule(Vertx vertx, JsonObject configuration) {
        this.vertx = vertx;
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(Vertx.class).toInstance(this.vertx);
        bind(UserDAL.class).to(MongoUserDAL.class);

        bind(JsonObject.class).annotatedWith(Names.named("config.mongo")).toInstance(configuration.getJsonObject("mongo"));
        bind(JsonObject.class).annotatedWith(Names.named("config.proxy")).toInstance(configuration.getJsonObject("proxy"));
        bind(JsonObject.class).annotatedWith(Names.named("config.groups")).toInstance(configuration.getJsonObject("groups"));
        bind(JsonObject.class).annotatedWith(Names.named("config.services")).toInstance(configuration.getJsonObject("services"));
    }

    @Provides
    MongoClient provideMongoClient(Vertx vertx, @Named("config.mongo") JsonObject configuration) {
        return MongoClient.createShared(vertx, configuration);
    }

    @Provides
    MongoUserDAL provideMongoUserDAL(MongoClient mongoClient) {
        return new MongoUserDAL(mongoClient);
    }

    @Provides
    HttpClient provideHttpClient(Vertx vertx) {
        return vertx.createHttpClient(new HttpClientOptions());
    }

    @Provides
    CookieHandler provideCookieHandler() {
        return CookieHandler.create();
    }

    @Provides
    SessionHandler provideSessionHandler(Vertx vertx, @Named("config.proxy") JsonObject proxyConfig) {
        SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
        sessionHandler.setNagHttps(proxyConfig.getBoolean("ssl"));
        return sessionHandler;
    }
}
