package querymaker;

//trieda reprezentujuca strukturu pre ulozenie dat group by klauzuly
public class GroupAtribute {

	private String optionalFunctionParameter;
	private String entity;
	private String entityAtribute;

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

	public GroupAtribute(String optionalFunctionParameter, String entity,
			String entityAtribute) {

		this.optionalFunctionParameter = optionalFunctionParameter;
		this.entity = entity;
		this.entityAtribute = entityAtribute;

	}
}
