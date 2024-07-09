//package de.kubbillum.masterthesis.rxcheckwrapper.config;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.MapperFeature;
//import com.fasterxml.jackson.databind.PropertyNamingStrategies;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
//
//@Configuration
//public class XmlConfig {
//
//	private static final JsonSerializer<?> LOCAL_DATETIME_SERIALIZER = null;
//
//	@Bean
//	public MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter() {
//		XmlMapper xmlMapper = new XmlMapper();
//		//xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE);
//		//xmlMapper.setDefaultMergeable(true);
//		return new MappingJackson2XmlHttpMessageConverter(xmlMapper);
//	}
//	@Configuration
//	class Configs {
//	    @Bean
//	    public Jackson2ObjectMapperBuilderCustomizer initJackson() {
//	        Jackson2ObjectMapperBuilderCustomizer c = new Jackson2ObjectMapperBuilderCustomizer() {
//	            @Override
//	            public void customize(Jackson2ObjectMapperBuilder builder) {
//	                builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
//	            }
//	        };
//
//	        return c;
//	    }
//	}
//
////	@Bean
////	public Jackson2ObjectMapperBuilderCustomizer initJackson() {
////		Jackson2ObjectMapperBuilderCustomizer c = new Jackson2ObjectMapperBuilderCustomizer() {
////			@Override
////			public void customize(Jackson2ObjectMapperBuilder builder) {
////				builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
////			}
////		};
////
////		return c;
////	}
//	
////	@Bean
////	@Primary
////	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
////	    return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL)
////	      .serializers(LOCAL_DATETIME_SERIALIZER).featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
////	}
//}
