package romanescu.backend.application;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;

import javax.annotation.Resource;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@PropertySource(value = "classpath:elasticsearch.properties")
public class Application {
	private Properties properties;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Resource
	private Environment environment;

	@Bean
	public Client client() throws NumberFormatException, UnknownHostException {
		TransportClient client = TransportClient.builder().settings(settings()).build();
		TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName(environment.getProperty("elasticsearch.host")),
				Integer.parseInt(environment.getProperty("elasticsearch.port")));
		
		client.addTransportAddress(address);
		return client;
	}

	private Settings settings() {
		if (properties != null) {
			return Settings.builder().put(properties).build();
		}
		return Settings.builder()
			.put("cluster.name", environment.getProperty("elasticsearch.cluster"))
			.put("node.client", true)
			.put("client.transport.sniff", true)
			.put("client.transport.ignore_cluster_name", false)
			.build();

	}	

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws NumberFormatException, UnknownHostException {
		return new ElasticsearchTemplate(client());
	}
	
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}