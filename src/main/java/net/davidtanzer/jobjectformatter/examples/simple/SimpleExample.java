package net.davidtanzer.jobjectformatter.examples.simple;

import net.davidtanzer.jobjectformatter.ObjectFormatter;

public class SimpleExample {
	public static void main(String[] args) {
		Address address = new Address("Evergreen Terrace", "12b");
		Person person = new Person("Jane", "Doe", address);

		System.out.println("person.toString() -> " + person);
		System.out.println("address.toString() -> " + address);
	}

	private static class Person {
		private String firstName;
		private String lastName;
		private Address address;

		public Person(final String firstName, final String lastName, final Address address) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.address = address;
		}

		@Override
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}

	private static class Address {
		private String street;
		private String streetNo;

		public Address(final String street, final String streetNo) {
			this.street = street;
			this.streetNo = streetNo;
		}

		@Override
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}
}
