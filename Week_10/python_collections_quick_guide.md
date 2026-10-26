# Python `collections` Module -- Quick Guide

The `collections` module in Python provides specialized container
datatypes that extend the functionality of the built‑in containers like
`list`, `dict`, `set`, and `tuple`.

This guide focuses on three commonly used tools:

-   `OrderedDict`
-   `namedtuple`
-   `Counter`

---------------------------

## 1. OrderedDict

`OrderedDict` is a dictionary subclass that **remembers the order** in
which items were inserted.

> Note: Since Python 3.7, normal dictionaries also preserve insertion
> order, but `OrderedDict` still provides extra ordering utilities.

### Import

``` python
from collections import OrderedDict
```

### Basic Example

``` python
od = OrderedDict()
od["a"] = 1
od["b"] = 2
od["c"] = 3

print(od)
# OrderedDict([('a', 1), ('b', 2), ('c', 3)])
```

### Useful Methods

#### `move_to_end(key, last=True)`

Move an existing key to the end or beginning.

``` python
od.move_to_end("a")          # moves 'a' to end
od.move_to_end("b", last=False)  # moves 'b' to start
```

#### `popitem(last=True)`

Removes and returns an item.

``` python
od.popitem()       # removes last
od.popitem(False)  # removes first
```

### When to Use

-   LRU caches - A Least Recently Used cache is a data structure that stores a limited number of items and automatically removes the item that hasn’t been used in the longest time when it runs out of space. (long running API requests, data operations, or heavy computations)
-   Order‑sensitive algorithms
-   Explicit reordering needs

------------------------------------------

## 2. namedtuple

`namedtuple` creates **tuple subclasses with named fields**, making code
more readable and self‑documenting.

### Import

``` python
from collections import namedtuple
```

### Basic Example

``` python
Point = namedtuple("Point", ["x", "y"])
p = Point(10, 20)

print(p.x)  # 10
print(p.y)  # 20
```

### Features

-   Immutable (like tuples)
-   Index access still works

``` python
p[0]  # 10
```

### Utility Methods

#### `_asdict()`

``` python
p._asdict()
# {'x': 10, 'y': 20}
```

#### `_replace()`

``` python
p2 = p._replace(x=50)
```

--------------------------

## 3. Counter

`Counter` is a dictionary subclass for **counting objects**.

### Import

``` python
from collections import Counter
```

### Basic Example

``` python
from collections import Counter

c = Counter("banana")
print(c)
# Counter({'a': 3, 'n': 2, 'b': 1})
```

### Common Operations

#### Most Common

``` python
c.most_common(2)
# 'Give me the top 2 most common elements, leave blank for all'
# [('a', 3), ('n', 2)]
```

#### Update Counts

``` python
# Adds to an existing counter
# bannana + band = Counter({'a': 4, 'n': 3, 'b': 2, 'd': 1})
c.update("band")
```

#### Subtract

``` python
# Subtract from an existing counter
# Counter({'a': 4, 'n': 3, 'b': 2, 'd': 1} - 'ana' = Counter({'a': 2, 'n': 2, 'b': 2, 'd': 1}
c.subtract("ana")
```

#### Arithmetic

``` python
c1 + c2
c1 - c2
c1 & c2  # intersection (min counts)
c1 | c2  # union (max counts)
```

### When to Use

-   Frequency analysis
-   Vote counting
-   Inventory tracking
-   Histogram‑like data

----------------------------------------------