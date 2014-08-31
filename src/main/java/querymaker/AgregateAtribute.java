package querymaker;

//trieda reprezentujuca strukturu pre ulozenie dat agregacnej funkcie
public class AgregateAtribute {

	private String function;
	private String optionalFunctionParameter;
	private String entity;
	private String entityAtribute;
	private String alias;

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getOptionalFunctionParameter() {
		return optionalFunctionParameter;
	}

	public void setOptionalFunctionParameter(String optionalFunctionParameter) {
		this.optionalFunctionParameter = optionalFunctionParameter;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEntityAtribute() {
		return entityAtribute;
	}

	public void setEntityAtribute(String entityAtribute) {
		this.entityAtribute = entityAtribute;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public AgregateAtribute(String function, String optionalFunctionParameter,
			String entity, String entityAtribute, String alias) {
		this.function = function;
		this.optionalFunctionParameter = optionalFunctionParameter;
		this.entity = entity;
		this.entityAtribute = entityAtribute;
		this.alias = alias;
	}

	public AgregateAtribute(String function) {
		this.function = function;
		this.optionalFunctionParameter = "";
	}

}
