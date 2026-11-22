# Question 368

## Question

What is the difference between HashMap resize and rehash?

## Answer

HashMap stores key/value pairs in buckets by key hash. It allows one null key and multiple null values, does not preserve insertion order, and is not thread-safe. Average operations are normally O(1).

## Related section

[Collections Framework Advanced](./README.md)
