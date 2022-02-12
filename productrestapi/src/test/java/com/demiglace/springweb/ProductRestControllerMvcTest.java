package com.demiglace.springweb;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.demiglace.springweb.entities.Product;
import com.demiglace.springweb.repos.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@WebMvcTest
public class ProductRestControllerMvcTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductRepository repository;
	
	private static final String PRODUCT_URL = "/productapi/products/";
	private static final String CONTEXT_URL = "/productapi";
	private static final double PRODUCT_PRICE = 2000d;
	private static final String PRODUCT_DESCRIPTION = "Gaming Laptop";
	private static final String PRODUCT_NAME = "Legion";
	private static final int PRODUCT_ID = 1;

	private Product buildProduct() {
		Product product = new Product();
		product.setId(PRODUCT_ID);
		product.setName(PRODUCT_NAME);
		product.setDescription(PRODUCT_DESCRIPTION);
		product.setPrice(PRODUCT_PRICE);
		return product;
	}

	@Test
	public void testFindAll() {
		Product product = buildProduct();
		List<Product> products = Arrays.asList(product);
		when(repository.findAll()).thenReturn(products);
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		mockMvc.perform(get(PRODUCT_URL).contextPath(CONTEXT_URL)).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(products)));
	}
	
	@Test
	public void testCreateProduct() throws JsonProcessingException, Exception {
		Product product = buildProduct();
		when(repository.save(any())).thenReturn(product);
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		mockMvc.perform(post(PRODUCT_URL).contextPath(CONTEXT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(product))).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(product)));
	}
	
	@Test
	public void testUpdateProduct() throws JsonProcessingException, Exception {
		Product product = buildProduct();
		product.setPrice(100);
		when(repository.save(any())).thenReturn(product);
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		mockMvc.perform(put(PRODUCT_URL).contextPath(CONTEXT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(product))).andExpect(status().isOk())
				.andExpect(content().json(objectWriter.writeValueAsString(product)));
	}
	
	@Test
	public void testDeleteProduct() {
		doNothing().when(repository.deleteById(PRODUCT_ID));
		mockMvc.perform(delete(PRODUCT_URL + PRODUCT_ID).contextPath(CONTEXT_URL)).andExpect(status().isOk());
	}
}
