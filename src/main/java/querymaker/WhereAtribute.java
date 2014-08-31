package querymaker;

//trieda  - struktura, ktora urcuje blizsie where atribut - ohranicenie pre dopyt
public class WhereAtribute {

	private String tableName;
	private String atributeName;
	private String atributeParameter;
	private String operator;

	public WhereAtribute(String tableName, String atributeName,
			String operator, String atributeParameter) {
		this.setTableName(tableName);
		this.setAtributeName(atributeName);
		this.setOperator(operator);
		this.setAtributeParameter(atributeParameter);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getAtributeName() {
		return atributeName;
	}

	public void setAtributeName(String atributeName) {
		this.atributeName = atributeName;
	}

	public String getAtributeParameter() {
		return atributeParameter;
	}

	public void setAtributeParameter(String atributeParameter) {
		this.atributeParameter = atributeParameter;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
