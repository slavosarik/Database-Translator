package logic;

import gui.DbLoginEditor;
import gui.InfiniteProgressPanel;
import gui.MainWindow;
import gui.TableManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import parser.Analyzer;
import parser.MorfoDB;
import parser.SynonymLoader;
import parser.Word;
import querymaker.QueryCreator;
import querymaker.WhereAtribute;
import database.DatabaseManager;
import database.DbOperator;

public class MainClass {

	private TableManager tableManager;
	private MainWindow mainWindow;
	private DbLoginEditor dbLoginEdit;
	private Analyzer analyzer;
	private SynonymLoader synonymLoader;
	private JTextField inputField;
	private DatabaseManager dbManager;
	private InfiniteProgressPanel glassPane;
	private Logger logger = Logger.getLogger(MainClass.class);

	private String input;
	private String query;

	// uvodna inicializacia
	public void initMain() {

		// konfiguracia loggera
		InputStream is = this.getClass().getResourceAsStream(
				"/properties/log4j.properties");

		PropertyConfigurator.configure(is);

		logger.info("\n\n==============================================================================\nPREBIEHA SPUSTANIE APLIKACIE\n\n");

		mainWindow = new MainWindow();
		dbLoginEdit = new DbLoginEditor();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					mainWindow.getFrame().setVisible(true);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.glassPane = new InfiniteProgressPanel();
		mainWindow.getFrame().setGlassPane(glassPane);

		new Thread(new Runnable() {
			public void run() {

				glassPane.start();

				tableManager = TableManager.getSingletonObject();
				analyzer = new Analyzer();
				synonymLoader = SynonymLoader.getInstance();
				dbManager = DatabaseManager.getInstance();
				MorfoDB.getInstance();

				glassPane.stop();

			}
		}).start();

		inputField = mainWindow.getTextFieldInput();

		addActionsToButtons();

	}

	// hlavna funkcia na vykonavanie a obsluhovanie prekladu
	public void operate() {

		if (input.isEmpty()) {
			logger.info("Nezadany vstup");
			return;
		}

		logger.info("Spracovavam vstup: " + input + "\n");

		// rozdelenie podla uvodzoviek, lema, tag, klucove slovo do DB
		List<Word> wordList = analyzer.analyze(input);

		if (wordList == null)
			return;

		// morfologicka analyza
		for (Word w : wordList) {
			System.out.println(w.getSourceWord() + "--"
					+ analyzer.analyzeTag(w.getTag()));
		}

		// synonymicka analyza
		for (Word w : wordList) {
			if (synonymLoader.findKeywordSynonym(w.getLema()) != null) {
				w.setLema(synonymLoader.findKeywordSynonym(w.getLema()));
			}
		}

		// vypis najdenych a otagovanych slov
		for (Word w : wordList) {
			System.out.println(w.getSourceWord() + " .. " + w.getLema() + ".."
					+ w.getTag());
		}

		// QUERY VYROBA//
		QueryCreator qcreator = new QueryCreator();
		query = qcreator.createQuery(wordList);

		System.out.println("VSTUP: " + input);
		System.out.println("QUERY: " + query + "\n\n");

		if (query == null)
			return;

		// DOPYTOVANIE DB QUERY//
		DbOperator.vytlacRS(dbManager.executeDbQuery(query),
				mainWindow.getTable(), tableManager);

		// zbieranie vlastnosti o dopyte pre nasledny vypis vlastnosti
		final StringBuilder usedUpWord = new StringBuilder();

		for (Word word : wordList) {
			usedUpWord.append(word.getSourceWord());
			usedUpWord.append(" - ");
			if (word.isUsed() == true)
				usedUpWord.append("pouûitÈ slovo");
			else if (word.getTag() == null)
				usedUpWord.append("neidentifikovanÈ slovo");
			else
				usedUpWord.append("nepouûitÈ slovo");
			usedUpWord.append("\n");
		}

		// builder pre vypis vlastnosti query do text area
		final StringBuilder entitiesAndAtributes = new StringBuilder();

		entitiesAndAtributes.append("PouûitÈ tabuæky: \n");
		for (String entity : qcreator.getListOfUsedEntities()) {
			entitiesAndAtributes.append(entity);
			entitiesAndAtributes.append("\n");
		}

		entitiesAndAtributes.append("\nPouûitÈ atrib˙ty: \n");
		for (WhereAtribute whereAtribute : qcreator.getWhereAtributes()) {
			entitiesAndAtributes.append(whereAtribute.getTableName() + ": "
					+ whereAtribute.getAtributeName() + " "
					+ whereAtribute.getOperator() + " "
					+ whereAtribute.getAtributeParameter());
			entitiesAndAtributes.append("\n");
		}

		if (qcreator.getGroupAtribute() != null) {
			entitiesAndAtributes.append("\nGroup by obmedzenia: \n");
			entitiesAndAtributes.append("GROUP BY "
					+ qcreator.getGroupAtribute().getEntity() + "\n");
		}

		// vypisanie vysledku do GUI
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				mainWindow.getBtnAno().setEnabled(true);
				mainWindow.getBtnNie().setEnabled(true);
				mainWindow.getTextArea_1().setText(usedUpWord.toString());
				mainWindow.getTextArea_1().setCaretPosition(0);

				mainWindow.getTextArea_2().setText(
						entitiesAndAtributes.toString());
				mainWindow.getTextArea_2().setCaretPosition(0);

				mainWindow.getTextArea().setText(" SQL Dopyt:\n\n");
				mainWindow.getTextArea().append(query);
				mainWindow.getTextArea().setCaretPosition(0);

			}
		});

	}

	// priradenie akcii k prvkov GUI
	private void addActionsToButtons() {

		mainWindow.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int confirmValue = JOptionPane.showOptionDialog(
						mainWindow.getFrame(), "Chcete ukonËiù program?",
						"Exit Confirmation", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);

				if (confirmValue == 0) {
					dbManager.closeConnection();
					System.exit(0);
				}

			}
		});

		mainWindow.getBtnX().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				inputField.setText("");
			}
		});

		mainWindow.getBtnPrelozit().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				mainWindow.getBtnAno().setEnabled(false);
				mainWindow.getBtnNie().setEnabled(false);
				input = inputField.getText();

				new Thread(new Runnable() {
					public void run() {
						glassPane.start();
						operate();
						glassPane.stop();

					}
				}).start();

			}
		});

		mainWindow.getBtnAno().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				logger.info("ANO - Pouzivatel dostal ocakavany vystup");

				new Thread(new Runnable() {
					public void run() {
						writeTestResults(true);
					}
				}).start();

				mainWindow.getBtnAno().setEnabled(false);
				mainWindow.getBtnNie().setEnabled(false);
			}

		});

		mainWindow.getBtnNie().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				logger.info("NIE - Pouzivatel nedostal ocakavany vystup");

				new Thread(new Runnable() {
					public void run() {
						writeTestResults(false);

					}
				}).start();

				mainWindow.getBtnAno().setEnabled(false);
				mainWindow.getBtnNie().setEnabled(false);

			}
		});

		mainWindow.getMntmSprvaDatabzy().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						dbLoginEdit.getFrame().setVisible(true);
						dbLoginEdit.getFrame().setLocationRelativeTo(
								mainWindow.getFrame());
						mainWindow.getFrame().setVisible(false);

						new Thread(new Runnable() {
							public void run() {
								loadDBProps();
							}
						}).start();

					}
				});

		mainWindow.getMntmAbout().addActionListener(new ActionListener() {

			@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent e) {

				JLabel label = new JLabel();

				String aboutString = "<html><body>Datab·zov˝ prekladaË bol öpeci·lne vyvinut˝ ako s˙Ëasù bakal·rskÈho projektu Vytv·ranie"
						+ " datab·zov˝ch dopytov v prirodzenom jazyku na FIIT STU v Bratislave.<br><br><br>Autor: SlavomÌr ä·rik<br>Ved˙ci pr·ce: Ing. Peter Lacko, PhD.</body></html>";
				label.setText(aboutString);

				JOptionPane.showMessageDialog(mainWindow.getParentFrame(),
						label, "About", JOptionPane.INFORMATION_MESSAGE);

			}
		});

		dbLoginEdit.getBtnUlozit().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new Thread(new Runnable() {
					public void run() {

						try {
							Properties properties = new Properties();

							String dbUrl = new String(dbLoginEdit
									.getTextField_5_DbConnector().getText()
									+ dbLoginEdit.getTextField_3_HostName()
											.getText()
									+ ":"
									+ dbLoginEdit.getTextField_4_Port()
											.getText()
									+ "/"
									+ dbLoginEdit.getTextField_2_DbName()
											.getText());

							properties.setProperty("jdbc.url", dbUrl);
							properties.setProperty("jdbc.username", dbLoginEdit
									.getTextFieldUserName().getText());
							properties.setProperty("jdbc.password", dbLoginEdit
									.getTextField_1_Password().getText());
							File file = new File(
									"properties/database.properties");
							FileOutputStream outStream = new FileOutputStream(
									file);
							properties.store(outStream, null);
							outStream.close();

						} catch (IOException e1) {
							e1.printStackTrace();
						}

					}
				}).start();

				JOptionPane.showMessageDialog(dbLoginEdit.getFrame(),
						"PrÌstupovÈ ˙daje boli uloûenÈ.", "Info",
						JOptionPane.INFORMATION_MESSAGE);

				dbLoginEdit.getFrame().setVisible(false);
				mainWindow.getFrame().setVisible(true);

			}
		});

		dbLoginEdit.getBtnZrusit().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				dbLoginEdit.getFrame().setVisible(false);
				mainWindow.getFrame().setVisible(true);
			}
		});

		mainWindow.getBtnAno().setEnabled(false);
		mainWindow.getBtnNie().setEnabled(false);

	}

	// nacitanie db properties pre okno s nastavenim db prihlasovacich udajov
	private void loadDBProps() {

		String CONFIG_FILE = "properties/database.properties";

		Properties properties = dbManager.loadProperties(CONFIG_FILE);

		// ziskavanie parametrov connection
		final String url = properties.getProperty("jdbc.url");
		final String username = properties.getProperty("jdbc.username");
		final String password = properties.getProperty("jdbc.password");

		final String[] urlParameters = url.split("\\:");
		final String hostPort[] = urlParameters[3].split("/");
		final String hostName[] = urlParameters[2].split("/");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				dbLoginEdit.getTextFieldUserName().setText(username);
				dbLoginEdit.getTextField_1_Password().setText(password);
				dbLoginEdit.getTextField_3_HostName().setText(hostName[2]);
				dbLoginEdit.getTextField_4_Port().setText(hostPort[0]);
				dbLoginEdit.getTextField_2_DbName().setText(hostPort[1]);
				dbLoginEdit.getTextField_5_DbConnector().setText(
						urlParameters[0] + ":" + urlParameters[1] + "://");
			}
		});

	}

	// zapis vysledkov (spatna vazba) do suboru
	private void writeTestResults(boolean success) {
		File file = new File("temp/TestResults.txt");

		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "UTF-8"));

			if (success == true)
				writer.append("¡NO - PouûÌvateæ dostal oËak·van˝ v˝stup.\n");
			else
				writer.append("NIE - PouûÌvateæ nedostal oËak·van˝ v˝stup.\n");

			writer.append("VSTUP:\n" + input + "\n");
			writer.append("V›STUP:\n" + query + "\n");
			writer.append("\n\n\n");
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		// spustenie applikacie
		MainClass mc = new MainClass();
		// inicializacia okna
		mc.initMain();
	}

}