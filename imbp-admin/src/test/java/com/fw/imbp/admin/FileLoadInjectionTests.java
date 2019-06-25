package com.fw.imbp.admin;

import com.fw.imbp.admin.service.FileLoadService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileLoadInjectionTests {

    @Autowired
    FileLoadService fileLoadService;

    @Test
    public void contextLoads() {
    }


    @Test
    public void testInjectionSampleData(){

        fileLoadService.parse();
    }
}
