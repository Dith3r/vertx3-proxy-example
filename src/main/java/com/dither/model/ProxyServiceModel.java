package com.dither.model;

import com.dither.exception.InvalidServiceNameException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Random;

/**
 * Finds service assignment to user group and peek one random host to handle request
 */
public class ProxyServiceModel {

    private final JsonObject services;
    private final Random generator;

    @Inject
    public ProxyServiceModel(@Named("config.services") JsonObject services, Random generator) {
        this.services = services;
        this.generator = generator;
    }

    /**
     * Peek random host for service name or throw exception if service is not defined in configuration file
     *
     * @param group String service name
     * @return JsonObject
     * @throws InvalidServiceNameException
     */
    public JsonObject peekHost(String group) throws InvalidServiceNameException {
        if (services.containsKey(group)) {
            JsonObject serviceArray = services.getJsonObject(group);
            JsonArray hostsArray = serviceArray.getJsonArray("hosts");
            int index = generator.nextInt(hostsArray.size());

            return hostsArray.getJsonObject(index);
        } else {
            throw new InvalidServiceNameException(group);
        }
    }
}

