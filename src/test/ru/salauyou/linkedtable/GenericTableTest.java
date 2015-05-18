package test.ru.salauyou.linkedtable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.*;

import org.junit.Test;

import ru.salauyou.linkedtable.LinkedTable;
import ru.salauyou.table.Table;

public class GenericTableTest {

	@Test
	public void converterTest() {
		List<List<String>> list = new ArrayList<>();
		list.add(Arrays.asList("A", "B", "C"));
		list.add(Arrays.asList("D", "E", "F"));
		list.add(Arrays.asList("G", "H", "I"));
		
		Table<String, String, String> tableRows = Table.fromRowCells(LinkedTable::new, list);
		List<List<String>> fromTable = tableRows.toRowCellsList();
		tableRows.rowStream().forEach(r -> r.stream().forEach(c -> System.out.println(c + " : " + c.get())));
		tableRows.cellStream().forEach(c -> System.out.println(c + " = " + c.get()));
		assertEquals(list, fromTable);
		System.out.println(list);
		System.out.println(tableRows);
		
		tableRows.setRowKey(0, "First");
		tableRows.setRowKey(1, "Second");
		tableRows.setRowKey(2, "Third");
		Table<String, String, String> tableCopy = Table.fromTable(LinkedTable::new, tableRows);
		System.out.println(tableCopy);
		System.out.println(tableCopy.toRowCellsList());
		System.out.println(tableRows.rowKeyStream().collect(toList()));
		System.out.println(tableCopy.rowKeyStream().collect(toList()));
	}
	
	
}
