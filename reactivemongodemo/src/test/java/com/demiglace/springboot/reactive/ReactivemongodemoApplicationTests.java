package com.demiglace.springboot.reactive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.demiglace.springboot.reactive.controllers.ProductController;
import com.demiglace.springboot.reactive.entities.Product;
import com.demiglace.springboot.reactive.repos.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class ReactivemongodemoApplicationTests {

	@Autowired
	ProductController controller;

	@MockBean
	ProductRepository repo;

	@Test
	void testAddProduct() {
		Product product = new Product(null, "Legion", "Gaming Laptop", 2000d);
		Product savedProduct = new Product("abc123", "Legion", "Gaming Laptop", 2000d);
		when(repo.save(product)).thenReturn(Mono.just(savedProduct));

		StepVerifier.create(controller.addProduct(product)).assertNext(p -> {
			assertNotNull(p);
			assertNotNull(p.getId());
			assertEquals("abc123", p.getId());
		}).expectComplete().verify();
		verify(repo).save(product);
	}

	@Test
	void testGetProducts() {
		when(repo.findAll()).thenReturn(Flux.just(
				new Product("abc123", "Legion", "Gaming Laptop", 2000d),
				new Product("abc456", "Legion", "Gaming Laptop", 2000d),
				new Product("abc789", "Legion", "Gaming Laptop", 2000d)));
		StepVerifier.create(controller.getProducts())
			.expectNextCount(3)
			.expectComplete()
			.verify();
		verify(repo).findAll();
	}
}
