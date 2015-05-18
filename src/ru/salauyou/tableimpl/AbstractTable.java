package ru.salauyou.tableimpl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ru.salauyou.table.Cell;
import ru.salauyou.table.Line;
import ru.salauyou.table.Table;
import ru.salauyou.table.WrongSizeException;

public abstract class AbstractTable<R, C, V> implements Table<R, C, V> {

	
	/* --------------------- row accessors and updaters -------------------- */
	
	// should be overrided by underlying implementation to avoid list creation
	@Override
	public Table<R, C, V> addRow(R key, Line<? extends R, ? extends V> row)
										throws IllegalArgumentException,
										       WrongSizeException {
		addRow(key, row.toList());
		return this;
	}
	

	
	// should be overrided by underlying implementation to avoid list creation
	@Override
	public Table<R, C, V> insertRow(int index, R key, Line<? extends R, ? extends V> row)
										throws IllegalArgumentException,
										       NoSuchElementException,
										       WrongSizeException {
		insertRow(index, key, row.toList());
		return this;
	}

	
	
	@Override
	public List<V> replaceRow(int index, Collection<? extends V> row) 
										throws	NoSuchElementException,
												WrongSizeException {
		checkIndex(index, true);
		Line<R, V> rw = getRow(index);
		checkSize(row.size(), rw.size());
		return replaceValues(rw, row, null);
	}
	
	
	@Override
	public List<V> replaceRow(R key, Collection<? extends V> row)
										throws	NoSuchElementException,
												WrongSizeException {
		checkKey(key, true);
		Line<R, V> rw = getRow(key);
		checkSize(row.size(), rw.size());
		return replaceValues(rw, row, null);
	}
	
	
	@Override
	public List<V> replaceRow(int index, Line<? extends R, ? extends V> row) 
										throws	NoSuchElementException,
												WrongSizeException {
		checkIndex(index, true);
		Line<R, V> rw = getRow(index);
		if (row == rw)
			return rw.toList();
		checkSize(row.size(), rw.size());
		return replaceValues(rw, null, row);
	}

	
	@Override
	public List<V> replaceRow(R key, Line<? extends R, ? extends V> row) 
										throws	NoSuchElementException,
												WrongSizeException {
		checkKey(key, true);
		Line<R, V> rw = getRow(key);
		if (row == rw)
			return rw.toList();
		checkSize(row.size(), rw.size());
		return replaceValues(rw, null, row);
	}
	
	
	@Override
	public boolean containsRow(int index) {
		return index >= 0 && index < rowNumber();
	}
	

	
	/* --------------------- column accessors and updaters -------------------- */
	
	
	// should be overrided by underlying implementation to avoid list creation
	@Override
	public Table<R, C, V> addColumn(C key, Line<? extends C, ? extends V> column)
										throws IllegalArgumentException,
										       WrongSizeException {
		addColumn(key, column.toList());
		return this;
	}
	
	
	// should be overrided by underlying implementation to avoid list creation
	@Override
	public Table<R, C, V> insertColumn(int index, C key, Line<? extends C, ? extends V> column)
										throws IllegalArgumentException,
										       NoSuchElementException,
										       WrongSizeException {
		insertColumn(index, key, column.toList());
		return this;
	}
	

	@Override
	public List<V> replaceColumn(int index, Collection<? extends V> column) 
										throws	NoSuchElementException,
												WrongSizeException {
		checkIndex(index, false);
		Line<C, V> col = getColumn(index);
		checkSize(column.size(), col.size());
		return replaceValues(col, column, null);
	}
	
	
	@Override
	public List<V> replaceColumn(C key, Collection<? extends V> column)
										throws	NoSuchElementException,
												WrongSizeException {
		checkKey(key, false);
		Line<C, V> line = getColumn(key);
		checkSize(column.size(), line.size());
		return replaceValues(line, column, null);
	}
	
	
	@Override
	public List<V> replaceColumn(int index, Line<? extends C, ? extends V> column) 
										throws	NoSuchElementException,
												WrongSizeException {
		checkIndex(index, false);
		Line<C, V> line = getColumn(index);
		if (column == line)
			return line.toList();
		checkSize(line.size(), column.size());
		return replaceValues(line, null, column);
	}

	
	@Override
	public List<V> replaceColumn(C key, Line<? extends C, ? extends V> column) 
										throws	NoSuchElementException,
												WrongSizeException {
		checkKey(key, false);
		Line<C, V> line = getColumn(key);
		if (column == line)
			return line.toList();
		checkSize(line.size(), column.size());
		return replaceValues(line, null, column);
	}
	
	
	@Override
	public boolean containsColumn(int index) {
		return index >= 0 && index < columnNumber();
	}
	
	
	
	
	
	// ----------------------- Object method overrides ------------------------ //
	
	
	/**
	 * Hash code is calculated based on keys and cell values. Cost is O(k*m)
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		for (R key : rowKeys()) {
			if (key != null)
				hash = hash * 19 + key.hashCode();
		}
		for (C key : columnKeys()) {
			if (key != null)
				hash = hash * 19 + key.hashCode(); 
		}
		for (V v : cellValues()) {
			if (v != null)
				hash = hash * 19 + v.hashCode();
		}
		return hash;
	}
	
	
	
	/**
	 * equals is true if and only if: 
	 *     1) passed object is <tt>Table</tt> and 
	 *     2) keys for all indices are equal in both tables and
	 *     3) values of cells at all positions are equal in both tables.<br>
	 *     
	 * Cost is O(k*m)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof Table))
			return false;
		
		Table t = (Table) o;
		if (t.rowNumber() != rowNumber() || t.columnNumber() != columnNumber())
			return false;
		
		Iterator i = rowKeys().iterator();				// compare row keys
		Iterator ti = t.rowKeys().iterator();
		while (i.hasNext()) {
			if (!Objects.equals(i.next(), ti.next()))
				return false;
		}
		i = columnKeys().iterator();					// compare column keys
		ti = t.columnKeys().iterator();
		while (i.hasNext()) {
			if (!Objects.equals(i.next(), ti.next()))
				return false;
		}
		i = cellValues().iterator();					// compare cell values
		ti = t.cellValues().iterator();
		while (i.hasNext()) {
			if (!Objects.equals(i.next(), ti.next()))
				return false;
		}
		return true;
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Rows: ").append(rowNumber());
		if (rowNumber() > 0) {
			sb.append(' ').append('{');
			for (R key : rowKeys()) {
				sb.append(key).append(',').append(' ');
			}
			sb.delete(sb.length() - 2, sb.length()).append('}');
		}
		sb.append("\nColumns: ").append(columnNumber());
		if (columnNumber() > 0) {
			sb.append(' ').append('{');
			for (C key : columnKeys()) {
				sb.append(key).append(',').append(' ');
			}
			sb.delete(sb.length() - 2, sb.length()).append('}');
		}
		for (Line<R, V> row : rows()) {
			sb.append('\n');
			for (Cell<V> c : row.cells())
				sb.append(c.get()).append('\t');
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
		
	}
	
	
	
	
	//**------------------------ helper methods --------------------------- **//
	
	
	protected void checkIndex(int index, boolean isRow) {
		if (index < 0 || (isRow && index >= rowNumber()) 
				|| (!isRow && index >= columnNumber()))
			throw new NoSuchElementException (
					String.format("Out of bounds: index=%d, %s=%d",
							index, 
							isRow ? "rows" : "columns", 
							isRow ? rowNumber() : columnNumber()
							));
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected void checkKey(Object key, boolean isRow) throws NoSuchElementException {
		if (key == null)
			throw new NoSuchElementException("Null keys are not allowed for access");
		if ((isRow && containsRow((R) key)) || (!isRow && containsColumn((C) key))) 
			throw new NoSuchElementException(
					"Key {" + key + "} doesn't exist for " + (isRow ? "row" : "column")
					);
	}
	
	
	
	protected void checkSize(int expected, int actual) {
		if (expected != actual)
			throw new WrongSizeException(expected, actual);
	}
	
	
	
	protected List<V> replaceValues(Line<?, V> line, Collection<? extends V> newValues, Line<?, ? extends V> newLine) {
		List<V> old = new ArrayList<>();
		Iterator<? extends V> i = newLine == null 
                                          ? newValues.iterator() 
                                          : cellValueIterator(newLine);
		for (Cell<V> c : line.cells()) {
			old.add(c.get());
			c.set(i.next());
		}
		return old;
	}
	
	
	
	protected Iterator<? extends V> cellValueIterator(Line<?, ? extends V> line) {
		return new Iterator<V>() {
			Iterator<? extends Cell<? extends V>> ci = line.cells().iterator();
			
			@Override public boolean hasNext() { 
				return ci.hasNext(); 
			}
			
			@Override public V next() { 
				return ci.next().get(); 
			}
		};
	}
	
	
	
	/* --------------------------- iterables and streams --------------------------- */
		
	// row iterators and streams
	
	@Override
	public Stream<Line<R, V>> rowStream() {
		return StreamSupport.stream(rows().spliterator(), false);
	}
	
	
	@Override
	public Stream<R> rowKeyStream() {
		return StreamSupport.stream(rowKeys().spliterator(), false);
	}
	
	
	// column iterators and streams
	
	@Override
	public Stream<Line<C, V>> columnStream() {
		return StreamSupport.stream(columns().spliterator(), false);
	}
	
	
	@Override
	public Stream<C> columnKeyStream() {
		return StreamSupport.stream(columnKeys().spliterator(), false);
	}
	
	
	@Override
	public Iterable<Cell<V>> cells() {
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
	
	
	@Override
	public Iterable<V> cellValues() {
		return () -> {
			Iterator<Cell<V>> i = cells().iterator();
			return new Iterator<V>() {
				@Override public boolean hasNext() { return i.hasNext(); }
				@Override public V next()          { return i.next().get(); }
			};
		};
	}
	
	
	@Override
	public Stream<Cell<V>> cellStream() {
		return StreamSupport.stream(cells().spliterator(), false);
	}
	
	
	
	/* ------------------------- converters ------------------------- */
	
	// row-by-row converters
	
	@Override
	public List<List<V>> toRowCellsList() {
		return rowStream().map(Line::toList).collect(toList());
	}
	
	
	@Override
	public Map<R, List<V>> toRowCellsMap() {
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
	
	@Override
	public List<List<V>> toColumnCellsList() {
		return columnStream().map(Line::toList).collect(toList());
	}
	
	
	@Override
	public Map<C, List<V>> toColumnCellsMap() {
		Map<C, List<V>> map = new HashMap<>();
		int i = 0;
		for (Line<C, V> line : columns()) {
			C key = getColumnKey(i++);
			if (key != null)
				map.put(key, line.toList());
		}
		return map;
	} 
	
	
	
	
	/* -------------- AbstractLine nested class ------------------- */
	
	public static abstract class AbstractLine<K, V> implements Line<K, V> {
		
		@Override
		public Stream<Cell<V>> stream() {
			return StreamSupport.stream(cells().spliterator(), false);
		}
	}
	
	
}
