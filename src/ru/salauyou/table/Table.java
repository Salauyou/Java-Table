package ru.salauyou.table;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Interface to represent 2D table and perform operations on it
 * 
 * @author salauyou
 *
 * @param <R> row key type
 * @param <C> column key type
 * @param <V> cell value type
 */
public interface Table<R, C, V> {
	
	/**
	 * Number of rows in table
	 */
	public int rowNumber();

	
	/**
	 * Number of columns in table
	 */
	public int columnNumber();

	
	
	/* --------------------- row accessors and updaters -------------------- */
	
	
	/**
	 * Returns mutable row by index
	 * @throws NoSuchElementException if <tt>index < 0 || index >= this.rowNumber()</tt>
	 */
	public Line<R, V> getRow(int index) throws NoSuchElementException;
	
	
	/**
	 * Returns mutable row by key
	 * @throws NoSuchElementException  if row with given key doesn't exist
	 */
	public Line<R, V> getRow(R key) throws NoSuchElementException;
	
	
	/**
	 * Appends a new row to the end.
	 * If provided collection is null or empty, row with null values is added
	 * 
	 * @throws WrongSizeException	if size of passed row doesn't fit table
	 */
	public Table<R, C, V> addRow(Collection<? extends V> row) 
										throws	WrongSizeException;

	
	/**
	 * Appends a new row with a key to the end.
	 * If provided collection is null or empty, row with null values is added
	 * 
	 * @throws IllegalArgumentException if row with given key already exists
	 * @throws WrongSizeException	    if size of passed row doesn't fit table
	 */
	public Table<R, C, V> addRow(R key, Collection<? extends V> row)
										throws	IllegalArgumentException,
												WrongSizeException;
	
	
	/**
	 * Appends a row with provided key to the end.
	 * If provided row is null or empty, row with null values is added
	 * 
	 * @throws IllegalArgumentException if row with given key already exists
	 * @throws WrongSizeException       if size of row doesn't fit table
	 */
	public default Table<R, C, V> addRow(R key, Line<? extends R, ? extends V> row)
										throws IllegalArgumentException,
										       WrongSizeException {
		addRow(key, row.toList());
		return this;
	}
	
	
	/**
	 * Inserts a new row before row with given index.
	 * If <tt>index >= this.rowNumber()</tt>, new row is appended to the end.
	 * If provided collection is null or empty, row with null values is added
	 * 
	 * @throws NoSuchElementException	if <tt>index < 0</tt>
	 * @throws WrongSizeException	    if size of passed row doesn't fit table
	 */
	public Table<R, C, V> insertRow(int index, Collection<? extends V> row) 
			                        	throws	NoSuchElementException,
			                        			WrongSizeException;
	
	
	/**
	 * Inserts a new row with key before row with given index.
	 * If <tt>index >= this.rowNumber()</tt>, new row is appended to the end.
	 * If provided collection is null or empty, row with null values is added
	 * 
	 * @throws IllegalArgumentException	if row with provided key already exists
	 * @throws NoSuchElementException	if <tt>index < 0</tt>
	 * @throws WrongSizeException		if size of passed row doesn't fit table
	 */
	public Table<R, C, V> insertRow(int index, R key, Collection<? extends V> row)
										throws	IllegalArgumentException, 
										   		NoSuchElementException, 
										   		WrongSizeException;

	
	/**
	 * Inserts a row with provided key before row with given index.
	 * If <tt>index >= this.rowNumber()</tt>, new row is appended to the end.
	 * If provided collection is null or empty, row with null values is added
	 * 
	 * @throws IllegalArgumentException	if row with provided key already exists
	 * @throws NoSuchElementException	if <tt>index < 0</tt>
	 * @throws WrongSizeException		if size of passed row doesn't fit table
	 */
	public default Table<R, C, V> insertRow(int index, R key, Line<? extends R, ? extends V> row)
										throws IllegalArgumentException,
										       NoSuchElementException,
										       WrongSizeException {
		insertRow(index, key, row.toList());
		return this;
	}

	
	/**
	 * Replaces a row at given index with new cell values, returns old values.
	 * 
	 * @throws NoSuchElementException	if row with given index doesn't exist
	 * @throws WrongSizeException	    if size of passed row doesn't fit table
	 */
	public default List<V> replaceRow(int index, Collection<? extends V> row) 
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsRow(index))
			throw new NoSuchElementException();
		Line<R, V> rw = getRow(index);
		if (row.size() != rw.size())
			throw new WrongSizeException(rw.size(), row.size());
		
		List<V> old = new ArrayList<>();
		Iterator<? extends V> i = row.iterator();
		for (Cell<V> c : rw.cells()) {
			old.add(c.get());
			c.set(i.next());
		}
		return old;
	}
	
	
	/**
	 * Replaces a row with given key with new cell values, returns old values
	 * 
	 * @throws NoSuchElementException	if row with given key doesn't exist
	 * @throws WrongSizeException	    if size of passed row doesn't fit table
	 */
	public default List<V> replaceRow(R key, Collection<? extends V> row)
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsRow(key))
			throw new NoSuchElementException();
		Line<R, V> rw = getRow(key);
		if (row.size() != rw.size())
			throw new WrongSizeException(rw.size(), row.size());
		
		List<V> old = new ArrayList<>();
		Iterator<? extends V> i = row.iterator();
		for (Cell<V> c : rw.cells()) {
			old.add(c.get());
			c.set(i.next());
		}
		return old;
	}
	
	
	/**
	 * Replaces cell values of row at given index with values copied from another row,
	 * returns old values
	 * 
	 * @throws NoSuchElementException	if column with given index doesn't exist
	 * @throws WrongSizeException	    if size of passed column doesn't fit table
	 */
	public default List<V> replaceRow(int index, Line<? extends R, ? extends V> row) 
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsRow(index))   
			throw new NoSuchElementException();
		Line<R, V> rw = getRow(index);
		if (row == rw)
			return rw.toList();
		if (row.size() != rw.size())
			throw new WrongSizeException(rw.size(), row.size());
		
		List<V> old = new ArrayList<>();
		Iterator<Cell<V>> ci = rw.cells().iterator();
		for (Cell<? extends V> c : row.cells()) {
			Cell<V> cell = ci.next();
			old.add(cell.get());
			cell.set(c.get());
		}
		return old;
	}

	
	/**
	 * Replaces cell values of a row with given key with cell values copied from another row,
	 * returns old values
	 * 
	 * @throws NoSuchElementException	if row with given index doesn't exist
	 * @throws WrongSizeException	    if size of passed row doesn't fit table
	 */
	public default List<V> replaceRow(R key, Line<? extends R, ? extends V> row) 
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsRow(key))   
			throw new NoSuchElementException();
		Line<R, V> rw = getRow(key);
		if (row == rw)
			return rw.toList();
		if (row.size() != rw.size())
			throw new WrongSizeException(rw.size(), row.size());
		
		List<V> old = new ArrayList<>();
		Iterator<Cell<V>> ci = rw.cells().iterator();
		for (Cell<? extends V> c : row.cells()) {
			Cell<V> cell = ci.next();
			old.add(cell.get());
			cell.set(c.get());
		}
		return old;
	}
	
	
	/**
	 * Removes a row at given index and returns its items
	 * @throws NoSuchElementException if <tt>index < 0 || index >= this.rowNumber()</tt>
	 */
	public List<V> removeRow(int index) throws NoSuchElementException;
	
	
	/**
	 * Removes a row by given key and returns its items
	 * @throws NoSuchElementException if key doesn't exist
	 */
	public List<V> removeRow(R key) throws NoSuchElementException;
	
	
	/**
	 * Removes the last row and returns its items
	 * @throws NoSuchElementException if table is empty
	 */
	public default List<V> removeLastRow() throws NoSuchElementException {
		return removeRow(rowNumber() - 1);
	}
	
	
	/**
	 * Returns if table contains row with given index
	 */
	public default boolean containsRow(int index) {
		return index >= 0 && index < rowNumber();
	}
	
	
	/**
	 * Returns if table contains row with given key
	 */
	public boolean containsRow(R key);
	
	
	/**
	 * Returns index for given row key, or -1 if such key doesn't exist
	 */
	public int getRowIndex(R key);
	
	
	/**
	 * Returns key for given row index or null if undefined
	 * @throws NoSuchElementException if <tt>index < 0 || index >= this.rowNumber()</tt>
	 */
	public default R getRowKey(int index) throws NoSuchElementException {
		if (!containsRow(index))
			throw new NoSuchElementException();
		return getRowKey(index);
	}
		
	
	/**
	 * Sets a new key to the row at given index, returns old key or null
	 * @throws NoSuchElementException	if <tt>index < 0 || index >= this.rowNumber()</tt>
	 * @throws IllegalArgumentException if given key is already associated with another row
	 */
	public R setRowKey(int index, R key) throws NoSuchElementException, 
	                                            IllegalArgumentException;
	
	
	/**
	 * Sets a new key to the row with given key
	 * @throws NoSuchElementException	if row with <tt>oldKey</tt> doesn't exist
	 */
	public void setRowKey(R oldKey, R newKey) throws NoSuchElementException;
	

	
	/* --------------------- column accessors and updaters -------------------- */
	
	
	/**
	 * Returns mutable column by index
	 * @throws NoSuchElementException if <tt>index < 0 || index >= this.columnNumber()</tt>
	 */
	public Line<C, V> getColumn(int index) throws NoSuchElementException;
	
	
	/**
	 * Returns mutable column by key 
	 * @throws NoSuchElementException if column with given index doesn't exist
	 */
	public Line<C, V> getColumn(C key) throws NoSuchElementException;
	
	
	/**
	 * Appends a new column to the end.
	 * If provided collection is null or empty, column with null values is added
	 * 
	 * @throws WrongSizeException	if size of passed column doesn't fit table
	 */
	public Table<R, C, V> addColumn(Collection<? extends V> column) 
										throws	WrongSizeException;

	
	/**
	 * Appends a new column with a key to the end.
	 * If provided collection is null or empty, column with null values is added
	 * 
	 * @throws IllegalArgumentException if column with given key already exists
	 * @throws WrongSizeException	    if size of passed column doesn't fit table
	 */
	public Table<R, C, V> addColumn(C key, Collection<? extends V> column)
										throws	IllegalArgumentException,
												WrongSizeException;
	
	
	/**
	 * Appends a column with provided key to the end.
	 * If provided column is null or empty, column with null values is added
	 * 
	 * @throws IllegalArgumentException if column with given key already exists
	 * @throws WrongSizeException       if size of column doesn't fit table
	 */
	public default Table<R, C, V> addColumn(C key, Line<? extends C, ? extends V> column)
										throws IllegalArgumentException,
										       WrongSizeException {
		addColumn(key, column.toList());
		return this;
	}
	
	
	/**
	 * Inserts a new column before column with given index.
	 * If <tt>index >= this.columnNumber()</tt>, new column is appended to the end.
	 * If provided collection is null or empty, column with null values is added
	 * 
	 * @throws NoSuchElementException	if <tt>index < 0</tt>
	 * @throws WrongSizeException	    if size of passed column doesn't fit table
	 */
	public Table<R, C, V> insertColumn(int index, Collection<? extends V> row) 
			                        	throws	NoSuchElementException,
			                        			WrongSizeException;
	
	
	/**
	 * Inserts a new column with key before column with given index.
	 * If <tt>index >= this.columnNumber()</tt>, new row is appended to the end.
	 * If provided collection is null or empty, column with null values is added
	 * 
	 * @throws IllegalArgumentException	if column with provided key already exists
	 * @throws NoSuchElementException	if <tt>index < 0</tt>
	 * @throws WrongSizeException		if size of passed column doesn't fit table
	 */
	public Table<R, C, V> insertColumn(int index, C key, Collection<? extends V> column)
										throws	IllegalArgumentException, 
										   		NoSuchElementException, 
										   		WrongSizeException;

	
	/**
	 * Inserts a column with provided key before column with given index.
	 * If <tt>index >= this.columnNumber()</tt>, row is appended to the end.
	 * If provided column is null or empty, column with null values is added
	 * 
	 * @throws IllegalArgumentException	if column with provided key already exists
	 * @throws NoSuchElementException	if <tt>index < 0</tt>
	 * @throws WrongSizeException		if size of passed row doesn't fit table
	 */
	public default Table<R, C, V> insertColumn(int index, C key, Line<? extends C, ? extends V> column)
										throws IllegalArgumentException,
										       NoSuchElementException,
										       WrongSizeException {
		insertColumn(index, key, column.toList());
		return this;
	}
	

	/**
	 * Replaces a column at given index with new cell values, returns old values
	 * 
	 * @throws NoSuchElementException	if column with given index doesn't exist
	 * @throws WrongSizeException	    if size of passed column doesn't fit table
	 */
	public default List<V> replaceColumn(int index, Collection<? extends V> column) 
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsColumn(index))
			throw new NoSuchElementException();
		Line<C, V> col = getColumn(index);
		if (column.size() != col.size())
			throw new WrongSizeException(col.size(), column.size());
		
		List<V> old = new ArrayList<>();
		Iterator<? extends V> i = column.iterator();
		for (Cell<V> c : col.cells()) {
			old.add(c.get());
			c.set(i.next());
		}
		return old;
	}
	
	
	/**
	 * Replaces a column with given key with new cell values, returns old values
	 * 
	 * @throws NoSuchElementException	if column with given key doesn't exist
	 * @throws WrongSizeException	    if size of passed column doesn't fit table
	 */
	public default List<V> replaceColumn(C key, Collection<? extends V> column)
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsColumn(key))
			throw new NoSuchElementException();
		Line<C, V> col = getColumn(key);
		if (column.size() != col.size())
			throw new WrongSizeException(col.size(), column.size());
		
		List<V> old = new ArrayList<>();
		Iterator<? extends V> i = column.iterator();
		for (Cell<V> c : col.cells()) {
			old.add(c.get());
			c.set(i.next());
		}
		return old;
	}
	
	
	/**
	 * Replaces cell values of column at given index with cell values of another column, 
	 * returns old cell values
	 * 
	 * @throws NoSuchElementException	if column with given index doesn't exist
	 * @throws WrongSizeException	    if size of passed column doesn't fit table
	 */
	public default List<V> replaceColumn(int index, Line<? extends C, ? extends V> column) 
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsColumn(index))   
			throw new NoSuchElementException();
		Line<C, V> line = getColumn(index);
		if (column == line)
			return line.toList();
		if (line.size() != column.size())
			throw new WrongSizeException(line.size(), column.size());
		
		List<V> old = new ArrayList<>();
		Iterator<Cell<V>> ci = line.cells().iterator();
		for (Cell<? extends V> c : column.cells()) {
			Cell<V> cell = ci.next();
			old.add(cell.get());
			cell.set(c.get());
		}
		return old;
	}

	
	/**
	 * Replaces cell values of column with given key with cell values of another column, 
	 * returns old cell values
	 * 
	 * @throws NoSuchElementException	if column with given index doesn't exist
	 * @throws WrongSizeException	    if size of passed column doesn't fit table
	 */
	public default List<V> replaceColumn(C key, Line<? extends C, ? extends V> column) 
										throws	NoSuchElementException,
												WrongSizeException {
		if (!containsColumn(key))   
			throw new NoSuchElementException();
		Line<C, V> line = getColumn(key);
		if (column == line)
			return line.toList();
		if (line.size() != column.size())
			throw new WrongSizeException(line.size(), column.size());
		
		List<V> old = new ArrayList<>();
		Iterator<Cell<V>> ci = line.cells().iterator();
		for (Cell<? extends V> c : column.cells()) {
			Cell<V> cell = ci.next();
			old.add(cell.get());
			cell.set(c.get());
		}
		return old;
	}
	
	
	/**
	 * Removes a column at given index and returns its items
	 * @throws NoSuchElementException if <tt>index < 0 || index >= this.columnNumber()</tt>
	 */
	public List<V> removeColumn(int index) throws NoSuchElementException;
	
	
	/**
	 * Removes a column by given key and returns its items
	 * @throws NoSuchElementException if key doesn't exist
	 */
	public List<V> removeColumn(C key) throws NoSuchElementException;
	
	
	/**
	 * Removes the last column and returns its items
	 * @throws NoSuchElementException if table is empty
	 */
	public default List<V> removeLastColumn() throws NoSuchElementException {
		return removeRow(columnNumber() - 1);
	}
	
	
	/**
	 * Returns if table contains column with given index
	 */
	public default boolean containsColumn(int index) {
		return index >= 0 && index < columnNumber();
	}
	
	
	/**
	 * Returns if table contains column with given key
	 */
	public boolean containsColumn(C key);
	
	
	/**
	 * Returns index for given column key, or -1 if such key doesn't exist
	 */
	public int getColumnIndex(C key);
	
	
	/**
	 * Returns key for given column index or null if undefined
	 * @throws NoSuchElementException if <tt>index < 0 || index >= this.rowNumber()</tt>
	 */
	public C getColumnKey(int index) throws NoSuchElementException;
		
	
	/**
	 * Sets a new key to the column at given index, returns old key or null
	 * @throws NoSuchElementException	if <tt>index < 0 || index >= this.rowNumber()</tt>
	 * @throws IllegalArgumentException if given key is already associated with another column
	 */
	public C setColumnKey(int index, C key) throws NoSuchElementException,
												   IllegalArgumentException;
	
	
	/**
	 * Sets a new key to the column with given key
	 * @throws NoSuchElementException	if column with <tt>oldKey</tt> doesn't exist
	 */
	public void setColumnKey(C oldKey, C newKey) throws NoSuchElementException;
	
	
	
	
	/* -------------------------- random cell accessors ---------------------------  */
	

	/**
	 * Returns cell at given row and column index
	 * @throws NoSuchElementException  if indices are out of table
	 */
	public Cell<V> getCell(int row, int column) throws NoSuchElementException;
	
	
	/**
	 * Returns cell at given row and column, defined by their keys
	 * @throws NoSuchElementException  if row or column, or both, don't exist for given key(s)
	 */
	public Cell<V> getCell(R rowKey, C columnKey) throws NoSuchElementException;
		
	
	
	/* --------------------------- iterables and streams --------------------------- */
		
	// row iterators and streams
	
	/**
	 * Row iterator. 
	 * Should be used to mutate rows or collect cell values
	 */
	public Iterable<Line<R, V>> rows();
	
	
	/**
	 * Row iterator accepting iteration order. 
	 * Should be used to mutate rows or collect cell values
	 */
	public Iterable<Line<R, V>> rows(boolean reversed);
	
	
	/**
	 * Row stream. 
	 * Should be used to mutate rows or collect cell values
	 */
	public default Stream<Line<R, V>> rowStream() {
		return StreamSupport.stream(rows().spliterator(), false);
	}
	
	
	/**
	 * Row key iterator.
	 * If key for some row is undefined, <tt>next()</tt> returns <tt>null</tt>
	 */
	public Iterable<R> rowKeys();
	
	
	/**
	 * Row key stream.
	 * If key for some row is undefined, <tt>next()</tt> returns <tt>null</tt>
	 */
	public default Stream<R> rowKeyStream() {
		return StreamSupport.stream(rowKeys().spliterator(), false);
	}
	
	
	// column iterators and streams
	
	/**
	 * Column iterator. 
	 * Should be used to mutate columns or collect cell values
	 */
	public Iterable<Line<C, V>> columns();
	
	
	/**
	 * Column iterator accepting iteration order.
	 * Should be used to mutate columns or collect cell values
	 */
	public Iterable<Line<C, V>> columns(boolean reversed);
	
	
	/**
	 * Column stream. 
	 * Should be used to mutate columns or collect cell values
	 */
	public default Stream<Line<C, V>> columnStream() {
		return StreamSupport.stream(columns().spliterator(), false);
	}
	
	
	/**
	 * Column key iterator. 
	 * If key for some column is undefined, <tt>next()</tt> returns <tt>null</tt>
	 */
	public Iterable<C> columnKeys();
	
	
	/**
	 * Column key stream.
	 * If key for some column is undefined, <tt>next()</tt> returns <tt>null</tt>
	 */
	public default Stream<C> columnKeyStream() {
		return StreamSupport.stream(columnKeys().spliterator(), false);
	}
	
	
	/**
	 * Cell iterator, returning cells row-by-row. 
	 * Should be used to mutate cells or collect their values
	 */
	public default Iterable<Cell<V>> cells() {
		return () -> {
			return new Iterator<Cell<V>>(){

				Iterator<Line<R, V>> ri = rows().iterator();
				int n = rowNumber();
				boolean empty = n == 0;
				
				Iterator<Cell<V>> ci = empty ? null : ri.next().iterator();
				
				@Override
				public boolean hasNext() {
					return !empty && (ci.hasNext() || ri.hasNext());
				}

				@Override
				public Cell<V> next() {
					if (!hasNext())
						throw new NoSuchElementException();
					if (!ci.hasNext()) 
						ci = ri.next().iterator();
					return ci.next();
				}
				
			};
		};
	}
	
	
	/**
	 * Cell value iterator, returning cell values row-by-row
	 */
	public default Iterable<V> cellValues() {
		return () -> {
			Iterator<Cell<V>> i = cells().iterator();
			return new Iterator<V>() {
				@Override public boolean hasNext() { return i.hasNext(); }
				@Override public V next()          { return i.next().get(); }
			};
		};
	}
	
	
	/**
	 * Cell stream, returning cells row-by-row. 
	 * Should be used to mutate cells or collect their values
	 */
	public default Stream<Cell<V>> cellStream() {
		return StreamSupport.stream(cells().spliterator(), false);
	}
	
	
	
	/* ------------------------- converters ------------------------- */
	
	// row-by-row converters
	
	/**
	 * Returns list of lists of cell values in row default order 
	 * (as returned by row stream)
	 */
	public default List<List<V>> toRowCellsList() {
		return rowStream().map(Line::toList).collect(toList());
	}
	
	
	/**
	 * Returns map <tt>(key -> List(cell value))</tt> for rows with non-null key	
	 */
	public default Map<R, List<V>> toRowCellsMap() {
		Map<R, List<V>> map = new HashMap<>();
		int i = 0;
		for (Line<R, V> line : rows()) {
			R key = getRowKey(i++);
			if (key != null)
				map.put(key, line.toList());
		}
		return map;
	}
	
	
	// column-by-column converters
	
	/**
	 * Returns list of lists of cell values in column default order 
	 * (as returned by column stream)
	 */
	public default List<List<V>> toColumnCellsList() {
		return columnStream().map(Line::toList).collect(toList());
	}
	
	
	/**
	 * Returns map <tt>(key -> List(cell value))</tt> for columns with non-null key	
	 */
	public default Map<C, List<V>> toColumnCellsMap() {
		Map<C, List<V>> map = new HashMap<>();
		int i = 0;
		for (Line<C, V> line : columns()) {
			C key = getColumnKey(i++);
			if (key != null)
				map.put(key, line.toList());
		}
		return map;
	} 
	
	
	
	
	/* ---------------------- factory methods -------------------------- */
	
	
	/**
	 * Creates a new table which contents and keys are copied from provided
	 * source table
	 */
	public static <R, C, V> Table<R, C, V> fromTable(Supplier<Table<R, C, V>> creator,
									Table<? extends R, ? extends C, ? extends V> source) {
		Table<R, C, V> table = creator.get();
		int i = 0;
		for (Line<? extends R, ? extends V> row : source.rows()) {
			table.addRow(source.getRowKey(i++), row);
		}
		i = 0;
		for (C key : source.columnKeys())
			table.setColumnKey(i++, key);
		return table;
	}
	
	
	/**
	 * Creates a new table which contents are copied from provided collection
	 */
	public static <R, C, V> Table<R, C, V> fromRowCells(Supplier<Table<R, C, V>> creator, 
									Collection<? extends Collection<? extends V>> source) {
		Table<R, C, V> table = creator.get();
		source.forEach(table::addRow);
		return table;
	}
	
	
	/**
	 * Creates a new table which contents and row keys are copied from provided map.
	 * Order of rows is defined by order of <tt>Map#entrySet()#forEach()</tt>
	 */
	public static <R, C, V> Table<R, C, V> fromRowCellsMap(Supplier<Table<R, C, V>> creator,
									Map<? extends R, ? extends Collection<? extends V>> source) {
		Table<R, C, V> table = creator.get();
		source.entrySet().forEach(e -> table.addRow(e.getKey(), e.getValue()));
		return table;
	}

	
	/**
	 * Creates a new table which contents are copied from provided collection
	 */
	public static <R, C, V> Table<R, C, V> fromColumnCells(Supplier<Table<R, C, V>> creator, 
									Collection<? extends Collection<? extends V>> source) {
		Table<R, C, V> table = creator.get();
		source.forEach(table::addColumn);
		return table;
	}
	
	
	/**
	 * Creates a new table which contents and column keys are copied from provided map.
	 * Order of columns is defined by order of <tt>Map#entrySet()#forEach()</tt>
	 */
	public static <R, C, V> Table<R, C, V> fromColumnCellsMap(Supplier<Table<R, C, V>> creator,
								Map<? extends C, ? extends Collection<? extends V>> source) {
		Table<R, C, V> table = creator.get();
		source.entrySet().forEach(e -> table.addColumn(e.getKey(), e.getValue()));
		return table;
	} 
}
