package xmlLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

//trieda pomocou ktorej nacitavam tabulky a ich parametre a atributy - z XML suboru
public class TableLoader {

	private final String xmlFileLocation = "/config/tables.xml";
	private static TableLoader instance;
	private List<Table> tables;
	private StreamSource xmlStream;

	private TableLoader() {
	}

	public static TableLoader getInstance() {
		if (instance == null)
			instance = new TableLoader();

		return instance;
	}

	// otvorenie streamu
	private StreamSource openXmlStream(String FILE) {

		InputStream is = this.getClass().getResourceAsStream(FILE);
		xmlStream = new StreamSource(is);

		return xmlStream;

	}

	// zatvorenie streamu
	private void closeXmlStream(StreamSource xmlStream) {

		try {
			xmlStream.getInputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// nacitavanie objektov zo suboru a obalovanie do wrapperu
	@SuppressWarnings("unchecked")
	private Wrapper<Table> fillWrapper(StreamSource xmlStream) {

		JAXBContext jaxbContext = null;

		try {
			jaxbContext = JAXBContext.newInstance(Wrapper.class, Table.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		try {
			return jaxbUnmarshaller.unmarshal(xmlStream, Wrapper.class)
					.getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
			// v pripade zlyhania vratim prazdny wrapper
			return null;
		}

	}

	// nacitavanie objektov - tabuliek zo suboru
	public void loadTables() {

		// obalim do Wrapperu vsetky objekty z XML suboru
		Wrapper<Table> JAXBWrapper = fillWrapper(openXmlStream(xmlFileLocation));

		tables = JAXBWrapper.getItems();

		closeXmlStream(xmlStream);

	}

	public List<Table> getTables() {
		return tables;
	}

}
