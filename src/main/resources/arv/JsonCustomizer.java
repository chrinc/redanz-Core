//package ch.redanz.redanzCore.json;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.PropertyNamingStrategy;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class JsonCustomizer {
//
//  @Bean
//  public Jackson2ObjectMapperBuilderCustomizer customJson(){
//    return builder -> {
//      // human readable
//      builder.indentOutput(true);
//      // exclude null values
//      builder.serializationInclusion(JsonInclude.Include.NON_NULL);
//      // all lowercase with under score between words
//      builder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//    };
//  }
//}
