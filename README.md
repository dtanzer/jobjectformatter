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
* [Transitive Objects](#TransitiveObjects)
* [Formatting Options (Annotations)](#FormattingOptions)
* [Design Considerations](#DesignConsiderations)
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

    compile 'net.davidtanzer:jobjectformatter:0.2.1'

**Maven**

    <dependency>
      <groupId>net.davidtanzer</groupId>
      <artifactId>jobjectformatter</artifactId>
      <version>0.2.1</version>
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

## <a name="TransitiveObjects"> Transitive Objects

In it's default configuration, jObjectFormatter does not format transitive objects. So, if you have a class "Person" that
has a field "Address", and you format a person object, the Address will not be formatted:

    private static class Person {
        private String firstName;
        private String lastName;
        private Address address;

        ...
    }

    private static class Address {
        private String street;
        private String streetNo;

        ...
    }

The output will be like this:

    person.toString() -> { firstName=Jane, lastName=Doe, address=[not null] }
    address.toString() -> { street=Evergreen Terrace, streetNo=12b }

The idea is to keep the formatted output as minimal as possible, and also to avoid cyclic dependencies by default. But you 
can change this behavior using annotations ([see below](#FormattingOptions)). 

When you configure the formatter to also format transitive objects, make sure to avoid formatting objects with cyclic dependencies.
Say you have a ```Person``` that has an ```Address```, and the address has an owner which is again a ```Person```, formatting
everything transitively will result in a stack overflow exception.

## <a name="FormattingOptions"> Formatting Options (Annotations)

You can configure the behavior of jObjectFormatter for every class you want to format using annotations. And you can override
that configuration in other classes that reference a class with an annotation. And you can configure the behavior of the
formatter when processing fields.

### Configuring the Behavior for a Class

You can annotate a class (or it's ```toString``` method) with ```@Formatted``` (If you annotate both, the annotation on the
class overrides the annotation on ```toString```). With this annotation, you can configure how jObjectFormatter will treat 
objects of the class.

    private static class Person {
        private String firstName;
        private String lastName;
        private Address address;

        @Override
        @Formatted
        public String toString() {
            return ObjectFormatter.format(this);
        }
    }

You can configure how objects of the class are formatted when calling ```format``` on them directly with the ```value```
of the annotation:

* ```FormattedInclude.ALL_FIELDS``` means that all property values of the object will be added to the formatted string. Example:  
  ```{ firstName=Jane, lastName=Doe, address={ street=Evergreen Terrace } }```
* ```FormattedInclude.ANNOTATED_FIELDS``` means that only property values of properties with a special annotation will be
  added to the formatted string. Example:  
  ```{ lastName=Doe }```
* ```FormattedInclude.NO_FIELDS``` means that no property values will be added to the formatted string. Example:  
  ```{  }```

You can also configure which properties to include when the object is referenced transitively through another object. In
the example above, when you call ```toString()``` on an instance of the class ```Person```, the person's ```address``` is
a transitive object. You can configure the transitive behavior with the ```transitive``` value of the annotation:

* ```TransitiveInclude.ALL_FIELDS``` means that all property values of the object will be added to the formatted string.
* ```TransitiveInclude.ANNOTATED_FIELDS``` means that only property values of properties with a special annotation will be
  added to the formatted string.
* ```TransitiveInclude.NO_FIELDS``` means that no property values will be added to the formatted string.

For more details about configuring transitivity, see [Transitive Objects](#TransitiveObjects) below. Here is the example
from above, with both configurations set:

    private static class Person {
        private String firstName;
        private String lastName;
        private Address address;

        @Override
        @Formatted(value = FormattedInclude.ALL_FIELDS, transitive = TransitiveInclude.NO_FIELDS)
        public String toString() {
            return ObjectFormatter.format(this);
        }
    }

### Overriding the Annotation on the Class

When one of your classes references an object from another class, and you want to override the configured behavior of that
class in your current situation, you can just annotate the field with ```@Formatted```. Say you have a class "Address" that
has a reference back to a Person. But when formatting the address, you want to change the transitive configuration from
```TransitiveInclude.NO_FIELDS``` (which is configured on the person class) to ```TransitiveInclude.ANNOTADED_FIELDS```.
Just add the ```@Formatted``` annotation to the ```owner``` field of ```Address```:

    private static class Address {
        private String street;
        private String streetNo;
        @Formatted(transitive = TransitiveInclude.ANNOTADED_FIELDS)
        private Person owner;

        @Override
        @Formatted
        public String toString() {
            return ObjectFormatter.format(this);
        }
    }

When you now call ```toString``` on an instance of ```Address```, the ```owner``` will be formatted with the transitive
configuration ```TransitiveInclude.ANNOTADED_FIELDS```. When other classes reference a ```Person```, that ```Person``` object
will be formatted with it's default transitive configuration (```TransitiveInclude.NO_FIELDS```).

### Configure Formatting of Fields

You can add the ```@FormattedField``` annotation on fields to configure how those fields are formatted when the object is
configured as ```FormattedInclude.ANNOTATED_FIELDS``` or ```TransitiveInclude.ANNOTADED_FIELDS```. With the ```@FormattedField```
annotation, you can configure how the field is formatted when formatting the object directly ("```value```") and how the
field is formatted when the object is formatted transitively ("```transitive```"). Both configurations can have three values:

* ```FormattedFieldType.DEFAULT```: Include the field in the formatted output. This is the default value for directly formatting objects.
* ```FormattedFieldType.VERBOSE```: Include the field in the formatted output, but only when the output is set to "verbose" (
  Note: Verbose output is not yet supported).
* ```FormattedFieldType.NEVER```: Do not include the field in the formatted output. This is the default value for transitively
  formatting objects.

For example, formatting objects of these two classes:

    private static class Person {
        private String firstName;
        @FormattedField(transitive = FormattedFieldType.DEFAULT)
        private String lastName;
        private Address address;

        ...
        
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

        ...
        
        @Override
        @Formatted(value= FormattedInclude.ALL_FIELDS, transitive = TransitiveInclude.ANNOTADED_FIELDS)
        public String toString() {
            return ObjectFormatter.format(this);
        }
    }

Will produce an output like this:

    Person: { firstName=Jane, lastName=Doe, address={ street=Evergreen Terrace } }
    Address: { street=Evergreen Terrace, streetNo=12b, owner={ lastName=Doe } }

## <a name="DesignConsiderations">Design Considerations

Using jObjectFormatter is really simple: Just add a line of code to your toString. Anyway, here are some things you might
want to consider:

### Formatting in toString vs. Where You Need The Value

Is it really a good idea to rely on every object to have a nice, usable ```toString``` method? I am not entirely sure.
Maybe instead of writing

    log.info("User logged in: {}", user);

it would be better to write:

    log.info("User logged in: {}", format(user));

Then you have complete control over when and how objects should be formatted.

### Using jObjectFormatter with Guice / Spring

I think in most real-world projects, I would not like the fact that there is the global ```ObjectFormatter``` that is used
everywhere to format objects. So maybe it would be better to configure a singleton instance of ```FormattedStringGenerator```
in a dependency injection ("DI") container like Guice or Spring and use that instance to format objects.

This will not work very well when you want to implement your toString methods with jObjectFormatter: You will have some
objects (Entities, DTOs, ...) where you want to override toString, but which you cannot create using Guice or Spring.

If you, OTOH, format your objects where you need the formatted value (as described above), that approach might work very
well: You will probably create all the Services, Controllers and Components that want to log something with your DI container.
So you will have access to the ```FormattedStringGenerator``` instance in every class that logs something.

## <a name="Support"> Support

* Stay in touch and get news about jObjectFormatter: Follow [David Tanzer (@dtanzer)](https://twitter.com/dtanzer) on Twitter.  
* If you tweet about jObjectFormatter, please use the hash tag [#jObjectFormatter](https://twitter.com/search?f=tweets&q=%23jObjectFormatter).
* If you have found a defect or want to request a new feature, please open an issue here at GitHub.
* Ask technical questions ("How can I ...?") at StackOverflow. If I don't answer them within a few days, please nudge me on Twitter.
* Ask open-ended questions through my ["Ask me anything"](http://davidtanzer.net/contact) form.

## <a name="Contributing"> Contributing

I'm always happy to receive pull requests with new features, bug fixes or improvements to the code. If you send a pull 
request please consider the following:

* Please write unit tests.
* Make sure all tests pass - Run ```./gradlew check``` to make sure the build passes before submitting the pull request.
* Indent your code with tabs, not spaces.
* Don't mix tabs and spaces - Tabs come at the beginning of the line, before the first non-whitespace character. After the
  first non-whitespace character, tabs are not allowed anymore.
* If you want to implement a new feature, let's discuss it first. Just add an issue to the issue tracker here on github. In
  the issue, describe the feature you want and also indicate that you would want to implement it yourself.
* Please don't add any dependencies to the build without discussing it first. Just create an issue for the discussion.

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
