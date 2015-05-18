package ru.salauyou.table;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Line<K, V> extends Iterable<Cell<V>> {
	
	//public K getKey();
	
	public int size();
	
	public Cell<V> firstCell() throws NoSuchElementException;
	
	public Cell<V> lastCell() throws NoSuchElementException;
	
	public Cell<V> getCell(int index) throws NoSuchElementException;

	
	/* -------------- iterators over cells ------------------- */
	
	/**
	 * Default iterator
	 */
	public Iterable<Cell<V>> cells();
	
	public Iterable<Cell<V>> cells(boolean reversed);
	
	public default Stream<Cell<V>> stream() {
		return StreamSupport.stream(cells().spliterator(), false);
	}
	
	
	/* ---------------------- converters -------------------------- */
	
	public List<V> toList();
	
}
