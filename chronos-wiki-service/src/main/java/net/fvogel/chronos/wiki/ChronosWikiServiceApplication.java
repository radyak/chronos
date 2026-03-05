package net.fvogel.chronos.wiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "net.fvogel.chronos.wiki",
        "net.fvogel.chronos.commons"
})
public class ChronosWikiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronosWikiServiceApplication.class, args);
    }

}
