package com.demiglace.springboot.reactive;

import java.util.function.Consumer;

import com.demiglace.springboot.reactive.vaccine.Vaccine;

public class VaccineConsumer implements Consumer<Vaccine> {
	@Override
	public void accept(Vaccine vaccine) {
		System.out.println(vaccine.getName());
		System.out.println(vaccine.isDelivered());
	}
}
