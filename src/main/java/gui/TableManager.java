package gui;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

//trieda, ktora naplna JTable udajmi z ResultSetu databazoveho dopytu
public class TableManager {

	private static TableManager ref;
	private Vector<String> columnNames = null;

	private TableManager() {
	}

	public static TableManager getSingletonObject() {
		if (ref == null)
			ref = new TableManager();
		return ref;
	}

	// vytvorenie tablemodelu podla rozlozeniu udajov v stlpcoch tabulky
	public DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();

		columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
		}

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (rs.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(rs.getObject(columnIndex));
			}
			data.add(vector);
		}

		rs.beforeFirst();

		return new DefaultTableModel(data, columnNames);

	}

	// vycentrovanie udajov v tabulke
	public void setTableAlignment(JTable table) {
		JTableHeader header = table.getTableHeader();
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) table
				.getTableHeader().getDefaultRenderer();
		header.setDefaultRenderer(renderer);
		renderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		int rowNumber = table.getColumnCount();
		for (int i = 0; i < rowNumber; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setEnabled(false);
	}

	// naplnenie udajmi a nastavenie centrovania tabulky
	public void updateTable(JTable table, ResultSet rs) throws SQLException {
		table.setModel(buildTableModel(rs));
		setTableAlignment(table);
	}
}
