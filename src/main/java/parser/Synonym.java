package parser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//trieda pre synonyma - klucove slovo a tvar synonyma
@XmlRootElement
public class Synonym {

	private String keyword;
	private String synonymword;

	public String getKeyword() {
		return keyword;
	}

	@XmlElement
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSynonymword() {
		return synonymword;
	}

	@XmlElement
	public void setSynonymword(String synonymword) {
		this.synonymword = synonymword;
	}

}
