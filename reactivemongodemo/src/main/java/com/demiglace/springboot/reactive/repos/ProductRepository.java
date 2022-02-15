package com.demiglace.springboot.reactive.repos;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.demiglace.springboot.reactive.entities.Product;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
	
}
