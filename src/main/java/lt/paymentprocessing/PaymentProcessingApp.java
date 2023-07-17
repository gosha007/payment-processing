package lt.paymentprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PaymentProcessingApp {

	public static void main(String[] args) {
		SpringApplication.run(PaymentProcessingApp.class, args);
	}

}
