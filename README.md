# ProvideJ

This is a type provider for Java that automatically and at compile time creates
Java pojos that store the schema and data of specified JSON files, and allows
client code to interact with them in an easy and type safe way.

ProvideJ is implemented using annotation processing.

## Structure

The repo contains two modules: annotation-client which has an example host
application, that uses ProvideJ to analyze some JSON data, and
annotation-processor which contains the implementation of ProvideJ including its
host-code-facing annotations and javac-facing annotation processors.

## IDE integration

ProvideJ should be intergatable with any Java IDEs that support annotation
processing. It can also be integrated into applications built with maven,
gradle, and bazel.

Lookup how to integrate annotation processing in your build system and IDE and
follow the instructions. There is no need to have any custom configuration
beyond that.

I tested the integration for Intellij IDEA. All you have to do is
1. Add the annotation-processor.jar file to the classpath as a dependency in the
   **Dependencies** tab in *Project structure -> Modules*.
2. Enable annotation processing in *Settings -> Build, Execution, Deployment ->*
   *Compiler -> Annotation Processors*.
3. In the same window as (2), make sure to configure a directory to store
   generated sources. In my configuration, I used directory "apt" at
   "Module content root".
4. Whatever directory you choose in step (3), make sure to mark that directory
   a source directoy in the **Sources** tab in *Project Structure -> Modules*.
   Otherwise, your IDE will not treat the generated files as Java source files
   that are compiled with your source.

## Dependencies

1. Java 11: in particular, I am using openjdk 11.0.10 on Ubuntu 20.04 64 bit.
