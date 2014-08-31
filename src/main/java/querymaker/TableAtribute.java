package querymaker;

//trieda - struktura, ktora uchovava informacie o kazdej tabulke
public class TableAtribute {

	private String name;
	private String tableName;
	private String label;
	private String tag;

	public TableAtribute(String tableName, String name, String label, String tag) {
		this.tableName = tableName;
		this.name = name;
		this.label = label;
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTable() {
		return tableName;
	}

	public void setTable(String table) {
		this.tableName = table;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
