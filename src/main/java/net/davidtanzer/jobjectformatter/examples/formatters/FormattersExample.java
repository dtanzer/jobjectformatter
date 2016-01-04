package net.davidtanzer.jobjectformatter.examples.formatters;

import net.davidtanzer.jobjectformatter.FormattedStringGenerator;
import net.davidtanzer.jobjectformatter.ObjectFormatter;
import net.davidtanzer.jobjectformatter.annotations.*;
import net.davidtanzer.jobjectformatter.formatter.*;

import java.util.Arrays;
import java.util.List;

public class FormattersExample {
	public static void main(String[] args) {
		Role role = new Role("admin");
		Address address = new Address("Evergreen Terrace", "12b");
		Person person = new Person("Jane", "Doe", address, "jdoe", Arrays.asList(role));
		address.setOwner(person);

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new SimpleFormatter()));
		System.out.println("SimpleFormatter: "+ObjectFormatter.format(person));

		System.out.println();

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(ConfigurableObjectStringFormatter.GROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME));
		System.out.println("ConfigurableObjectStringFormatter.GROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME: "+ObjectFormatter.format(person));

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(ConfigurableObjectStringFormatter.UNGROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME));
		System.out.println("ConfigurableObjectStringFormatter.UNGROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME: "+ObjectFormatter.format(person));

		System.out.println();

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new JsonObjectStringFormatter(FormatGrouped.NO, DisplayClassName.NEVER)));
		System.out.println("JsonObjectStringFormatter(FormatGrouped.NO, DisplayClassName.NEVER): "+ObjectFormatter.format(person));

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new JsonObjectStringFormatter(FormatGrouped.BY_CLASS, DisplayClassName.NEVER)));
		System.out.println("JsonObjectStringFormatter(FormatGrouped.BY_CLASS, DisplayClassName.NEVER): "+ObjectFormatter.format(person));

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new JsonObjectStringFormatter(FormatGrouped.NO, DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS)));
		System.out.println("JsonObjectStringFormatter(FormatGrouped.NO, DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS): "+ObjectFormatter.format(person));

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new JsonObjectStringFormatter(FormatGrouped.BY_CLASS, DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS)));
		System.out.println("JsonObjectStringFormatter(FormatGrouped.BY_CLASS, DisplayClassName.WHEN_NOT_GROUPED_BY_CLASS): "+ObjectFormatter.format(person));

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new JsonObjectStringFormatter(FormatGrouped.NO, DisplayClassName.ALWAYS)));
		System.out.println("JsonObjectStringFormatter(FormatGrouped.NO, DisplayClassName.ALWAYS): "+ObjectFormatter.format(person));

		ObjectFormatter.configureGenerator(new FormattedStringGenerator(new JsonObjectStringFormatter(FormatGrouped.BY_CLASS, DisplayClassName.ALWAYS)));
		System.out.println("JsonObjectStringFormatter(FormatGrouped.BY_CLASS, DisplayClassName.ALWAYS): "+ObjectFormatter.format(person));
	}

	private static class Role {
		private String name;

		public Role(final String name) {
			this.name = name;
		}

		@Override
		@Formatted(value = FormattedInclude.ALL_FIELDS, transitive = TransitiveInclude.NO_FIELDS)
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}

	private static class Principal {
		private String identification;
		private List<Role> roles;

		public Principal(final String identification, final List<Role> roles) {
			this.identification = identification;
			this.roles = roles;
		}
	}

	private static class Person extends Principal {
		private String firstName;
		@FormattedField(transitive = FormattedFieldType.DEFAULT)
		private String lastName;
		private Address address;

		public Person(final String firstName, final String lastName, final Address address, final String identification, final List<Role> roles) {
			super(identification, roles);

			this.firstName = firstName;
			this.lastName = lastName;
			this.address = address;
		}

		@Override
		@Formatted(value = FormattedInclude.ALL_FIELDS, transitive = TransitiveInclude.NO_FIELDS)
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}

	private static class Address {
		@FormattedField(transitive = FormattedFieldType.DEFAULT)
		private String street;
		private String streetNo;
		@Formatted(transitive = TransitiveInclude.ANNOTADED_FIELDS)
		private Person owner;

		public Address(final String street, final String streetNo) {
			this.street = street;
			this.streetNo = streetNo;
		}

		public void setOwner(final Person owner) {
			this.owner = owner;
		}

		@Override
		@Formatted(value= FormattedInclude.ALL_FIELDS, transitive = TransitiveInclude.ANNOTADED_FIELDS)
		public String toString() {
			return ObjectFormatter.format(this);
		}
	}
}
