# jObjectFormatter - A toString Builder for Java

![Build Status](https://travis-ci.org/dtanzer/jobjectformatter.svg?branch=master)

jObjectFormatter is a library for implementing toString at runtime. It is easy to use, yet fully configurable and very
flexible. You can decide which fields to include in toString using Java annotations - And you can configure a different
behavior for transitive objects.

Stay in touch and get news about jObjectFormatter: Follow [David Tanzer (@dtanzer)](https://twitter.com/dtanzer) on Twitter.  
If you tweet about jObjectFormatter, please use the hash tag [#jObjectFormatter](https://twitter.com/search?f=tweets&q=%23jObjectFormatter).

* [Getting Started](#GettingStarted)
* [Why jObjectFormatter](#WhyJObjectFormatter)
* [How It Works](#HowItWorks)
* [Configuring jObjectFormatter](#ConfiguringJObjectFormatter)
* [Formatters](#Formatters)
* [Formatting Options (Annotations)](#FormattingOptions)
* [Transitive Objects](#TransitiveObjects)
* [Formatting in toString vs. Where You Need The Value](#FormattingWhereYouNeed)
* [Using jObjectFormatter with Guice / Spring](#GuiceSpring)
* [Support](#Support)
* [Contributing](#Contributing)
* [License](#License)

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

    compile 'net.davidtanzer:jobjectformatter:0.2.0'

**Maven**

    <dependency>
      <groupId>net.davidtanzer</groupId>
      <artifactId>jobjectformatter</artifactId>
      <version>0.2.0</version>
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

## <a name="WhyJObjectFormatter"> Why jObjectFormatter

In mid-2015, a customer of mine wanted to make sure that all toString methods in a large legacy code base produce their
output in the same format. So I evaluated ToStringBuilder from [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/) and
ToStringHelper from [Guava](https://github.com/google/guava), but the team did not like any of them.

We shortly discussed whether they should roll their own reflection-based toString builder. There we already discussed some
requirements for features that you can now find in jObjectFormatter. The team decided against rolling their own toString
builder, and used their IDEs to re-create all toString methods (I think that was a good decision back then).

Later that year I had to create a toString method again, in another project. I thought about creating it with my IDE, but
that has some drawbacks (like, what if I want to change the formatting of the toString methods later?). So I remembered 
our earlier discussion. So I tried to implement what we discussed back then (but with some changed requirements), and so 
jObjectFormatter was born.

## <a name="HowItWorks"> How It Works

When asked to format an object, jObjectFormatter does it's work in three phases:

1. It gathers all necessary information about the class of the object. This information is cached,
   so jObjectFormatter does not have to compute it again when formatting another object of the same class.
2. It gathers the actual values of all properties of the object.
3. It creates a string from the gathered property values using an ```ObjectStringFormatter```.

You can configure how phases 1 and 2 do their work with annotations on the objects you want to format. That means, you
can configure which property values will be printed and how transitive objects are processed using annotations on your
classes and fields. See [Formatting Options (Annotations)](#FormattingOptions) and [Transitive Objects](#TransitiveObjects) 
for more details.

In the third phase, jObjectFormatter uses an ```ObjectStringFormatter``` to actually format the values it gathered in
phase 2. See [Configuring jObjectFormatter](#ConfiguringJObjectFormatter) for more details about how to configure a
formatter and [Formatters](#Formatters) for details about available formatters and how to write your own formatter.

## <a name="ConfiguringJObjectFormatter"> Configuring jObjectFormatter

The central class in jObjectFormatter is ```FormattedStringGenerator```. You somehow need access to an object of that class
to format your objects. You can either configure a global instance in the helper class ```ObjectFormatter```, or you can
create an instance yourself and pass it to the objects that need that instance (see [Using jObjectFormatter with Guice / Spring](#GuiceSpring) for
a discussion about that).

```ObjectFormatter``` has a default instance of ```FormattedStringGenerator```. So if you only call ```ObjectFormatter.format(...)```
without configuring anything, ```ObjectFormatter``` will use that instance to format your object. You can configure another
```FormattedStringGenerator``` by simply calling the static ```configureGenerator(...)``` method when your application starts
up:

    ObjectFormatter.configureGenerator(myFormattedStringGenerator);

You can create a ```FormattedStringGenerator``` by simply calling ```new```, but you have to supply a formatter:

    FormattedStringGenerator myFormattedStringGenerator = new FormattedStringGenerator(
        ConfigurableObjectStringFormatter.GROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME
    );

## <a name="Formatters"> Formatters

jObjectFormatter comes with several built in formatters:

* ```SimpleFormatter```: The default formatter when you configure nothing else. It has no configuration options. Example:  
  ```{ firstName=Jane, lastName=Doe, address={ street=Evergreen Terrace }, identification=jdoe, roles=[{ name=admin }] }```
* ```JsonObjectStringFormatter```: A formatter that tries to output something that is as JSON-like as possible. You can configure
  it to either display the property values in groups or all together and when to add the class name to the property values.
  Example:  
  ```{"Person": {"firstName": "Jane", "lastName": "Doe", "address": "{"Address": {"street": "Evergreen Terrace"}}"}, "Principal": {"identification": "jdoe", "roles": "[{"Role": {"name": "admin"}}]"}}```
* ```ConfigurableObjectStringFormatter```: A formatter that is totally configurable. You don't have to create objects of
  this class directly, instead you can use one of the predefined formatters:
  * ```ConfigurableObjectStringFormatter.GROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME``` simple format that uses curly braces
    to group properties, and groups properties by their declaring class. Example:  
    ```Person{firstName="Jane", lastName="Doe", address="Address{street="Evergreen Terrace"}}"}, Principal{identification="jdoe", roles="[Role{name="admin"}}]"}}```
  * ```ConfigurableObjectStringFormatter.UNGROUPED_CURLY_BRACED_OUTPUT_WITH_CLASS_NAME``` similar to the format above, but
    it does not group the properties by their declaring class. Example:  
    ```Person{firstName="Jane", lastName="Doe", address="Address{street="Evergreen Terrace"}", identification="jdoe", roles="[Role{name="admin"}]"}```

You can also write your own formatter. You just have to implement the interface ```net.davidtanzer.jobjectformatter.formatter.ObjectStringFormatter```,
but the easier way to create your formatter is to extend the abstract class ```net.davidtanzer.jobjectformatter.formatter.AbstractObjectStringFormatter```.

## <a name="FormattingOptions"> Formatting Options (Annotations)

TBD

## <a name="TransitiveObjects"> Transitive Objects

TBD

## <a name="FormattingWhereYouNeed">Formatting in toString vs. Where You Need The Value

TBD

## <a name="GuiceSpring"> Using jObjectFormatter with Guice / Spring

TBD

## <a name="Support"> Support

TBD

## <a name="Contributing"> Contributing

TBD

## <a name="License"> License

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
