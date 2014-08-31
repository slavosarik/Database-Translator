package parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;

//trieda pre pripojenie na server text.fiit.stuba.sk a ziskavanie lemy pre dane slova vety
public class Lematizer {

	private Logger logger = Logger.getLogger(Lematizer.class);

	// vytvorenie http spojenia
	private HttpURLConnection createHttpURLConnection(String query) {

		final String webServiceAdress = "http://text.fiit.stuba.sk:8080/lematizer/services/lemmatizer/lemmatize/fast";

		URL url = null;
		try {
			url = new URL(webServiceAdress);
		} catch (MalformedURLException e) {
			logger.error(
					"MalformedURLException: Failed to create URL webservice link",
					e);
		}

		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			logger.error("IOException: Failed to open Connection", e);
		}

		try {
			// reuqest header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "text/plain");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setDoOutput(true);
		} catch (ProtocolException e) {
			logger.error(
					"ProtocolException: Error while setting request header", e);
		}

		try {
			// Send post request
			logger.info("Sending POST request to URL: " + webServiceAdress);
			logger.info("Sending POST request - query: " + query);
			connection.getOutputStream().write(query.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException: Sending POST (query: "
					+ query + ")", e);
		} catch (IOException e) {
			logger.error("IOException: Sending POST (query: " + query + ")", e);
		}

		return connection;

	}

	// ziskavanie lemy pre danu vetu/slovo - parsovanie odpovede z http get
	// requestu
	public String lematize(String input) throws NullPointerException {

		InputStream is = null;
		try {
			logger.info("Opening inpustream - lematizer");
			is = createHttpURLConnection(input).getInputStream();
		} catch (IOException e) {

			logger.error("IOException: Opening inpustream - lematizer", e);
		}

		Document doc = null;
		try {
			logger.info("Opening document for parsing");
			doc = Jsoup.parse(is, "UTF-8", "");
		} catch (NullPointerException e) {
			logger.error("NullPointerException: Opening document for parsing",
					e);
			throw new NullPointerException();
		}

		catch (IOException e) {
			logger.error("IOException: Opening document for parsing", e);
		}

		doc.outputSettings().escapeMode(EscapeMode.xhtml);

		String response = doc.select("body").text();

		return response;
	}

}
