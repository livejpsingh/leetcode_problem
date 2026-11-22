# Question 231

## Question

What is the difference between Optional.of() and Optional.ofNullable()?

## Answer

Optional represents an optional return value and reduces ambiguous null handling. Use map/flatMap and orElse/orElseGet; do not use it for fields or parameters.

## Related section

[Java 8 Basics](./README.md)
