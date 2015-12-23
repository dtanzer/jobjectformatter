package net.davidtanzer.jobjectformatter.examples.annotated;

import net.davidtanzer.jobjectformatter.ObjectFormatter;
import net.davidtanzer.jobjectformatter.annotations.*;

public class AnnotationsExample {
	public static void main(String[] args) {
		Address address = new Address("Evergreen Terrace", "12b");
		Person person = new Person("Jane", "Doe", address);
		address.setOwner(person);

		System.out.println("Person: " + person);
		System.out.println("Address: " + address);
	}

	private static class Person {
		private String firstName;
		@FormattedField(transitive = FormattedFieldType.DEFAULT)
		private String lastName;
		private Address address;

		public Person(final String firstName, final String lastName, final Address address) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.address = address;
		}

		@Override
		@Formatted(value = FormattedType.ALL, transitive = Transitive.DISALLOWED)
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}

	private static class Address {
		@FormattedField(transitive = FormattedFieldType.DEFAULT)
		private String street;
		private String streetNo;
		@Formatted(transitive = Transitive.ALLOWED)
		private Person owner;

		public Address(final String street, final String streetNo) {
			this.street = street;
			this.streetNo = streetNo;
		}

		public void setOwner(final Person owner) {
			this.owner = owner;
		}

		@Override
		@Formatted(value=FormattedType.ALL, transitive = Transitive.ALLOWED)
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}
}
