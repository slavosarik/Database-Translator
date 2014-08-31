package parser;

//trieda reprezentujuca dane slovo vratane jeho atributov a vlastnosti
public class Word {

	private String sourceWord;
	private String tag;
	private String lema;
	private boolean used = false;

	public Word(String sourceWord) {
		this.sourceWord = sourceWord;
	}

	public Word(String lema, String sourceWord, String tag) {
		this.sourceWord = sourceWord;
		this.lema = lema;
		this.sourceWord = sourceWord;
		this.tag = tag;
	}

	public String getSourceWord() {
		return sourceWord;
	}

	public void setSourceWord(String sourceWord) {
		this.sourceWord = sourceWord;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getLema() {
		return lema;
	}

	public void setLema(String lema) {
		this.lema = lema;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

}
