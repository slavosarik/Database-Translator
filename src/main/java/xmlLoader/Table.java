package xmlLoader;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

//trieda reprezentujucu tabulku - entitu databazy
@XmlRootElement
@XmlType(propOrder = { "name", "label", "listOfDefaultColumns",
		"listOfColumns", "listOfNeighbours" })
public class Table {

	private String name;
	private String label;
	private ArrayList<Neighbour> listOfNeighbours;
	private ArrayList<Column> listOfColumns;

	public Table() {
	}

	@XmlElementWrapper(name = "defaultColumns")
	@XmlElement(name = "defaultColumn")
	public ArrayList<String> listOfDefaultColumns;

	// toto nie je element pre JAXB mapovanie
	@XmlTransient
	public HashMap<String, Column> columnMap;

	public String getName() {
		return name;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Neighbour> getListOfNeighbours() {
		return listOfNeighbours;
	}

	@XmlElementWrapper(name = "neighbours")
	@XmlElement(name = "neighbour")
	public void setListOfNeighbours(ArrayList<Neighbour> listOfNeighbours) {
		this.listOfNeighbours = listOfNeighbours;
	}

	public ArrayList<Column> getListOfColumns() {
		return listOfColumns;
	}

	@XmlElementWrapper(name = "columns")
	@XmlElement(name = "column")
	public void setListOfColumns(ArrayList<Column> listOfColumns) {
		this.listOfColumns = listOfColumns;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
