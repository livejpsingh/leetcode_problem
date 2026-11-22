# Question 273

## Question

What is the difference between Class.forName() and ClassLoader.loadClass()?

## Answer

The ClassLoader locates and loads class definitions into the JVM, creates Class objects, and participates in delegation and linking. Bootstrap, platform, and application class loaders cover the common Java layers.

## Related section

[JVM Internals & Memory Management](./README.md)
