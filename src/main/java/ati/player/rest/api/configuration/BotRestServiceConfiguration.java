package ati.player.rest.api.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ati.player.rest.api")
//@ImportResource({"classpath:/configuration-ws.xml"})
public class BotRestServiceConfiguration {

}
