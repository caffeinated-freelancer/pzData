package ncu.mac.pzdata.config;

import ncu.mac.pzdata.properties.ApplicationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class PropertiesConfig {
//    @Bean
//    ApplicationProperties applicationProperties(ApplicationProperties applicationProperties) {
//        return applicationProperties;
//    }
}
