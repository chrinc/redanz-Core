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
//@CrossOrigin(origins="*")
@EnableCaching(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
@EnableEncryptableProperties
public class RedanzCoreSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedanzCoreSystemApplication.class, args);

//		final String fromEmail = "registration@stirit.ch"; //requires valid gmail id
//		final String toEmail = "zeus522@gmail.com"; // can be any email id
//
//		System.out.println("TLSEmail Start");
//		Properties props = new Properties();
//		props.put("mail.smtp.host", "asmtp.mail.hostpoint.ch"); //SMTP Host
//		props.put("mail.smtp.socketFactory.class",
//			"javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
//		props.put("mail.smtp.port", "587"); //SMTP Port
//		props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
//		props.put("mail.smtp.starttls.enable", "true"); //SMTP Port
//		props.setProperty("mail.debug", "true");
//		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
//
//		Authenticator auth = new Authenticator() {
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication(fromEmail, password);
//			}
//		};
//
//		Session session = Session.getDefaultInstance(props, auth);
//		EmailUtil.sendEmail(EmailUtil.getSession(), toEmail,"Registration Successfull", "SSLEmail Testing Body");

//		EmailUtil.sendAttachmentEmail(session, toEmail,"SSLEmail Testing Subject with Attachment", "SSLEmail Testing Body with Attachment");

//		EmailUtil.sendImageEmail(session, toEmail,"SSLEmail Testing Subject with Image", "SSLEmail Testing Body with Image");

	}




	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(
			Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
			"Accept", "Authorization", "Origin, Accept", "X-Requested-With"));
		corsConfiguration.setExposedHeaders(
			Arrays.asList("Origin", "Content-Type", "Accept", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
			)
		);
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}




//	Gson gson = new GsonBuilder()
//		.excludeFieldsWithoutExposeAnnotation()
//		.create();
//	String jsonString = gson.toJson(source);
//	assertEquals(expectedResult, jsonString);
//	@Bean
//	public TaskScheduler taskScheduler(){
//		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
////		threadPoolTaskScheduler.setPoolSize(5);
//		threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
//		return threadPoolTaskScheduler;
//	}

}
