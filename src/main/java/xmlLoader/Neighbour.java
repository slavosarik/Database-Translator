package xmlLoader;

import javax.xml.bind.annotation.XmlRootElement;

//trieda pre uchovavanie susednych tabuliek - entit s informaciou, pomocou akeho cudzieho kluca sa napaja na hlavnu entitu
@XmlRootElement
public class Neighbour {

	private String target;
	private String targetkey;
	private String sourcekey;

	public Neighbour() {

	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getTargetkey() {
		return targetkey;
	}

	public void setTargetkey(String targetkey) {
		this.targetkey = targetkey;
	}

	public String getSourcekey() {
		return sourcekey;
	}

	public void setSourcekey(String sourcekey) {
		this.sourcekey = sourcekey;
	}

}
