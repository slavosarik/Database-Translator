package querymaker;

//trieda reprezentujuca strukturu pre ulozenie dat having klauzuly
public class HavingAtribute {

	private String atributeName;
	private String operator;
	private String atributeValue;

	public HavingAtribute(String atributeName, String operator,
			String atributeValue) {
		this.setAtributeName(atributeName);
		this.setOperator(operator);
		this.setAtributeValue(atributeValue);

	}

	public String getAtributeName() {
		return atributeName;
	}

	public void setAtributeName(String atributeName) {
		this.atributeName = atributeName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getAtributeValue() {
		return atributeValue;
	}

	public void setAtributeValue(String atributeValue) {
		this.atributeValue = atributeValue;
	}

}
