package test.ru.salauyou.linkedtable;

import java.util.Arrays;

import org.junit.Test;

import ru.salauyou.table.Line;
import ru.salauyou.table.Table;
import ru.salauyou.tableimpl.LinkedTable;

public class GenericTableTest {

	@Test
	public void converterTest() {
		/*List<List<String>> list = new ArrayList<>();
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
		System.out.println(tableRows.toRowCellsList());
		System.out.println(tableCopy.toColumnCellsList());
		System.out.println(tableCopy.equals(tableRows) + ", " + tableRows.equals(tableCopy));
		System.out.println(tableCopy.hashCode() + ", " + tableRows.hashCode());
		
		System.out.println(tableCopy.replaceRow(0, tableCopy.getRow(1)));
		System.out.println(tableCopy.toColumnCellsList());
		System.out.println(tableCopy.equals(tableRows) + ", " + tableRows.equals(tableCopy));
		System.out.println(tableCopy.hashCode() + ", " + tableRows.hashCode());*/
		
		Table<String, String, String> t = new LinkedTable<>(3, 4, "-");
		t.insertRow(1, Arrays.asList("1", "2", "3", "4"));
		t.insertColumn(2, Arrays.asList("A", "B", "C", "D"));
		System.out.println(t);
		Line<String, String> removedRow = t.getRow(1);
		System.out.println(removedRow);
		t.removeRow(1);
		System.out.println(t);
		System.out.println(t.toColumnCellsList());
		System.out.println(t.toRowCellsList());
		System.out.println(removedRow);
		t.addRow("restored", removedRow)
			.addRow("restored-2", removedRow)
			.addRow("from-list", Arrays.asList("T", "T", "T", "T", "T"));
		System.out.println(t);
		System.out.println(t.toRowCellsMap());
		System.out.println(t.toColumnCellsList());
	}
	
	
}
