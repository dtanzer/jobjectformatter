# jObjectFormatter - A toString Builder for Java

jObjectFormatter is a library for implementing toString at runtime. It is easy to use, yet fully configurable and very
flexible. You can decide which fields to include in toString using Java annotations - And you can configure a different
behavior for transitive objects.

Stay in touch and get news about jObjectFormatter: Follow [David Tanzer (@dtanzer)](https://twitter.com/dtanzer) on Twitter.

* [Getting Started](#wiki-Getting Started)

## <a name="Getting Started"> Getting Started

You really only need to do two things to get started: Add jObjectFormatter to your project, and then implement your 
```toString()``` methods like this:

    @Override
    public void toString() {
        return ObjectFormatter.format(this);
    }

### Add jObjectFormatter to your project

**Gradle**
    compile 'net.davidtanzer:jobjectformatter:0.1.0'

**Maven**
    <dependency>
      <groupId>net.davidtanzer</groupId>
      <artifactId>jobjectformatter</artifactId>
      <version>0.1.0</version>
    </dependency>
