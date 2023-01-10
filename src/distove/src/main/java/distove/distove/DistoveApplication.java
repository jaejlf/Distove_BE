package distove.distove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class DistoveApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistoveApplication.class, args);
	}

}
