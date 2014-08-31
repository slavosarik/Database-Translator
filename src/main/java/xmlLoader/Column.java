package xmlLoader;

import javax.xml.bind.annotation.XmlRootElement;

//trieda reprezentujuca atributy(stlpce) danej entity(tabulky)
@XmlRootElement
public class Column {

	private String columnName;
	private String label;
	private String tag;

	public Column() {
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
