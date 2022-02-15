package com.demiglace.springweb.controllers;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demiglace.springweb.entities.Product;
import com.demiglace.springweb.repos.ProductRepository;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Product Rest Endpoint")
//@Hidden
public class ProductRestController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);
	
	@Autowired
	ProductRepository repository;

	@RequestMapping(value = "/products/", method = RequestMethod.GET)
	@Hidden
	public List<Product> getProducts() {
		return repository.findAll();
	}

	@Cacheable("product-cache")
	@Transactional(readOnly = true)
	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	@Operation(summary = "Returns a product", description = "takes id, returns single product")
	public @ApiResponse(description = "Product object") Product getProduct(@Parameter(description = "Id of the product") @PathVariable("id") int id) {
		LOGGER.info("finding product by ID" + id);
		return repository.findById(id).get();
	}

	@RequestMapping(value = "/products/", method = RequestMethod.POST)
	public Product createProduct(@Valid @RequestBody Product product) {
		return repository.save(product);
	}

	@RequestMapping(value = "/products/", method = RequestMethod.PUT)
	public Product updateProduct(@RequestBody Product product) {
		return repository.save(product);
	}

	@CacheEvict("product-cache")
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	public void deleteProduct(@PathVariable("id") int id) {
		repository.deleteById(id);
	}
}
