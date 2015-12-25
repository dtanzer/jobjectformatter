/*
   Copyright 2015 David Tanzer (business@davidtanzer.net / @dtanzer)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
