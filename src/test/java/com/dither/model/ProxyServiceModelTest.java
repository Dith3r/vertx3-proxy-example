package com.dither.model;

import com.dither.exception.InvalidServiceNameException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test Proxy service model
 */
public class ProxyServiceModelTest {

    private Random random;
    private JsonObject serviceConfiguration;

    @Before
    public void setUp() {
        this.random = mock(Random.class);
        this.serviceConfiguration = new JsonObject();
    }

    /**
     * Check if class striped from randomness will produce same and predictable result for given values
     */
    @Test
    public void testPeekHostWithValidName() throws Exception {
        JsonObject host1 = new JsonObject().put("host", "10.10.10.10").put("port", "9000");
        JsonObject host2 = new JsonObject().put("host", "20.20.20.20").put("port", "8000");
        JsonObject host3 = new JsonObject().put("host", "30.30.30.30").put("port", "7000");
        JsonObject host4 = new JsonObject().put("host", "40.40.40.40").put("port", "6000");

        String groupA = "A";
        String groupB = "B";

        serviceConfiguration.put(groupA, new JsonObject()
                .put("hosts", new JsonArray()
                                .add(host1)
                                .add(host2)
                ))
                .put(groupB, new JsonObject().put("hosts", new JsonArray()
                                .add(host3)
                                .add(host4)
                ));
        when(random.nextInt(2)).thenReturn(0).thenReturn(1);
        ProxyServiceModel proxyServiceModel = new ProxyServiceModel(serviceConfiguration, random);
        assertEquals("Should return first host from group A", host1, proxyServiceModel.peekHost(groupA));
        assertEquals("Should return second host from group B", host4, proxyServiceModel.peekHost(groupB));
    }

    /**
     * Test should trigger exception because name of service is not found
     *
     * @throws Exception
     */
    @Test(expected = InvalidServiceNameException.class)
    public void testPeekHostWithEmptyConfiguration() throws Exception {
        // should never try to acquire random value
        verify(random, never()).nextInt();

        ProxyServiceModel proxyServiceModel = new ProxyServiceModel(serviceConfiguration, random);
        proxyServiceModel.peekHost("anyName");
    }

    /**
     * Test should trigger exception because name of service is not found
     *
     * @throws Exception
     */
    @Test(expected = InvalidServiceNameException.class)
    public void testPeekHostWithInvalidServiceName() throws Exception {
        JsonObject host1 = new JsonObject().put("host", "10.10.10.10").put("port", "9000");
        JsonObject host2 = new JsonObject().put("host", "20.20.20.20").put("port", "8000");

        String groupA = "A";

        serviceConfiguration.put(groupA, new JsonObject()
                .put("hosts", new JsonArray()
                                .add(host1)
                                .add(host2)
                ));
        // should never try to acquire random value
        verify(random, never()).nextInt();

        ProxyServiceModel proxyServiceModel = new ProxyServiceModel(serviceConfiguration, random);
        proxyServiceModel.peekHost("nonExistent");
    }
}