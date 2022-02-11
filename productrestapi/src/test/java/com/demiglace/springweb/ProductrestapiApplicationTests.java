package com.demiglace.springweb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.demiglace.springweb.entities.Product;

@SpringBootTest
class ProductrestapiApplicationTests {

	@Test
	public void testGetProduct() {
		RestTemplate restTemplate = new RestTemplate();
		Product product = restTemplate.getForObject("http://localhost:8080/productapi/products/1", Product.class);
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
		Product newProduct = restTemplate.postForObject("http://localhost:8080/productapi/products/", product,
				Product.class);
		assertNotNull(newProduct);
		assertNotNull(newProduct.getId());
		assertEquals("LG G6", newProduct.getName());
	}

	@Test
	public void testUpdateProduct() {
		RestTemplate restTemplate = new RestTemplate();
		Product product = restTemplate.getForObject("http://localhost:8080/productapi/products/1", Product.class);
		product.setPrice(279d);
		restTemplate.put("http://localhost:8080/productapi/products/", product);
	}
}
