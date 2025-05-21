package systems.lordes.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@Slf4j
//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class }) //ToDo:REMOVE
public class FakeRestaurantBeApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(FakeRestaurantBeApplication.class, args);
        } catch (Exception e) {
            log.error("Cannot start spring application", e);
            System.exit(-1);
        }
    }

}
