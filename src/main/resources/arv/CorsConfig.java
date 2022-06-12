//package ch.redanz.redanzCore.security.config;
//
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/login")
////                        .allowedMethods("GET", "PUT", "POST", "DELETE")
//          .allowedOrigins("http;//localhost:4200");
//    }
//
//    @Bean
//    public WebMvcConfigurer webCorsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
////                        .allowedMethods("GET", "PUT", "POST", "DELETE")
//                        .allowedMethods("*")
//                        .allowedHeaders("*")
//                        .allowedOrigins("*");
//
//            }
//        };
//    }
//}
