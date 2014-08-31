package querymaker;

//trieda reprezentujuca strukturu pre ulozenie informacie, ci bol atribut uz pouzity pocas ziskavania informacii z tokenov
public class UsedAtribute {

	private String entityName;
	private String atributeName;

	public UsedAtribute(String entityName, String atributeName) {
		this.setEntityName(entityName);
		this.setAtributeName(atributeName);

	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getAtributeName() {
		return atributeName;
	}

	public void setAtributeName(String atributeName) {
		this.atributeName = atributeName;
	}

}
