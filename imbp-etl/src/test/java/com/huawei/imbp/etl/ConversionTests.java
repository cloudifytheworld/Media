package com.huawei.imbp.etl;

import com.datastax.driver.core.LocalDate;
import com.huawei.imbp.etl.common.DataType;
import com.huawei.imbp.etl.transform.DateConversion;
import com.huawei.imbp.etl.transform.IntConversion;
import com.huawei.imbp.etl.transform.TextConversion;
import com.huawei.imbp.etl.transform.TimestampConversion;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;

/**
 * @author Charles(Li) Cai
 * @date 4/26/2019
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConversionTests {

    @Test
    public void contextLoads() {}

    @Test
    public void TestTimestampConversion() {

        TimestampConversion conversion = new TimestampConversion();
        try {
            String timestamp = conversion.convertDateToDateFormat("1556303657138", "yyyy-MM-dd HH:mm:ss", null);
            Assert.assertNotNull(timestamp);

            Timestamp timestamp1 = (Timestamp)conversion.convertNumberToTimestamp("1556303657138", null);
            Assert.assertEquals(1556303657138L, timestamp1.getTime());

            String localDate2 = (String)conversion.convertNumberToTimestamp("1556303657138", "yyyy/MM/dd");
            Assert.assertEquals("2019/04/26", localDate2);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void TestDateConversion() {

        DateConversion conversion = new DateConversion();
        try {
            String localDate = conversion.convertDateToDate("26/04/2019", "MM-dd-yyyy", "dd/MM/yyyy");
            Assert.assertEquals("04-26-2019", localDate);

            LocalDate localDate1 = (LocalDate)conversion.convertNumberToDate("1556303657138", null);
            Assert.assertEquals("2019-04-26", localDate1.toString());

            String localDate2 = (String)conversion.convertNumberToDate("1556303657138", "MM-dd-yyyy");
            Assert.assertEquals("04-26-2019", localDate2);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void TestTextConversion(){

        TextConversion textConversion = new TextConversion();
        try{

            String data = textConversion.convertNumberToTextFormat("3456789", "###,###");
            Assert.assertEquals(data, "3,456,789");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
