package com.huawei.mfg.pool;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.huawei.mfg.conf.RestfulClientConfiguration;
import com.huawei.mfg.rest.MfgRestClient;
import com.huawei.mfg.util.CommonConstants;
import com.huawei.mfg.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.huawei.mfg.conf.RestfulClientConfiguration.DEFAULT_ETL_RESTFUL_SERVICE_PORT;
import static com.huawei.mfg.conf.RestfulClientConfiguration.MFG_ETL_REST_HOSTS;
import static com.huawei.mfg.conf.RestfulClientConfiguration.MFG_ETL_REST_MEDIA_TYPE;

/**
 * TODO: should be implemented as a Pool
 */
public class RestDatasource {
    private static final Logger logger = LoggerFactory.getLogger(RestDatasource.class);
    private static final List<String> VALID_MEDIA_TYPES = new ArrayList<>();
    private RestfulClientConfiguration config;
    private List<MfgRestClient> restClients;
    private List<Pair<String, Integer>> hostPorts;
    private String contextPath;
    private String mediaType;
    private Random random;

    static {
        VALID_MEDIA_TYPES.add(MediaType.APPLICATION_JSON);
        VALID_MEDIA_TYPES.add(MediaType.TEXT_PLAIN);
        VALID_MEDIA_TYPES.add(MediaType.APPLICATION_XML);
        VALID_MEDIA_TYPES.add(MediaType.TEXT_HTML);
        VALID_MEDIA_TYPES.add(MediaType.TEXT_XML);
    };

    public RestDatasource(RestfulClientConfiguration config) {
        this.config = config;
        this.restClients = new ArrayList<>();
        this.random = new Random();
        this.hostPorts = new ArrayList<>();
    }

    private void configure() {
        Preconditions.checkNotNull(this.config);

        String restHosts = this.config.getString(MFG_ETL_REST_HOSTS);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(restHosts), "Hosts for MFG Restful service must be specified in config.properties");

        this.mediaType = this.config.getString(MFG_ETL_REST_MEDIA_TYPE);
        if (Strings.isNullOrEmpty(this.mediaType)) {
            this.mediaType = MediaType.APPLICATION_JSON;
        }
        else {
            boolean validMediaType = false;
            for (String mt : VALID_MEDIA_TYPES) {
                if (mediaType.equalsIgnoreCase(mt)) {
                    validMediaType = true;
                    break;
                }
            }
            Preconditions.checkArgument(validMediaType, "A valid media type for MFG Restful client must be specified in config.properties");
        }

        String[] parts = restHosts.split(",");

        String host = null;
        int port = DEFAULT_ETL_RESTFUL_SERVICE_PORT;
        for (String part : parts) {
            String[] pair = part.split(":");
            if (pair.length == 2) {
                host = pair[0];
                try {
                    port = Integer.parseInt(pair[1]);
                }
                catch (NumberFormatException e) {
                    port = DEFAULT_ETL_RESTFUL_SERVICE_PORT;
                }
            }
            else {
                host = pair[0];
            }

            this.hostPorts.add(Pair.of(host, port));
        }

        Preconditions.checkNotNull(this.hostPorts, "Hosts for MFG Restful service must be specified in config.properties");
        Preconditions.checkArgument(!this.hostPorts.isEmpty(), "there is at least one MFG Restful service configured in config.properties");

        this.contextPath = String.format("/%s/%s", CommonConstants.RESTFUL_API_VERSION_1, CommonConstants.RESTFUL_API_PUBLISH_PATH);
    }

    public void connect() {
        this.configure();

        try {
            for (Pair<String, Integer> pair : this.hostPorts) {
                MfgRestClient restClient = new MfgRestClient(pair.getFirst(), pair.getSecond(), this.contextPath, this.mediaType);
                this.restClients.add(restClient);
            }
        }
        catch (ProcessingException e) {
            e.printStackTrace();
        }
    }

    public MfgRestClient getMfgRestClient() {
        if (this.restClients.isEmpty()) return null;
        return this.restClients.get(this.random.nextInt(this.restClients.size()));
    }

    public void close()throws SQLException {
        if (this.restClients.isEmpty()) return ;
        this.restClients.forEach(r -> {
            r.close();
        });
    }

}
