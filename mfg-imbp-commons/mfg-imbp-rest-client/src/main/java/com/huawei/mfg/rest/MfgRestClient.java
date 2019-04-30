package com.huawei.mfg.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.huawei.mfg.pojo.MfgRequest;
import com.huawei.mfg.pojo.MfgResponse;
import com.huawei.mfg.util.MfgException;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.BindException;
import java.net.ConnectException;
import java.util.Map;

public class MfgRestClient {
    private static Logger logger = LoggerFactory.getLogger(MfgRestClient.class);
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int READ_TIMEOUT    = 30000;
    private static final int DEFAULT_RETRIES = 3;
    private Client client;
    private Invocation.Builder invocationBuilder;
    private int retries;
    private String host;
    private int port;
    private String contextPath;
    private String mediaType;
    private ObjectMapper objectMapper;
    private String requestUrlStr;

    public MfgRestClient(String host, int port, String contextPath, String mediaType) {
        this.host = host;
        this.port = port;
        this.contextPath = contextPath;
        this.mediaType = mediaType;
        this.retries = DEFAULT_RETRIES;

        this.objectMapper = new ObjectMapper();

        this.createClientBuilder();
    }

    private void createClientBuilder() {
        this.client = ClientBuilder.newClient();
        this.client.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
        this.client.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);

        if (!Strings.isNullOrEmpty(contextPath)) {
            if (contextPath.startsWith("/")) {
                requestUrlStr = String.format("http://%s:%d%s", host, port, contextPath);
            }
            else {
                requestUrlStr = String.format("http://%s:%d/%s", host, port, contextPath);
            }
        }
        else {
            requestUrlStr = String.format("http://%s:%d", host, port);
        }
        logger.debug("{}", requestUrlStr);

        WebTarget webTarget = this.client.target(requestUrlStr);
        this.invocationBuilder = webTarget.request(mediaType);
    }

    public <T> Response post(T t, String mediaType) throws MfgException {
        try {
            String message = this.objectMapper.writeValueAsString(t);
            return this.post(message, mediaType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new MfgException("", e);
        }
    }

    public Response post(MfgRequest request, String mediaType) throws MfgException {
        try {
            String message = this.objectMapper.writeValueAsString(request);
            return this.post(message, mediaType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new MfgException("", e);
        }
    }

    private Response post(String message, String mediaType) throws MfgException {
        Response response = null;

        int tries = 0;
        while (tries < this.retries) {
            tries++;
            try {
                response = this.invocationBuilder.post(Entity.entity(message, mediaType));

                if (response.getStatus() != 200) {
                    if (response.getStatus() == 406) {
                        logger.debug("{}", response.readEntity(MfgResponse.class));
                    } else {
                        this.close();
                        throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
                    }
                } else {
                    logger.debug("Posting {}", message);
                    logger.debug("Got 200 status");
                }
                return response;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                this.close();
                throw new MfgException("IllegalArgumentException caught while creating WS post Entity", e);
            } catch (ProcessingException e) {
                e.printStackTrace();
                Throwable throwable = e.getCause();
                throwable.printStackTrace();
                this.close();

                if (throwable instanceof ConnectException) {
                    this.createClientBuilder();
                } else if (throwable instanceof BindException) {
                    this.createClientBuilder();
                }
                else {
                    throw new MfgException("", e);
                }
            }
        }
        return null;
    }

    public Response get(String path, Map<String, String> queryParams, String mediaType) throws MfgException {
        WebTarget webTarget = buildWebTarget(path);

        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                webTarget.queryParam(entry.getKey(), entry.getValue());
            }
        }

        Invocation.Builder builder = webTarget.request();
        if (!Strings.isNullOrEmpty(mediaType)) {
            builder = builder.accept(mediaType);
        }

        Invocation invocation = builder.buildGet();

        int tries = 0;
        while (tries < this.retries) {
            tries++;
            try {
                Response response = invocation.invoke();
                return response;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                this.close();
                throw new MfgException("IllegalArgumentException caught while creating WS post Entity", e);
            } catch (ProcessingException e) {
                e.printStackTrace();
                Throwable throwable = e.getCause();
                throwable.printStackTrace();
                this.close();

                if (throwable instanceof ConnectException) {
                    this.createClientBuilder();
                } else if (throwable instanceof BindException) {
                    this.createClientBuilder();
                } else {
                    throw new MfgException("", e);
                }
            }
        }
        return null;
    }

    private WebTarget buildWebTarget(String path) {
        WebTarget webTarget = null;
        if (!Strings.isNullOrEmpty(path)) {
            String fullPath = this.requestUrlStr;
            if (this.requestUrlStr.endsWith("/")) {
                fullPath += path;
            } else {
                fullPath += "/" + path;
            }
            webTarget = this.client.target(fullPath);
        } else {
            webTarget = this.client.target(this.requestUrlStr);
        }
        return webTarget;
    }

    public Invocation createGetInvocation(String path, Map<String, String> queryParams, String mediaType) throws MfgException {
        WebTarget webTarget = buildWebTarget(path);

        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                webTarget.queryParam(entry.getKey(), entry.getValue());
            }
        }

        Invocation.Builder builder = webTarget.request();
        if (!Strings.isNullOrEmpty(mediaType)) {
            builder = builder.accept(mediaType);
        }

        return builder.buildGet();
    }

    public Object get(Invocation invocation) throws MfgException {
        int tries = 0;
        while (tries < this.retries) {
            tries++;
            try {
                Response response = invocation.invoke();
                return response;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                this.close();
                throw new MfgException("IllegalArgumentException caught while invoking WS post Entity", e);
            } catch (ProcessingException e) {
                e.printStackTrace();
                Throwable throwable = e.getCause();
                throwable.printStackTrace();
                this.close();

                if (throwable instanceof ConnectException) {
                    this.createClientBuilder();
                } else if (throwable instanceof BindException) {
                    this.createClientBuilder();
                } else {
                    throw new MfgException("", e);
                }
            }
        }
        return null;
    }

    public <T> Response put(String path, T object) throws MfgException {
        WebTarget webTarget = buildWebTarget(path);

        Invocation.Builder builder = webTarget.request();
        if (!Strings.isNullOrEmpty(mediaType)) {
            builder = builder.accept(mediaType);
        }
        Invocation invocation = builder.buildPut(Entity.entity(object, mediaType));

        int tries = 0;
        while (tries < this.retries) {
            tries++;
            try {
                Response response = invocation.invoke();
                return response;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                this.close();
                throw new MfgException("IllegalArgumentException caught while invoking WS put Entity", e);
            } catch (ProcessingException e) {
                e.printStackTrace();
                Throwable throwable = e.getCause();
                throwable.printStackTrace();
                this.close();

                if (throwable instanceof ConnectException) {
                    this.createClientBuilder();
                } else if (throwable instanceof BindException) {
                    this.createClientBuilder();
                } else {
                    throw new MfgException("", e);
                }
            }
        }
        return null;
    }

    public <T> Response delete(String path) throws MfgException {
        WebTarget webTarget = buildWebTarget(path);

        Invocation.Builder builder = webTarget.request();
        if (!Strings.isNullOrEmpty(mediaType)) {
            builder = builder.accept(mediaType);
        }
        Invocation invocation = builder.buildDelete();

        int tries = 0;
        while (tries < this.retries) {
            tries++;
            try {
                Response response = invocation.invoke();
                return response;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                this.close();
                throw new MfgException("IllegalArgumentException caught while invoking WS delete Entity", e);
            } catch (ProcessingException e) {
                e.printStackTrace();
                Throwable throwable = e.getCause();
                throwable.printStackTrace();
                this.close();

                if (throwable instanceof ConnectException) {
                    this.createClientBuilder();
                } else if (throwable instanceof BindException) {
                    this.createClientBuilder();
                } else {
                    throw new MfgException("", e);
                }
            }
        }
        return null;
    }

    public void close() {
        if (this.client != null) {
            this.client.close();
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        MfgRestClient client = new MfgRestClient("10.208.51.107", 8500, null, null);
        try {
            Invocation invocation = client.createGetInvocation("/v1/catalog/nodes", null, "application/json");
            long end = System.currentTimeMillis();
            System.err.println("Time to create Invocation: " + (end-start) + "(ms)");
            System.err.println(invocation);

            client.get(invocation);
            start = System.currentTimeMillis();
            System.err.println("Time to make the first get: " + (start - end) + "(ms)");

            client.get(invocation);
            end = System.currentTimeMillis();
            System.err.println("Time to make the 2nd get: " + (end - start) + "(ms)");

////            System.err.println(response.readEntity(String.class));
//            Object obj = response.readEntity(new GenericType<List<ConsulNode>>() {});
//            System.err.println(obj.getClass());
//
//            obj = response.readEntity(new GenericType<List<ConsulNode>>() {});
//            System.err.println(obj.getClass());

            client = new MfgRestClient("10.208.51.107", 8500, null, MediaType.APPLICATION_JSON);
            Response response = client.put("/v1/kv/config/test", "Hello");
            System.err.println(response);

            response = client.delete("/v1/kv/config/test");
            System.err.println(response);
        } catch (MfgException e) {
            e.printStackTrace();
        }

    }

}
