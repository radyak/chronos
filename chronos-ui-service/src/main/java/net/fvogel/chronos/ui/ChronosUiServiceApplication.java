package net.fvogel.chronos.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "net.fvogel.chronos.ui",
        "net.fvogel.chronos.commons"
})
public class ChronosUiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronosUiServiceApplication.class, args);
    }

}
