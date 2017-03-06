package com.dither.handler;

import com.dither.exception.InvalidServiceNameException;
import com.dither.model.ProxyServiceModel;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;

/**
 * Handler for proxy magic between client <-> proxy <-> backend
 */
public class ProxyServiceHandler implements Handler<RoutingContext> {

  /**
   * Used for proxying requests
   */
  private final HttpClient client;
  /**
   * Definitions of service to whom request is going to be delivered
   */
  private final ProxyServiceModel proxyServiceModel;

  @Inject
  public ProxyServiceHandler(HttpClient client, ProxyServiceModel proxyServiceModel) {
    this.client = client;
    this.proxyServiceModel = proxyServiceModel;
  }

  @Override
  public void handle(RoutingContext context) {
    try {
      HttpServerRequest clientRequest = context.request();
      HttpServerResponse clientResponse = clientRequest.response();

      JsonObject host = proxyServiceModel.peekHost(context.session().get("group"));

      HttpClientRequest proxyRequest = client
          .request(clientRequest.method(), host.getInteger("port"), host.getString("host"),
              clientRequest.uri(), innerRequest -> {
                clientResponse.setChunked(true);
                clientResponse.setStatusCode(innerRequest.statusCode());
                clientResponse.headers().setAll(innerRequest.headers());

                innerRequest.handler(clientResponse::write);
                innerRequest.endHandler((V) -> clientResponse.end());
              });
      proxyRequest.setChunked(true);
      proxyRequest.headers().setAll(clientRequest.headers());
      clientRequest.resume();
      clientRequest.handler(proxyRequest::write);
      clientRequest.endHandler((V) -> proxyRequest.end());
    } catch (InvalidServiceNameException exception) {
      context.response()
          .setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
      context.fail(exception);
    }
  }
}