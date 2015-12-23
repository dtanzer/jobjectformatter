# jObjectFormatter - A toString Builder for Java

jObjectFormatter is a library for implementing toString at runtime. It is easy to use, yet fully configurable and very
flexible. You can decide which fields to include in toString using Java annotations - And you can configure a different
behavior for transitive objects.

Stay in touch and get news about jObjectFormatter: Follow [David Tanzer (@dtanzer)](https://twitter.com/dtanzer) on Twitter.

* [Getting Started](#GettingStarted)

## <a name="GettingStarted"> Getting Started

You really only need to do two things to get started: Add jObjectFormatter to your project, and then implement your 
```toString()``` methods like this:

    @Override
    public void toString() {
        return ObjectFormatter.format(this);
    }

### Add jObjectFormatter to Your Project

jObjectFormatter is available on Maven Central. So if you are using a build tool that supports maven dependencies and
if you have already configured the central repository correctly, you only have to add the dependency to your project:

**Gradle**

    compile 'net.davidtanzer:jobjectformatter:0.1.0'

**Maven**

    <dependency>
      <groupId>net.davidtanzer</groupId>
      <artifactId>jobjectformatter</artifactId>
      <version>0.1.0</version>
    </dependency>

### Implement toString to Use jObjectFormatter

Say you have a class ```Person``` and a class ```Address```, where a person has an address. You just have to implement
toString to use jObjectFormatter, which will take care of the rest.

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

In it's default configuration, jObjectFormatter will use a very simple toString style. Let's try to print a person and
an address:

    Address address = new Address("Evergreen Terrace", "12b");
    Person person = new Person("Jane", "Doe", address);

The output of your toString from above will look like this:

    person.toString() -> { firstName=Jane, lastName=Doe, address=[not null] }
    address.toString() -> { street=Evergreen Terrace, streetNo=12b }

As you can see, jObjectFormatter does not transitively print objects in it's default configuration. Also, the string
formatter from the default configuration does not print the class name or group values.
