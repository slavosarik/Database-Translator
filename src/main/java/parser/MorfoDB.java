package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

//nacitavanie morfologickeho slovnika - obsahuje slova, zakladne tvary a tiez aj tagy
public class MorfoDB {

	private static Logger logger = Logger.getLogger(MorfoDB.class);
	private static MorfoDB morfoInstance;
	private static Multimap<String, Word> morfoMap;

	private String[] morfoDbFiles = { "/database/morfo_db_part1.txt",
			"/database/morfo_db_part2.txt", "/database/morfo_db_part3.txt" };

	// private constructor - singleton
	private MorfoDB() {
		for (String FILE : morfoDbFiles) {
			InputStream in = openFile(FILE);
			createHaspMap(in);
			closeFile(in);
		}
	}

	// singleton getInstance() method
	public static MorfoDB getInstance() {
		if (morfoInstance == null)
			morfoInstance = new MorfoDB();

		return morfoInstance;
	}

	// otvorenie suboru a streamu
	private InputStream openFile(String FILE) {
		InputStream in = null;

		in = this.getClass().getResourceAsStream(FILE);

		return in;
	}

	// zatvorenie streamu
	private void closeFile(InputStream in) {

		// closing inputstream
		try {
			in.close();
		} catch (IOException e) {
			logger.error("IOException - Failed to close file stream", e);
		}

	}

	// citanie suboru, vytvorenie hash mapy a ulozenie vsetkych slov do mapy
	private void createHaspMap(InputStream in) {

		// constant used as index in word
		final int LEMA = 0;
		final int SOURCE_WORD = 1;
		final int TAG = 2;

		// open bufferedreader
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in, "UTF8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("UnsupportedEncodingException", e);
		}

		String line;
		Word word;
		morfoMap = ArrayListMultimap.create();

		try {
			// loop to read and put word to hashmap
			while ((line = br.readLine()) != null) {
				String[] words = line.split("	");
				word = new Word(words[LEMA], words[SOURCE_WORD], words[TAG]);
				morfoMap.put(words[SOURCE_WORD], word);
			}

		} catch (IOException e) {
			logger.error("IOException: Failed to read file", e);
		}
	}

	// vyhladavanie slov v slovniku - hash mape
	public Collection<Word> searchWord(String input) {
		System.out.println(input);
		Collection<Word> words = morfoMap.get(input);

		return words;

	}

}
