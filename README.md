# Java-Table
2D-table interface and implementation

---
`Table` -- common interface to represent, access and manipulate mutable 2D table, mainly by entire rows and columns. It includes all row/column CRUD operations. Row, column, cell iterators in direct and reverse order are supported, as well as streams and converters from/to standard Java collections.

---

`LinkedTable` -- table implementation based on 4-linked cells: 

- `addRow()`, `addColumn()`, `replaceRow()`, `replaceColumn()` cost *O(k)* or *O(m)*, where *k* and *m* are table dimensions
- `insertRow()`, `insertColumn()`, `removeRow()`, `removeColumn()` cost *O(k + m)*
- `hasNext()`, `next()` cost *O(1)* for all direct and reversed iterators
- Row/column access by index costs *O(1)*, by key--at most *O(k)* or *O(m)*
- Cell random access by index costs *O(min(k, m))*, by key--at most *O(k + m)*

---

**All implementations are in progress.** Current source code is available on [branch devel]( https://github.com/Salauyou/Java-Table/tree/devel).
