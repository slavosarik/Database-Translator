package querymaker;

//trieda reprezentujuca strukturu pre ulozenie dat order by klauzuly
public class OrderAtribute {

	private String entity;
	private String atribute;
	private String sortDirection;

	public OrderAtribute(String entity, String atribute, String sortDirection) {
		this.setEntity(entity);
		this.setAtribute(atribute);
		this.setSortDirection(sortDirection);
	}

	public OrderAtribute(String atribute, String sortDirection) {
		this.setAtribute(atribute);
		this.setSortDirection(sortDirection);
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getAtribute() {
		return atribute;
	}

	public void setAtribute(String atribute) {
		this.atribute = atribute;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

}
