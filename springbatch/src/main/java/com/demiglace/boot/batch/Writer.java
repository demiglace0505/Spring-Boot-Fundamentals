package com.demiglace.boot.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class Writer implements ItemWriter<String> {
	@Override
	public void write(List<? extends String> items) throws Exception {
		System.out.println("INSIDE WRITE");
		System.out.println("Writing Data: " + items);
	}
}
