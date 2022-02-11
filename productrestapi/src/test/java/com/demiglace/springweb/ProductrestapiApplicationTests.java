package com.demiglace.springweb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.demiglace.springweb.entities.Product;

@SpringBootTest
class ProductrestapiApplicationTests {
	
	@Value("${productrestapi.services.url}")
	private String baseURL;

	@Test
	public void testGetProduct() {
		System.out.println(baseURL);
		RestTemplate restTemplate = new RestTemplate();
		Product product = restTemplate.getForObject(baseURL + "1", Product.class);
		assertNotNull(product);
		assertEquals("Nitro 5", product.getName());
	}

	@Test
	public void testCreateProduct() {
		RestTemplate restTemplate = new RestTemplate();
		Product product = new Product();
		product.setName("LG G6");
		product.setDescription("great phone");
		product.setPrice(200d);
		Product newProduct = restTemplate.postForObject(baseURL, product,
				Product.class);
		assertNotNull(newProduct);
		assertNotNull(newProduct.getId());
		assertEquals("LG G6", newProduct.getName());
	}

	@Test
	public void testUpdateProduct() {
		RestTemplate restTemplate = new RestTemplate();
		Product product = restTemplate.getForObject(baseURL + "1", Product.class);
		product.setPrice(279d);
		restTemplate.put(baseURL, product);
	}
}
