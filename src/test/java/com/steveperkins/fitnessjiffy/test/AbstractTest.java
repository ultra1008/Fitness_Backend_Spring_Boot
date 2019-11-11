package com.steveperkins.fitnessjiffy.test;

import com.steveperkins.fitnessjiffy.service.ReportDataService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class AbstractTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    DataSource dataSource;

    @Autowired
    ReportDataService reportDataService;

    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void before() throws Exception {
        try (
                final Connection connection = dataSource.getConnection();
                final Statement statement = connection.createStatement();
        ) {
            statement.execute("DROP ALL OBJECTS");
            statement.execute("RUNSCRIPT FROM 'classpath:/backup.sql'");
        }
    }

    @After
    public void after() throws InterruptedException {
        while (!reportDataService.isIdle()) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        }
    }

}
