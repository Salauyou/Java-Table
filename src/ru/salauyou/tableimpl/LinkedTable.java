package ru.salauyou.tableimpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import ru.salauyou.table.Cell;
import ru.salauyou.table.Line;
import ru.salauyou.table.Table;
import ru.salauyou.table.WrongSizeException;



public final class LinkedTable<R, C, V> extends AbstractTable<R, C, V> 
                                        implements Serializable {
    
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
        this(rows, columns, null);
    }
    
    
    
    public LinkedTable(int rows, int columns, V defaultValue) {
        List<V> one = Arrays.asList(defaultValue);
        for (int i = 0; i < columns; i++)
            addColumn(one);
        for (int i = 1; i < rows; i++) {
            addRow(null, getRow(0));
        }
    }
    
    
    
    /**
     * Creates empty (cell values filled with nulls) <tt>LinkedTable</tt> 
     * which have rows and columns defined by key collections
     */
    public LinkedTable(Collection<? extends R> rowKeys, 
    		           Collection<? extends C> columnKeys, V defaultValue) {
        this(rowKeys.size(), columnKeys.size(), defaultValue);
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
    
    
    
    //=============== row and column accessors and mutators ==============//
    
    
    @Override
    public Line<R, V> getRow(R key) throws NoSuchElementException {
        checkKey(key, true);
        return rowMap.get(key);
    }
    

    
    @Override
    public Line<C, V> getColumn(C key) throws NoSuchElementException {
        checkKey(key, false);
        return colMap.get(key);
    }



    @Override
    public Line<R, V> getRow(int index) throws NoSuchElementException {
        checkIndex(index, true);
        return rows.get(index);
    }
    
    
    
    @Override
    public Line<C, V> getColumn(int index) throws NoSuchElementException {
        checkIndex(index, false);
        return cols.get(index);
    }



    @Override
    public Table<R, C, V> addRow(Collection<? extends V> row) 
    		                                  throws WrongSizeException {
        if (row != null && row.size() != 0)
            checkSize(row.size(), true);
        return addRow(null, row);
    }
    
    
    
    @Override
    public Table<R, C, V> addColumn(Collection<? extends V> column) 
    		                                  throws WrongSizeException {
        if (column != null && column.size() != 0)
            checkSize(column.size(), false);
        return addColumn(null, column);
    }



    @Override
    public Table<R, C, V> addRow(R key, Collection<? extends V> row)
                                              throws IllegalArgumentException, 
                                                     WrongSizeException {
        if (row != null && row.size() != 0)
            checkSize(row.size(), true);
        checkKeyNotDuplicate(key, true);
        insertLine(rows.size(), key, row == null ? null : row.iterator(), 
                   row == null ? 0 : row.size(), true);
        return this;
    }
    
    
    
    @Override
    public Table<R, C, V> addColumn(C key, Collection<? extends V> column)
                                              throws IllegalArgumentException,
                                                     WrongSizeException {
        if (column != null && column.size() != 0)
            checkSize(column.size(), false);
        checkKeyNotDuplicate(key, false);
        insertLine(cols.size(), key, column == null ? null : column.iterator(), 
                   column == null ? 0 : column.size(), false);
        return this;
    }



    @Override
    public Table<R, C, V> addRow(R key, Line<? extends R, ? extends V> row)
                                              throws IllegalArgumentException, 
                                                     WrongSizeException {
        if (row != null && row.size() != 0)
            checkSize(row.size(), true);
        checkKeyNotDuplicate(key, true);
        insertLine(rows.size(), key, row == null ? null : cellValueIterator(row), 
                   row == null ? 0 : row.size(), true);
        return this;
    }
    
    
    
    @Override
    public Table<R, C, V> addColumn(C key, Line<? extends C, ? extends V> column)
                                              throws IllegalArgumentException, 
                                                     WrongSizeException {
        if (column != null && column.size() != 0)
            checkSize(column.size(), false);
        checkKeyNotDuplicate(key, false);
        insertLine(cols.size(), key, column == null ? null : cellValueIterator(column), 
                   column == null ? 0 : column.size(), false);
        return this;
    }



    @Override
    public Table<R, C, V> insertRow(int index, Collection<? extends V> row)
                                              throws NoSuchElementException, 
                                                     WrongSizeException {
        if (row != null && row.size() != 0)
            checkSize(row.size(), true);
        insertLine(index, null, row == null ? null : row.iterator(), 
                   row == null ? 0 : row.size(), true);
        return this;
    }



    @Override
    public Table<R, C, V> insertColumn(int index, Collection<? extends V> column)
                                              throws NoSuchElementException, 
                                                     WrongSizeException {
        if (column != null && column.size() != 0)
            checkSize(column.size(), false);
        insertLine(index, null, column == null ? null : column.iterator(), 
                   column == null ? 0 : column.size(), false);
        return this;
    }



    @Override
    public Table<R, C, V> insertRow(int index, R key, Collection<? extends V> row) 
                                              throws IllegalArgumentException, 
                                                     NoSuchElementException, 
                                                     WrongSizeException {
        if (row != null && row.size() != 0)
            checkSize(row.size(), true);
        checkKeyNotDuplicate(key, true);
        insertLine(index, key, row == null ? null : row.iterator(), 
                   row == null ? 0 : row.size(), true);
        return this;
    }
        

        
    @Override
    public Table<R, C, V> insertColumn(int index, C key, Collection<? extends V> column) 
                                              throws IllegalArgumentException, 
                                                     NoSuchElementException, 
                                                     WrongSizeException {
        if (column != null && column.size() != 0)
            checkSize(column.size(), false);
        checkKeyNotDuplicate(key, false);
        insertLine(index, key, column == null ? null : column.iterator(), 
                   column == null ? 0 : column.size(), false);
        return this;
    }



    @Override
    public Table<R, C, V> insertRow(int index, R key, Line<? extends R, ? extends V> row)
                                              throws IllegalArgumentException, 
                                                     NoSuchElementException,
                                                     WrongSizeException {
        if (row != null && row.size() != 0)
            checkSize(row.size(), true);
        checkKeyNotDuplicate(key, true);
        insertLine(index, key, row == null ? null : cellValueIterator(row), 
                   row == null ? 0 : row.size(), true);
        return this;
    }    
    

    
    @Override
    public Table<R, C, V> insertColumn(int index, C key, Line<? extends C, ? extends V> column)
                                              throws IllegalArgumentException, 
                                                     NoSuchElementException,
                                                     WrongSizeException {
        if (column != null && column.size() != 0)
            checkSize(column.size(), false);
        checkKeyNotDuplicate(key, false);
        insertLine(index, key, column == null ? null : cellValueIterator(column), 
                   column == null ? 0 : column.size(), false);
        return this;
    }



    @Override
    public List<V> removeRow(R key) throws NoSuchElementException {
        checkKey(key, true);
        int index = indexForKey(key, true);
        return removeAndReturn(index, true);
    }
    
    
    
    @Override
    public List<V> removeColumn(C key) throws NoSuchElementException {
        checkKey(key, false);
        int index = indexForKey(key, false);
        return removeAndReturn(index, false);
    }



    @Override
    public List<V> removeRow(int index) throws NoSuchElementException {
        checkIndex(index, true);            
        return removeAndReturn(index, true);
    }
    
    
    
    @Override
    public List<V> removeColumn(int index) throws NoSuchElementException {
        checkIndex(index, false);
        return removeAndReturn(index, false);
    }



    @Override
    public List<V> removeLastRow() throws NoSuchElementException {
        return removeRow(rows.size() - 1);
    }



    @Override
    public List<V> removeLastColumn() throws NoSuchElementException {
        return removeColumn(cols.size() - 1);
    }



    @Override
    public List<V> removeFirstRow() throws NoSuchElementException {
        return removeRow(0);
    }
    
    
    
    @Override
    public List<V> removeFirstColumn() throws NoSuchElementException {
        return removeColumn(0);
    }



    @Override
    public boolean containsRow(R key) {
        return key != null && rowMap != null && rowMap.containsKey(key);
    }



    @Override
    public boolean containsColumn(C key) {
        return key != null && colMap != null && colMap.containsKey(key);
    }



    @Override
    public int getRowIndex(R key) {
        return indexForKey(key, true);
    }



    @Override
    public int getColumnIndex(C key) {
        return indexForKey(key, false);
    }



    @Override
    public R getRowKey(int index) throws NoSuchElementException {
        checkIndex(index, true);
        return rows.get(index).key;
    }



    @Override
    public C getColumnKey(int index) throws NoSuchElementException {
        checkIndex(index, false);
        return cols.get(index).key;
    }



    @Override
    public R setRowKey(int index, R key) throws NoSuchElementException, 
                                                IllegalArgumentException {
        checkIndex(index, true);
        LinkedLine<R, V> row = rows.get(index);
        if (Objects.equals(row.key, key))
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
    public C setColumnKey(int index, C key) throws NoSuchElementException {
        checkIndex(index, false);
        LinkedLine<C, V> col = cols.get(index);
        if (Objects.equals(col.key, key))
            return key;
        checkKeyNotDuplicate(key, false);
        C oldKey = col.key;
        col.key = key;
        if (colMap != null)
            colMap.remove(oldKey);
        getColMap().put(key, col);
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
        if (newKey != null)
            rowMap.put(newKey, row);
    }
    
    

    @Override
    public void setColumnKey(C oldKey, C newKey) throws NoSuchElementException {
        checkKey(oldKey, false);
        if (oldKey.equals(newKey))
            return;
        LinkedLine<C, V> col = colMap.get(oldKey);
        colMap.remove(oldKey);
        col.key = newKey;
        if (newKey != null)
            colMap.put(newKey, col);
    }
    
    
    
    
    // random access methods
    
    
    @Override
    public Cell<V> getCell(int row, int column) throws NoSuchElementException {
        checkIndex(row, true);
        checkIndex(column, false);
        // TODO implement
        throw new UnsupportedOperationException();
    }



    @Override
    public Cell<V> getCell(R rowKey, C columnKey) throws NoSuchElementException {
        checkKey(rowKey, true);
        checkKey(columnKey, false);
        // TODO implement
        throw new UnsupportedOperationException();
    }
    
    
    
    
    // inner checking methods
    
    
    void checkKeyNotDuplicate(Object key, boolean isRow) 
    		                                  throws IllegalArgumentException {
        if (key == null)
            return;
        if ((isRow && rowMap != null && rowMap.containsKey(key)) 
                || (!isRow && colMap != null && colMap.containsKey(key))) 
            throw new IllegalArgumentException("Key {" + key + "} already exists");
    }
    
    
    void checkSize(int size, boolean isRow) throws WrongSizeException {
        if (isRow && cols.size() > 0 && size != cols.size())
            throw new WrongSizeException(cols.size(), size);
        if (!isRow && rows.size() > 0 && size != rows.size())
            throw new WrongSizeException(rows.size(), size);
    }
    
    
    
    // inserts row or column in given position and assigns a key
    // Cost is always O(k + m)
    
    @SuppressWarnings("unchecked")
    void insertLine(int index, Object key, Iterator<? extends V> line, 
    		        int lineSize, boolean isRow) {
        
        boolean lineEmpty = (line == null || lineSize == 0);
        boolean tableEmpty = rows.size() == 0;        
        
        if (tableEmpty && lineEmpty)    // empty line is inserted into empty table
            return;
        
        // adjust index
        if (isRow && index > rows.size())
            index = rows.size();
        if (!isRow && index > cols.size())
            index = cols.size();
        
        int size = lineEmpty ? (isRow ? cols.size() : rows.size()) : lineSize;
        
        // new line
        LinkedLine<?, V> newLine = new LinkedLine<>(isRow, null, null, key, index, this);
        
        LinkedCell<V> prev = null;    // previous cell
        LinkedCell<V> right;          // cell right to the current as seen in direction of traversal
        LinkedCell<V> left;           // cell left to the current
        
        boolean lineFirst = false;    // will inserted line be the first?
        boolean lineLast = false;     // will it be the last?
        
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
        Iterator<? extends V> iv = lineEmpty ? null : line;
        
        // main loop
        for (int i = 0; i < size; i++) {
            V value = iv == null ? null : iv.next();            
            LinkedCell<V> c = isRow ? new LinkedCell<>(left, right, prev, null, value)
                                    : new LinkedCell<>(prev, null, right, left, value);
            if (isRow) {
                if (prev != null)
                    prev.r = c;
                if (right != null)
                    right.u = c;
                if (left != null)
                    left.d = c;
                left = left == null ? null : left.r;
                right = right == null ? null : right.r;
                prev = c;
            } else {
                if (prev != null)
                    prev.d = c;
                if (right != null)
                    right.r = c;
                if (left != null)
                    left.f = c;
                left = left == null ? null : left.d;
                right = right == null ? null : right.d;
                prev = c;
            }
            
            if (i == 0)
                newLine.first = c;
            if (i == size - 1)
                newLine.last = c;
            
            if (tableEmpty) {                  // line is inserted into empty table
                if (isRow)
                    cols.add(new LinkedLine<>(false, c, c, null, i, this));
                else
                    rows.add(new LinkedLine<>(true, c, c, null, i, this));
                
            } else if (lineFirst) {            // reassign first cell
                if (isRow)
                    cols.get(i).first = c;
                else
                    rows.get(i).first = c;
                
            } else if (lineLast) {             // reassign last cell
                if (isRow)
                    cols.get(i).last = c;
                else
                    rows.get(i).last = c;
            }
                
        }
        
        // add row/column to main list
        if (isRow) {
            if (!lineLast)
                rows.add(index, (LinkedLine<R, V>) newLine);
            else
                rows.add((LinkedLine<R, V>) newLine);
        } else {
            if (!lineLast)
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
    
    
    
    // Removes row or column by given index and removes key association 
    // Cost is always O(k + m)
    
    List<V> removeAndReturn(int index, boolean isRow) {
        
        List<V> old = new ArrayList<>(isRow ? cols.size() : rows.size());
        
        boolean first = index == 0;
        boolean last = index == ((isRow ? rows.size() : cols.size()) - 1);
        LinkedLine<?, V> line = isRow ? rows.get(index) : cols.get(index);
        
        // assign size and detach from table
        line.size = isRow ? cols.size() : rows.size();
        line.table = null;                   
        
        LinkedCell<V> c = line.first;
        
        if (first && last) {                  // only one line exists
            while (c != null) {
                old.add(c.get());
                c = isRow ? c.r : c.d;    
            }
            rows.clear();
            cols.clear();
            rowMap.clear();
            colMap.clear();
            
        } else {                              // more than one line exist
            for (int i = 0; i < (isRow ? cols.size() : rows.size()); i++) {        
                old.add(c.get());
                
                if (isRow) {
                    if (first) 
                        cols.get(i).first = c.d;
                    if (last) 
                        cols.get(i).last = c.u;
                    if (c.u != null)          // relink
                        c.u.d = c.d;
                    if (c.d != null)
                        c.d.u = c.u;
                    c.d = c.u = null;         // clear side links
                    c = c.r;
                    
                } else {
                    if (first)
                        rows.get(i).first = c.r;
                    if (last)
                        rows.get(i).last = c.f;
                    if (c.f != null)          // relink
                        c.f.r = c.r;
                    if (c.r != null)
                        c.r.f = c.f;
                    c.r = c.f = null;         // clear side links
                    c = c.d;
                }
            }
            
            if (isRow)
                rows.remove(index);
            else
                cols.remove(index);
        }
        
        // remove key association
        if (isRow) {
            if (rowMap != null && line.key != null)
                rowMap.remove(line.key);
        } else {
            if (colMap != null && line.key != null)
                colMap.remove(line.key);
        }
        
        return old;
    }
    
    
    
    // Search for index by provided key. 
    // Cost: O(1) if lines weren't rearranged by insertion or deletion
    //       O(k)/O(m) if rows/columns were rearranged
    
    int indexForKey(Object key, boolean isRow) {
        LinkedLine<?, V> line = isRow ? rowMap.get(key) : colMap.get(key);
        if (line == null)
            return -1;
        
        int size = (isRow ? rows.size() : cols.size());
        int ir = isRow ? Math.min(rowMap.get(key).weakIndex, size - 1) 
                       : Math.min(colMap.get(key).weakIndex, size - 1);
        
        int il = ir - 1;
        while (il >= 0 || ir < size) {        // search in both directions
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
    
    
    
    
    // key map lazy initializers 
    
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
    
    

    
    // ================== nested classes ==================== //
    
    
    
    //enum LineType implements Serializable { ROW, COLUMN };
    
    
    /** Represents a single row or column */
    private static class LinkedLine<K, V> extends AbstractLine<K, V> 
                                  implements Serializable {
        
        private static final long serialVersionUID = -2644441702340616775L;
        
        boolean isRow;
        LinkedCell<V> first;
        LinkedCell<V> last;
        K key;
        int weakIndex;
        LinkedTable<?, ?, V> table;
        int size = -1;              // to assign only when line is detached
        
        
        LinkedLine(boolean isRow, LinkedCell<V> first, LinkedCell<V> last, 
        		   K key, int weakIndex, LinkedTable<?, ?, V> table) {
            this.first = first;
            this.last = last;
            this.isRow = isRow;
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
            LinkedCell<V> c = first;
            List<V> res = new ArrayList<>();
            while (c != null) {
                res.add(c.get());
                c = isRow ? c.r : c.d;
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
            LinkedCell<V> c = (LinkedCell<V>) firstCell();
            for (int i = 0; i < index; i++) {
                if (isRow ? c.r == null : c.d == null) 
                    throw new NoSuchElementException();
                else 
                    c = isRow ? c.r : c.d;
            }
            return c;
        }
        
        
        @Override
        public String toString() {
            StringBuilder sb =  new StringBuilder(isRow ? "R[" : "C[");
            LinkedCell<V> c = first;
            while (c != null) {
                sb.append(c.value).append(',').append(' ');
                c = isRow ? c.r : c.d;
            }
            if (sb.length() > 2)
                sb.delete(sb.length() - 2, sb.length());
            return sb.append(']').toString();
        }

        
        @Override
        public int size() {
        	if (table == null)
        		return size;
        	else
        		return isRow ? table.cols.size() : table.rows.size();
        }
        

        Iterator<Cell<V>> iterator(boolean reversed) {
            return new Iterator<Cell<V>>() {
                LinkedCell<V> c = reversed ? last : first;
                
                @Override
                public boolean hasNext() {
                    return c != null;
                }

                @Override
                public Cell<V> next() {
                    if (c == null)
                        throw new NoSuchElementException();
                    LinkedCell<V> cc = c;
                    if (isRow) 
                        c = reversed ? c.f : c.r;
                    else
                        c = reversed ? c.u : c.d;
                    return cc;
                }
            };
        }
    }
    
    

    /** ----------------- LinkedCell class ------------------ **/
    
    
    private static class LinkedCell<T> implements Cell<T>, Serializable {
        
        private static final long serialVersionUID = -1363494083583791405L;
        
        static int idCounter = 0;
        int id;            // cell ID
        
        LinkedCell<T> u;  // upper
        LinkedCell<T> d;  // lower
        LinkedCell<T> f;  // left
        LinkedCell<T> r;  // right
        
        T value;
        
        LinkedCell(LinkedCell<T> u, LinkedCell<T> d, 
        		   LinkedCell<T> f, LinkedCell<T> r, T value) {
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
            return String.format("Cell [%03d]: u=%03d, d=%03d, f=%03d, r=%03d, value=%s", 
            		              id, 
                                  (u == null ? 0 : u.id), (d == null ? 0 : d.id), 
                                  (f == null ? 0 : f.id), (r == null ? 0 : r.id),
                                  value
                                  );
        }
    }    
}
