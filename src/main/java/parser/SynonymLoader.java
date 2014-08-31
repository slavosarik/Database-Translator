package parser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import xmlLoader.Wrapper;

//trieda, ktorou nacitavam vsetky synonyma z XML suboru
public class SynonymLoader {

	private static SynonymLoader instance;
	private HashMap<String, String> map;

	// private constructor - singleton
	private SynonymLoader() {
		loadSynonyms();
	}

	// singleton getInstance() method
	public static SynonymLoader getInstance() {
		if (instance == null)
			instance = new SynonymLoader();
		return instance;
	}

	// vytvorenie hash mapy z nacitanych synonym
	private void createHashMap(List<Synonym> list) {
		map = new HashMap<String, String>();

		for (Synonym s : list) {
			map.put(s.getSynonymword(), s.getKeyword());
		}
	}

	// vyhladavanie synonym v hash mape
	public String findKeywordSynonym(String word) {
		return map.get(word);
	}

	// citanie synonym a obalovanie do wrappera
	@SuppressWarnings("unchecked")
	private void loadSynonyms() {
		JAXBContext jc = null;
		try {
			jc = JAXBContext.newInstance(Wrapper.class, Synonym.class);
		} catch (JAXBException e2) {
			e2.printStackTrace();
		}

		String xmlLocation = "/config/synonyms.xml";

		// create stream from xml file
		InputStream is = this.getClass().getResourceAsStream(xmlLocation);
		StreamSource xml = new StreamSource(is);

		// Unmarshal
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		List<Synonym> synonymList = null;
		try {
			synonymList = unmarshaller.unmarshal(xml, Wrapper.class).getValue()
					.getItems();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		createHashMap(synonymList);

	}

}
