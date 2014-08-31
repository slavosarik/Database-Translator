package parser;

import gui.MainWindow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.google.common.collect.Iterables;

public class Analyzer {

	private Logger logger = Logger.getLogger(Analyzer.class);

	// vratis lovny druh pre tag
	public String analyzeTag(String tag) {

		// zdroj dat: http://korpus.sk/morpho.html

		if (tag == null)
			return "empty tag";

		switch (tag.charAt(0)) {
		case 'S':
			return "podstatnÈ meno";
		case 'A':
			return "prÌdavnÈ meno";
		case 'P':
			return "z·meno";
		case 'V':
			return "sloveso";
		case 'G':
			return "prÌËastie";
		case 'N':
			return "ËÌslovka";
		case 'D':
			return "prÌslovka";
		case 'E':
			return "predloûka";
		case 'T':
			return "Ëastica";
		case 'O':
			return "spojka";
		case 'X':
			return "vlastÈ meno";
			// vlastne definovane
		case 'n':
			return "ËÌslo";
		case 'd':
			return "d·tum";
		default:
			return "wrong tag";
		}

	}

	// rozseknutie vety na slova a vytvorenie zoznamu slov
	private List<Word> splitSentence(String inputSentence) {

		String sentence = inputSentence.replaceAll("[!?,.]", "");
		String[] words = sentence.split("\\s+");

		List<Word> wordList = new ArrayList<>();

		for (int i = 0; i < words.length; i++) {
			wordList.add(new Word(words[i]));

		}

		return wordList;

	}

	// analyza vety, najdenie lemy, tagu pre kazde slovo a vytvorenie zoznamu
	// otagovanych slov
	public List<Word> analyze(String input) {

		Lematizer lematizer;
		String lematizedSentence = null;
		List<Word> words = new ArrayList<>();
		String[] lematizedWords;
		Collection<Word> foundWord;
		int count = 0;
		String string = input;
		int first = 0, last = 0;
		String substring = null;

		lematizer = new Lematizer();

		input = Character.toLowerCase(input.charAt(0)) + input.substring(1);

		System.out.println("Vstup pre analyzu:" + input);

		while (true) {

			// hladam zaciatok uvodzoviek
			// last == pozicia uvodzoviek
			// last = index ukoncovacich uvodzoviek
			last = string.indexOf("\"", first);
			if (last == -1)
				// ak sa tam uvodzovky nenachadzaju, tak sa zoberie cely text do
				// substringu
				if (first > input.length())
					substring = "";
				else
					substring = (String) input.subSequence(first,
							input.length());
			else
				substring = (String) input.subSequence(first, last);

			// pozeram, ci substring je prazdny
			if (substring.length() == 0) {
				if (last == -1) {
					break;
				}
			} else {
				// substring nieco obsahuje, tak ideme to analyzovat
				// count je priznak, ci v substringu sa nachadza veta alebo
				// keyword do DB tabulky

				// parny count znamena vetu

				if (count % 2 == 0) {
					List<Word> pom = splitSentence(substring);

					// ziskavanie lemy zo substringu

					try {
						lematizedSentence = lematizer.lematize(substring);
					} catch (NullPointerException e) {

						logger.error(
								"NullPointerException: Failed to connect to web",
								e);

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {

								// chybova hlaska a ukoncenie otagovania
								JOptionPane.showMessageDialog(
										MainWindow.getParentFrame(),
										"Nie je moûnÈ sa pripojiù na web.",
										"Internet connection error",
										JOptionPane.ERROR_MESSAGE);
							}
						});

						return null;

					}

					System.out.println("lematized sentence: "
							+ lematizedSentence);
					lematizedWords = lematizedSentence.split("\\s+");

					bugFixLemaWords(lematizedWords);

					// zapis otagovanych slov do zoznamu words
					for (Word w : pom) {

						// ak to je nejaky nazov, tak ho oznacim ako vlastne
						// meno
						if (Character.isUpperCase(w.getSourceWord().charAt(0))) {
							w.setTag("X");
						} else {

							w.setLema(lematizedWords[pom.indexOf(w)]);
							foundWord = MorfoDB.getInstance().searchWord(
									w.getSourceWord());
							// Word w = Iterables.getLast(words);
							System.out.println("DEBUG: " + w.getSourceWord());
							if (foundWord != null && foundWord.isEmpty() == false) {
								w.setTag(Iterables.getLast(foundWord).getTag());
							} else {
								foundWord = MorfoDB.getInstance().searchWord(
										w.getSourceWord().toLowerCase());
								if (foundWord != null && foundWord.isEmpty() == false) {
									// snazim sa fixnut ci pomoze, ked vymenim
									// nenajdene povodne slovo za to co naslo
									Word word = Iterables.getLast(foundWord);
									w.setSourceWord(word.getSourceWord());
									w.setTag(word.getTag());
								} else
									w.setTag(null);
							}
						}
					}
					words.addAll(pom);

				}

				else {
					// neparny count znamena viacslovne slovo s uvodzovkami
					Word w = new Word(substring);
					// X oznacuje vlastne meno
					w.setTag("X");

					words.add(w);
				}

			}

			// ak uz vo zvysku neostala ziadne slovo oznacujuce keyword do DB,
			// tak skoncim cyklus

			if (last == -1)
				break;

			first = last + 1;

			if (count % 2 == 1)
				first++;

			count++;
		}

		fixAdjectives(words);
		taggerBugFix(words);

		return words;

	}

	// zistovanie, ci dane slovo vo vete je skutocne cislo
	public boolean isNumber(String input) {

		try {
			@SuppressWarnings("unused")
			double number = Double.parseDouble(input);

		} catch (NumberFormatException nfe) {

			return false;
		} catch (NullPointerException npe) {

			return false;
		}

		return true;
	}

	// opravenie bugu lematizatora
	private void bugFixLemaWords(String[] lematizedWords) {

		for (int i = 0; i < lematizedWords.length; i++) {
			if ("ûltieù".equals(lematizedWords[i]))
				lematizedWords[i] = "ûlt˝";
			if ("Ëervenieù".equals(lematizedWords[i]))
				lematizedWords[i] = "Ëerven˝";
			if ("modrieù".equals(lematizedWords[i]))
				lematizedWords[i] = "modr˝";
			if ("ruûovieù".equals(lematizedWords[i]))
				lematizedWords[i] = "ruûov·";
		}
	}

	// upravenie koncoviek pridavnych mien, aby sa zhodovali s rodmi atributov
	private void fixAdjectives(List<Word> words) {

		for (Word w : words) {
			System.out.println(w.getSourceWord() + "..." + w.getLema() + " .. "
					+ w.getTag());
			if (w.getTag() != null && w.getTag().isEmpty() == false
					&& 'A' == w.getTag().charAt(0)) {

				String pridMeno = w.getLema();

				if (pridMeno.endsWith("y") || pridMeno.endsWith("e"))
					pridMeno = pridMeno.substring(0, pridMeno.length() - 1) + 'a';
				else if (pridMeno.endsWith("˝") || pridMeno.endsWith("È"))
					pridMeno = pridMeno.substring(0, pridMeno.length() - 1) + '·';

				w.setLema(pridMeno);

			}
		}
	}

	// upravenie tagov aby sa zhodovali s nasou domenou - chyba slovenciny
	private void taggerBugFix(List<Word> words) {
		for (Word w : words) {

			if ("najmenej".equals(w.getSourceWord()) == true) {
				w.setLema("najmenej");
				w.setTag("N");
			} else if ("najviac".equals(w.getSourceWord()) == true) {
				w.setLema("najviac");
				w.setTag("N");
			} else if ("viac".equals(w.getSourceWord()) == true) {
				w.setLema("viac");
				w.setTag("N");
			} else if ("menej".equals(w.getSourceWord()) == true) {
				w.setLema("menej");
				w.setTag("N");
			}

			// otagovanie cisel rucne, kedze automaticke otagovanie nezvlada
			// otagovat cisla
			else if (isNumber(w.getLema()))
				w.setTag("n");
		}
	}
}
