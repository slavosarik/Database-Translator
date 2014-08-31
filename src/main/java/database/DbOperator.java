package database;

import gui.TableManager;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JTable;

//trieda zabezpecujuca extrakciu informacii z Resultsetu a naplnajucu tabulku
public class DbOperator {

	public static void vytlacRS(ResultSet rs, JTable table, TableManager tm) {

		//naplnim tabulku datami z resultsetu
		try {
			tm.updateTable(table, rs);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}

		ResultSetMetaData rsmd = null;

		if (rs == null) {
			System.out.println("Prazdny ResultSet - Chybne query?");
			return;
		}

		
		//pomocny vypis obsahu tabulky do konzoly
		
		//nazbieram si udaje o resultsete
		try {
			rsmd = rs.getMetaData();
		} catch (SQLException e1) {			
			e1.printStackTrace();
		}

		//zistujem pocet stlpcov aby som vedel, ako bude cyklus iterovat
		int numberOfColumns = 0;
		try {
			numberOfColumns = rsmd.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//vypisujem nazvy stlpcov
		for (int i = 1; i <= numberOfColumns; i++) {
			if (i > 1)
				System.out.print(",  ");
			String columnName = null;
			try {
				columnName = rsmd.getColumnName(i);
			} catch (SQLException e) {				
				e.printStackTrace();
			}
			System.out.print(columnName);
		}
		System.out.println("");

		//vypisujem obsah resultsetu do konzoly
		try {
			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue);
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		DatabaseManager.getInstance().disconnect();

	}

}
