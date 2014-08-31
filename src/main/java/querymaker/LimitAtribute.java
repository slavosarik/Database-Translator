package querymaker;

//trieda reprezentujuca strukturu pre ulozenie dat limit klauzuly
public class LimitAtribute {

	private String count = "1";

	public LimitAtribute(String count) {
		this.setCount(count);
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

}
