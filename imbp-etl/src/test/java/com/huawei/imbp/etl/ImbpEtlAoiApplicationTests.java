package com.huawei.imbp.etl;

import com.huawei.imbp.etl.service.CassandraRepository;
import com.huawei.imbp.etl.service.build.BuildStatementService;
import com.huawei.imbp.etl.util.DataType;
import com.huawei.imbp.etl.util.DataUtil;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.*;
import com.datastax.driver.core.Statement;


import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImbpEtlAoiApplicationTests {

    File imageFile;
    File largeImageFile;

    @Autowired
    BuildStatementService statementService;

    @Autowired
    public CassandraRepository cassandraRepository;

    @Before
    public void setUp(){
        imageFile = new File("src/test/java/resources/20181101020226495.bmp");
        largeImageFile = new File("src/test/java/resources/large.jpg");

    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void Test64Encoding() throws  Exception{

        assert(imageFile.exists());
        Path path = Paths.get(imageFile.getPath());
        byte[] imageInByte = Files.readAllBytes(path);

        String imageStr = Base64.encodeBase64String(imageInByte);
        ByteBuffer byteBuffer = (ByteBuffer) DataType.buildDataType(null, "blob", imageStr);

        Assert.assertEquals(imageInByte.length,  byteBuffer.array().length);

    }

    @Test
    public void TestLargeImage() throws Exception{

        assert(largeImageFile.exists());
        Map rawData = new HashMap();
        Map payload = new HashMap();

        rawData.put("system", "aoi_single_component_image_0");
        rawData.put("table", "aoi_single_component_image_l");
        rawData.put("destination", "cassandra");
        rawData.put("payload", payload);

        Path path = Paths.get(largeImageFile.getPath());
        byte[] imageInByte = Files.readAllBytes(path);
        String imageStr = Base64.encodeBase64String(imageInByte);

        payload.put("created_day", "20190327");
        payload.put("device_type", "whatever");
        payload.put("label", "mylabel");
        payload.put("created_time", System.currentTimeMillis());
        payload.put("board_id", "234");
        payload.put("board_loc", "san jose");
        payload.put("extension", "willknow");
        payload.put("file_name", "123.bmp");
        payload.put("product_type", "danban");
        payload.put("image", imageStr);

        Statement statement = statementService.buildCassandraOnInsertLarge(rawData);
        Assert.assertNotNull(statement);

    }

}