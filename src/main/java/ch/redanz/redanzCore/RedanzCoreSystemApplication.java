package ch.redanz.redanzCore;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
@EnableEncryptableProperties
public class RedanzCoreSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedanzCoreSystemApplication.class, args);
	}
}


