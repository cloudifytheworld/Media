package com.huawei.mfg.rest;

import com.google.common.base.Stopwatch;
import com.huawei.mfg.conf.RestfulClientConfiguration;
import com.huawei.mfg.pojo.MfgRequest;
import com.huawei.mfg.util.CommonConstants;
import com.huawei.mfg.util.MfgConfigurationException;
import com.huawei.mfg.util.MfgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.huawei.mfg.conf.RestfulClientConfiguration.DEFAULT_CONFIG_FILE;
import static com.huawei.mfg.rest.RestClientConstants.*;

public class DeviceStatusPipelineRestfulTestAlpha extends BasePerformanceTest {
    private static Logger logger = LoggerFactory.getLogger(DeviceStatusPipelineRestfulTestAlpha.class);
    protected static final String DEFAULT_TEST_FACTORY = "R5-WH01";
    protected static final String DEFAULT_TEST_EQUIPMENT = null;
//    private static final int NUMBER_OF_RUNS = 1_000_000;
    private static final int NUMBER_OF_RUNS = 1;
    private RestfulClientConfiguration config;
    private String host;
    private int port;
    private List<Map<String, Object>> mapList;

    @BeforeClass
    void setup() throws MfgConfigurationException {
        this.config = RestfulClientConfiguration.load(DEFAULT_CONFIG_FILE);
        Assert.assertNotNull(this.config, "No configuration is loaded");

        String restHosts = config.getString(MFG_ETL_HOSTS);
        Assert.assertNotNull(restHosts, "RESTful hosts are not configured");

        String[] parts = restHosts.split(",");

        port = DEFAULT_ETL_RESTFUL_SERVICE_PORT;
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
            break;
        }

        Assert.assertNotNull(this.host, "RESTful host must be configured");

        String sampleDataDir = config.getString(ETL_SAMPLE_DATA_DIR);
        Assert.assertNotNull(sampleDataDir, "Sample data dir must be specified");

        this.mapList = this.loadDataAsMap(sampleDataDir, "device-status-table.txt");
        Assert.assertNotNull(mapList, "Sample data file contains no records");
    }

    @Test
    private void ingest() throws MfgConfigurationException {
        MfgRestClient client = new MfgRestClient(host, port, "/v1/publish", MediaType.APPLICATION_JSON);
        Stopwatch stopwatch = Stopwatch.createStarted();
        int count = 0;
        Random random = new Random();
        String system = "agv";
        String sender = "localhost";
        CommonConstants.EtlProcessType etlProcessType = CommonConstants.EtlProcessType.NORMAL;

        logger.debug("Starting at {}", new Date());

        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            Map<String, Object> map = mapList.get(random.nextInt(mapList.size()));
            int rand = random.nextInt(targets.length);
            String targetName = targetNames[rand];
            CommonConstants.Target target = targets[rand];

            try {
                String payload = objectMapper.writeValueAsString(map);
                MfgRequest request = new MfgRequest(
                        system,
                        DEFAULT_TEST_FACTORY, //faked factory
                        DEFAULT_TEST_EQUIPMENT,
                        "device_status",
                        sender,
                        targetName,
                        "[" + payload + "]",
                        etlProcessType);

                //String message = objectMapper.writeValueAsString(request);

                try {
                    Response response = client.post(request, MediaType.APPLICATION_JSON);
                    count++;
                    if (count % 100 == 0) {
                        System.err.println("Ingest " + count + " device_status messages");
                    }
                } catch (MfgException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            this.sleep(5);
        }

        logger.debug("Ended at {}", new Date());
        stopwatch.stop();
        System.err.println("Total processing time: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " (ms)!");

        client.close();
    }

    protected DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

}
