package ru.salauyou.linkedtable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import ru.salauyou.table.Cell;
import ru.salauyou.table.Line;
import ru.salauyou.table.Table;
import ru.salauyou.table.WrongSizeException;



public class LinkedTable<R, C, V> implements Table<R, C, V>, Serializable {
	
	private static final long serialVersionUID = 6939482161443314381L;

	// list ot rows and columns
	List<LinkedLine<R, V>> rows = new ArrayList<>();
	List<LinkedLine<C, V>> cols = new ArrayList<>();
	
	// mapping keys to rows and columns
	Map<R, LinkedLine<R, V>> rowMap = null;
	Map<C, LinkedLine<C, V>> colMap = null;
	
	
	
	/**
	 * Default constructor
	 */
	public LinkedTable() { }
	

	
	/**
	 * Creates empty (cell values filled with nulls) <tt>LinkedTable</tt> 
	 * with given number of rows and columns
	 */
	public LinkedTable(int rows, int columns) {
		List<V> empty = new ArrayList<>();
		for (int i = 0; i < columns; i++) {
			empty.add(null);
		}
		for (int i = 0; i < rows; i++) {
			this.addRow(empty);
		}
	}
	
	
	
	/**
	 * Creates empty (cell values filled with nulls) <tt>LinkedTable</tt> 
	 * which have rows and columns defined by key collections
	 */
	public LinkedTable(Collection<? extends R> rowKeys, Collection<? extends C> columnKeys) {
		this(rowKeys.size(), columnKeys.size());
		int i = 0;
		for (R key : rowKeys) {
			setRowKey(i++, key);
		}
		i = 0;
		for (C key : columnKeys) {
			setColumnKey(i++, key);
		}
	}
	
	
	
	@Override
	public int rowNumber() {
		return rows.size();
	}
	
	
	
	@Override
	public int columnNumber() {
		return cols.size();
	}
	
	
	
	@Override
	public Line<R, V> getRow(R key) throws NoSuchElementException {
		checkKey(key, true);
		return rowMap.get(key);
	}
	
	
	
	@Override
	public Line<R, V> getRow(int index) throws NoSuchElementException {
		checkIndex(index, true);
		return rows.get(index);
	}
	
	
	
	@Override
	public Table<R, C, V> addRow(Collection<? extends V> row) throws WrongSizeException {
		return addRow(null, row);
	}
	
	
	
	@Override
	public Table<R, C, V> addRow(R key, Collection<? extends V> row)
									   throws IllegalArgumentException, WrongSizeException {
		checkSize(row, true);
		checkKeyNotDuplicate(key, true);
		insertLine(rows.size(), key, row, true);
		return this;
	}
	
	
	
	@SuppressWarnings("unchecked")
	void insertLine(int index, Object key, Collection<? extends V> line, boolean isRow) {
		
		boolean lineEmpty = (line == null || line.size() == 0);
		boolean tableEmpty = rows.size() == 0;		
		
		if (tableEmpty && lineEmpty)	// empty line is inserted into empty table
			return;
		
		// adjust index
		if (isRow && index > rows.size())
			index = rows.size();
		if (!isRow && index > cols.size())
			index = cols.size();
		
		int size = lineEmpty ? (isRow ? cols.size() : rows.size()) : line.size();
		
		// new line
		LinkedLine<?, V> newLine = new LinkedLine<>(isRow == true ? LineType.ROW : LineType.COLUMN, 
				                                    null, null, key, index, this);
		
		LinkedCell<V> prev = null;		// previous cell
	    LinkedCell<V> right;			// cell right to the current as seen in direction of traversal
	    LinkedCell<V> left;				// cell left to the current
	    
	    boolean lineFirst = false;		// will inserted line be the first?
	    boolean lineLast = false;	 	// will it be the last?
	    
	    if (isRow) {
	    	right = index < rows.size() ? rows.get(index).first : null;
	    	left = (index == 0 || rows.size() == 0) ? null : rows.get(index - 1).first;
	    	lineFirst = left == null;
	    	lineLast = right == null;
	    } else {
	    	left = index < cols.size() ? cols.get(index).first : null;
	    	right = (index == 0 || rows.size() == 0) ? null : cols.get(index - 1).first;
	    	lineFirst = right == null;
	    	lineLast = left == null;
	    }
	    
	    // iterator to get cell values
	    Iterator<? extends V> iv = lineEmpty ? null : line.iterator();
	    
	    // main loop
	    for (int i = 0; i < size; i++) {
	    	V value = iv == null ? null : iv.next();	    	
	    	LinkedCell<V> n = isRow ? new LinkedCell<>(left, right, prev, null, value)
	    							: new LinkedCell<>(prev, null, right, left, value);
	    	if (isRow) {
	    		if (prev != null)
	    			prev.r = n;
	    		if (right != null)
	    			right.u = n;
	    		if (left != null)
	    			left.d = n;
	    		left = left == null ? null : left.r;
	    		right = right == null ? null : right.r;
	    		prev = n;
	    	} else {
	    		if (prev != null)
	    			prev.d = n;
	    		if (right != null)
	    			right.r = n;
	    		if (left != null)
	    			left.f = n;
	    		left = left == null ? null : left.d;
	    		right = right == null ? null : right.d;
	    	}
	    	
	    	if (i == 0)
	    		newLine.first = n;
	    	if (i == size - 1)
	    		newLine.last = n;
	    	
	    	if (tableEmpty) {				// line is inserted into empty table
	    		if (isRow)
	    			cols.add(new LinkedLine<>(LineType.COLUMN, n, n, null, i, this));
	    		else
	    			rows.add(new LinkedLine<>(LineType.ROW, n, n, null, i, this));
	    		
	    	} else if (lineFirst) {			// reassign first cell
	    		if (isRow)
	    			cols.get(i).first = n;
	    		else
	    			rows.get(i).first = n;
	    		
	    	} else if (lineLast) {			// reassign last cell
	    		if (isRow)
	    			cols.get(i).last = n;
	    		else
	    			rows.get(i).last = n;
	    	}
	    		
	    }
	    
	    // add row/column to main list
	    if (isRow) {
	    	if (lineLast)
	    		rows.add(index, (LinkedLine<R, V>) newLine);
	    	else
	    		rows.add((LinkedLine<R, V>) newLine);
	    } else {
	    	if (lineLast)
	    		cols.add(index, (LinkedLine<C, V>) newLine);
	    	else
	    		cols.add((LinkedLine<C, V>) newLine);
	    }
	    
	    // put row/column to key map
	    if (key != null && isRow)
	    	getRowMap().put((R) key, (LinkedLine<R, V>) newLine);
	    else if (key != null && !isRow)
	    	getColMap().put((C) key, (LinkedLine<C, V>) newLine); 	
	}
	
	
	
	@Override
	public List<V> removeRow(R key) throws NoSuchElementException {
		checkKey(key, true);
		int index = indexForKey(key, true);
		rowMap.remove(key);
		return removeAndReturn(index, true);
	}
	
	
	
	@Override
	public List<V> removeRow(int index) throws NoSuchElementException {
		checkIndex(index, true);			
		LinkedLine<R, V> row = rows.get(index);		// row to remove
		if (row.key != null) {						// remove key association
			rowMap.remove(row.key);
		}
		return removeAndReturn(index, true);
	}
	
	
	
	@Override
	public boolean containsRow(R key) {
		return rowMap != null && rowMap.containsKey(key);
	}



	@Override
	public int getRowIndex(R key) {
		return indexForKey(key, true);
	}



	@Override
	public R getRowKey(int index) throws NoSuchElementException {
		checkIndex(index, true);
		return rows.get(index).key;
	}



	@Override
	public R setRowKey(int index, R key) throws NoSuchElementException, 
	                                            IllegalArgumentException {
		checkIndex(index, true);
		LinkedLine<R, V> row = rows.get(index);
		if (row.key != null && row.key.equals(key))
			return key;
		checkKeyNotDuplicate(key, true);
		R oldKey = row.key;
		row.key = key;
		if (rowMap != null) 
			rowMap.remove(oldKey);
		getRowMap().put(key, row);
		return oldKey;
	}



	@Override
	public void setRowKey(R oldKey, R newKey) throws NoSuchElementException {
		checkKey(oldKey, true);
		if (oldKey.equals(newKey))
			return;
		
		LinkedLine<R, V> row = rowMap.get(oldKey);
		rowMap.remove(oldKey);
		row.key = newKey;
		rowMap.put(newKey, row);
	}
	
	

	
	@Override
	public Line<C, V> getColumn(C key) throws NoSuchElementException {
		checkKey(key, false);
		return colMap.get(key);
	}
	
	
	
	@Override
	public Line<C, V> getColumn(int index) throws NoSuchElementException {
		checkIndex(index, false);
		return cols.get(index);
	}
	

	
	@Override
	public List<V> removeColumn(C key) throws NoSuchElementException {
		checkKey(key, false);
		int index = indexForKey(key, false);
		colMap.remove(key);
		return removeAndReturn(index, false);
	}
	
	
	
	@Override
	public List<V> removeColumn(int index) throws NoSuchElementException {
		checkIndex(index, false);
		LinkedLine<C, V> column = cols.get(index);	// column to remove
		if (colMap != null && column.key != null) { // remove key association
			colMap.remove(column.key);
		}
		return removeAndReturn(index, false);
	}
	
	
	
	
	// inner checking methods
	
	private void checkIndex(int index, boolean isRow) throws NoSuchElementException {
		if (index < 0 || (isRow && index >= rows.size()) 
				|| (!isRow && index >= cols.size()))
			throw new NoSuchElementException (
					String.format("Out of bounds: index=%d, %s=%d",
							index, 
							isRow ? "rows" : "columns", 
							isRow ? rows.size() : cols.size()
							));
	}
	
	
	
	private void checkKey(Object key, boolean isRow) throws NoSuchElementException {
		if (key == null)
			throw new NoSuchElementException("Null keys are not allowed");
		if ((isRow && (rowMap == null || !rowMap.containsKey(key))) 
				|| (!isRow && (colMap == null || !colMap.containsKey(key)))) 
			throw new NoSuchElementException("Key {" + key + "} doesn't exist");
	}
	
	
	
	private void checkKeyNotDuplicate(Object key, boolean isRow) throws IllegalArgumentException {
		if (key == null)
			return;
		if ((isRow && rowMap != null && rowMap.containsKey(key)) 
				|| (!isRow && colMap != null && colMap.containsKey(key))) 
			throw new IllegalArgumentException("Key {" + key + "} already exists");
	}
	
	
	private void checkSize(Collection<? extends V> line, boolean isRow) throws WrongSizeException {
		if (isRow && cols.size() > 0 && line.size() != cols.size())
			throw new WrongSizeException(cols.size(), line.size());
		if (!isRow && rows.size() > 0 && line.size() != rows.size())
			throw new WrongSizeException(rows.size(), line.size());
	}
	
	
	
	
	// Removes row or column by given index. 
	// Cost is always O(k)
	
	private List<V> removeAndReturn(int index, boolean isRow) {
		
		List<V> old = new ArrayList<>(isRow ? cols.size() : rows.size());
		
		boolean first = index == 0;
		boolean last = index == ((isRow ? rows.size() : cols.size()) - 1);		
		LinkedCell<V> n = isRow ? rows.get(index).first : cols.get(index).first;
		
		if (first && last) {				// only one line exists
			while (n != null) {
				old.add(n.get());
					
				if (isRow) {				
					if (n.f != null) 		// help GC
						n.f.r = n.f = null;
					n = n.r;
				} else {
					if (n.u != null) 
						n.u.d = n.u = null;
					n = n.d;
				}
			}
			rows.clear();
			cols.clear();
			rowMap.clear();
			colMap.clear();
			
		} else {   							// more than one bar exist
			for (int i = 0; i < (isRow ? cols.size() : rows.size()); i++) {		
				old.add(n.get());
				
				if (isRow) {
					if (first) 
						cols.get(i).first = n.d;
					if (last) 
						cols.get(i).last = n.u;
					if (n.u != null)		// reassign references
						n.u.d = n.d;
					if (n.d != null)
						n.d.u = n.u;
					if (n.f != null) 		// help GC
						n.f.r = n.f = n.d = n.u = null;
					n = n.r;
					
				} else {
					if (first)
						rows.get(i).first = n.r;
					if (last)
						rows.get(i).last = n.f;
					if (n.f != null) 		// reassign references
						n.f.r = n.r;
					if (n.r != null)
						n.r.f = n.f;
					if (n.u != null) 		// help GC
						n.u.d = n.u = n.r = n.f = null;
					n = n.d;
				}
			}
			
			if (isRow)
				rows.remove(index);
			else
				cols.remove(index);
		}
		
		return old;
	}
	
	
	
	// Search for index by provided key. 
	// Cost: O(1) if lines weren't rearranged by insertion or deletion
	//       O(k) in other case of significant rearrange
	
	int indexForKey(Object key, boolean isRow) {
		LinkedLine<?, V> line = isRow ? rowMap.get(key) : colMap.get(key);
		if (line == null)
			return -1;
		
		int size = (isRow ? rows.size() : cols.size());
		int ir = isRow ? Math.min(rowMap.get(key).weakIndex, size - 1) 
				       : Math.min(colMap.get(key).weakIndex, size - 1);
		
		int il = ir - 1;
		while (il >= 0 || ir < size) {		// search in both directions
			if (il >= 0) {
				if (isRow && rows.get(il) == line) {
					rows.get(il).weakIndex = il;
					return il;
				} else if (!isRow && cols.get(il) == line) {
					cols.get(il).weakIndex = il;
					return il;
				} else {
					il--;
				}
			}
			if (ir < size) {
				if (isRow && rows.get(ir) == line) {
					rows.get(ir).weakIndex = ir;
					return ir;
				} else if (!isRow && cols.get(ir) == line) {
					cols.get(ir).weakIndex = ir;
					return ir;
				} else {
					ir++;
				}
			}
		}
		return -1;
	}
	
	
	// key maps lazy initializers 
	
	Map<R, LinkedLine<R, V>> getRowMap() {
		if (rowMap == null)
			rowMap = new HashMap<>();
		return rowMap;
	}
	
	Map<C, LinkedLine<C, V>> getColMap() {
		if (colMap == null)
			colMap = new HashMap<>();
		return colMap;
	}
	
	
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Rows: "+ rows.size());
		for (LinkedLine<R, V> row : rows) {
			sb.append("\n").append(row);
		}
		sb.append("\nColumns: " + cols.size());
		for (LinkedLine<C, V> col : cols) {
			sb.append("\n").append(col);
		}
		return sb.toString();
	}
	
	
	
	// ------------------- iterators ------------------------ //
	

	@Override
	public Iterable<Line<R, V>> rows() {
		return rows(false);
	}
	

	@Override
	public Iterable<Line<C, V>> columns() {
		return columns(false);
	}
	
	
	@Override
	public Iterable<Line<R, V>> rows(boolean reversed) {
		return () -> {
			return rowIterator(reversed);
		};
	}
	
	
	@Override
	public Iterable<Line<C, V>> columns(boolean reversed) {
		return () -> {
			return columnIterator(reversed);
		};
	}
	
	
	
	@Override
	public Iterable<R> rowKeys() {
		return () -> {
			return new Iterator<R>() {
				int i = 0;
				
				@Override public boolean hasNext() { 
					return i < rows.size(); 
				}
				
				@Override public R next() { 
					if (!hasNext())
						throw new NoSuchElementException();
					return rows.get(i++).key; 
				}	
			};
		};
	}
	
	
	
	@Override
	public Iterable<C> columnKeys() {
		return () -> {
			return new Iterator<C>() {
				int i = 0;
				
				@Override public boolean hasNext() { 
					return i < cols.size(); 
				}
				
				@Override public C next() { 
					if (!hasNext())
						throw new NoSuchElementException();
					return cols.get(i++).key; 
				}	
			};
		};
	}
	
	
	
	
	// Iterator implementations
	// Cost is always O(1) for new(), hasNext() and next()
	
	Iterator<Line<R, V>> rowIterator(boolean reversed) {
		return new Iterator<Line<R, V>>(){
			int index = rows.size() == 0 ? -1 : (reversed ? rows.size() - 1 : 0);
			
			@Override public boolean hasNext() {
				return index >= 0 && index < rows.size();
			}

			@Override public Line<R, V> next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return rows.get(reversed ? index-- : index++);
			}
		};
	}
	
	
	
	Iterator<Line<C, V>> columnIterator(boolean reversed) {
		return new Iterator<Line<C, V>>(){
			int index = cols.size() == 0 ? -1 : (reversed ? cols.size() - 1 : 0);
			
			@Override
			public boolean hasNext() {
				return index >= 0 && index < cols.size();
			}

			@Override
			public Line<C, V> next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return cols.get(reversed ? index-- : index++);
			}
		};
	}
	
	

	
	/** ================== nested classes ==================== **/
	
	
	
	enum LineType implements Serializable { ROW, COLUMN };
	
	
	/** Represents a single row or column */
	static class LinkedLine<K, V> implements Line<K, V>, Serializable {
		
		private static final long serialVersionUID = -2644441702340616775L;
		
		LinkedCell<V> first;
		LinkedCell<V> last;
		LineType type;
		K key;
		int weakIndex;
		LinkedTable table;
		
		
		LinkedLine(LineType type, LinkedCell<V> first, LinkedCell<V> last, K key, 
				   int weakIndex, LinkedTable table) {
			this.first = first;
			this.last = last;
			this.type = type;
			this.key = key;
			this.weakIndex = weakIndex;
			this.table = table;
		}
		
		
		@Override
		public Iterator<Cell<V>> iterator() {
			return iterator(false);
		}
		
		
		@Override
		public Iterable<Cell<V>> cells() {
			return cells(false);
		}
		
		
		@Override
		public Iterable<Cell<V>> cells(boolean reversed) {
			return () -> {
				return iterator(reversed);
			};
		}
		
		
		@Override
		public List<V> toList() {
			LinkedCell<V> n = first;
			List<V> res = new ArrayList<>();
			while (n != null) {
				res.add(n.get());
				n = type == LineType.ROW ? n.r : n.d;
			}
			return res;
		}
		

		@Override
		public Cell<V> firstCell() throws NoSuchElementException {
			if (first == null)
				throw new NoSuchElementException();
			else
				return first;
		}


		@Override
		public Cell<V> lastCell() throws NoSuchElementException {
			if (last == null)
				throw new NoSuchElementException();
			else
				return last;
		}


		@Override
		public Cell<V> getCell(int index) throws NoSuchElementException {
			LinkedCell<V> n = (LinkedCell<V>) firstCell();
			for (int i = 0; i < index; i++) {
				if (type == LineType.ROW ? n.r == null : n.d == null) 
					throw new NoSuchElementException();
				else 
					n = type == LineType.ROW ? n.r : n.d;
			}
			return n;
		}
		
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(type == LineType.ROW ? "R[" : "C[");
			LinkedCell<V> n = first;
			while (n != null) {
				sb.append(n.id).append(',').append(' ');
				n = type == LineType.ROW ? n.r : n.d;
			}
			if (sb.length() > 2)
				sb.delete(sb.length() - 2, sb.length());
			return sb.append(']').toString();
		}

		
		@Override
		public int size() {
			return type == LineType.ROW ? table.cols.size() : table.rows.size();
		}
		

		Iterator<Cell<V>> iterator(boolean reversed) {
			return new Iterator<Cell<V>>() {
				LinkedCell<V> n = reversed ? last : first;
				
				@Override
				public boolean hasNext() {
					return n != null;
				}

				@Override
				public Cell<V> next() {
					if (n == null)
						throw new NoSuchElementException();
					LinkedCell<V> c = n;
					if (type == LineType.ROW) 
						n = reversed ? n.f : n.r;
					else
						n = reversed ? n.u : n.d;
					return c;
				}
			};
		}
	}
	
	

	/** ----------------- LinkedCell class ------------------ **/
	
	
	public static class LinkedCell<T> implements Cell<T>, Serializable {
		
		private static final long serialVersionUID = -1363494083583791405L;
		
		static int idCounter = 0;
		int id;      	  // cell ID
		
		LinkedCell<T> u;  // upper
		LinkedCell<T> d;  // lower
		LinkedCell<T> f;  // left
		LinkedCell<T> r;  // right
		
		T value;
		
		LinkedCell(LinkedCell<T> u, LinkedCell<T> d, LinkedCell<T> f, LinkedCell<T> r, T value) {
			this.u = u;
			this.d = d;
			this.r = r;
			this.f = f;
			id = ++idCounter;
			this.value = value;
		}
		
		@Override
		public T set(T value) {
			T old = this.value;
			this.value = value;
			return old;
		} 
		
		@Override
		public T get() {
			return this.value;
		}
		
		@Override
		public String toString() {
			return String.format("Cell [%03d]: u=%03d, d=%03d, f=%03d, r=%03d, value=%s", id, 
					              (u == null ? 0 : u.id), (d == null ? 0 : d.id), 
					              (f == null ? 0 : f.id), (r == null ? 0 : r.id),
					              value
					              );
		}
	}



	@Override
	public Table<R, C, V> insertRow(int index, Collection<? extends V> row)
			throws NoSuchElementException, WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Table<R, C, V> insertRow(int index, R key,
			Collection<? extends V> row) throws IllegalArgumentException,
			NoSuchElementException, WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}



	



	@Override
	public Table<R, C, V> addColumn(Collection<? extends V> column)
			throws WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Table<R, C, V> addColumn(C key, Collection<? extends V> column)
			throws IllegalArgumentException, WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Table<R, C, V> insertColumn(int index, Collection<? extends V> row)
			throws NoSuchElementException, WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Table<R, C, V> insertColumn(int index, C key,
			Collection<? extends V> column) throws IllegalArgumentException,
			NoSuchElementException, WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean containsColumn(C key) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public int getColumnIndex(C key) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public C getColumnKey(int index) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public C setColumnKey(int index, C key) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setColumnKey(C oldKey, C newKey) throws NoSuchElementException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public Cell<V> getCell(int row, int column) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Cell<V> getCell(R rowKey, C columnKey) throws NoSuchElementException {
		// TODO Auto-generated method stub
		return null;
	}

	/*

	@Override
	public Table<R, C, V> addRow(R key, Line<? extends R, ? extends V> row)
			throws IllegalArgumentException, WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Table<R, C, V> insertRow(int index, R key,
			Line<? extends R, ? extends V> row)
			throws IllegalArgumentException, NoSuchElementException,
			WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Table<R, C, V> addColumn(C key, Line<? extends C, ? extends V> column)
			throws IllegalArgumentException, WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Table<R, C, V> insertColumn(int index, C key,
			Line<? extends C, ? extends V> column)
			throws IllegalArgumentException, NoSuchElementException,
			WrongSizeException {
		// TODO Auto-generated method stub
		return null;
	}
	 */
}
