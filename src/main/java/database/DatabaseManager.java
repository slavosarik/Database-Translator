package database;

import gui.MainWindow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.mysql.jdbc.PreparedStatement;

//Trieda pre spravu a ovladanie databazy, vykonavanie dopytov
public class DatabaseManager {

	// konfiguracne subory pre databazu - username, password a url
	private String CONFIG_FILE = "/properties/database.properties";
	private static DatabaseManager instance;
	private Connection connection;
	private Statement statement;
	private PreparedStatement pStatement;
	private ResultSet resultSet;
	private Logger logger = Logger.getLogger(DatabaseManager.class);

	private DatabaseManager() {
		logger.debug("Vytvara sa instancia DatabaseManager");
	}

	/**
	 * Singleton - vracia instanciu spravcu databazy
	 * 
	 * @return instancia DatabaseManager
	 */
	public static DatabaseManager getInstance() {

		if (instance == null)
			instance = new DatabaseManager();

		return instance;
	}

	/**
	 * Vracia database connection, ak nie je otvorene, tak vytvara nove
	 * connection
	 * 
	 * @return Databse connection
	 */
	private Connection getDBConnection() throws SQLException {

		try {
			if (connection == null) {
				createConnection(CONFIG_FILE);
			}
		} catch (SQLException e) {

			logger.error("Nie je mozne nadviazat spojenie s databazou", e);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					JOptionPane.showMessageDialog(MainWindow.getParentFrame(),
							"Nie je možné nadviazat spojenie s databázou.",
							"Database Error", JOptionPane.ERROR_MESSAGE);
				}
			});

			throw new SQLException();

		}

		return connection;
	}

	/**
	 * Zatvaranie statementov a resultsetov
	 */
	public void disconnect() {

		try {
			if (resultSet != null)
				resultSet.close();

		} catch (SQLException e) {
			logger.error("Chyba pri zatvarani resultsetu", e);

		}

		try {
			if (statement != null)
				statement.close();

		} catch (SQLException e) {
			logger.error("Chyba pri zatvarani statementu", e);

		}

		try {
			if (pStatement != null)
				pStatement.close();

		} catch (SQLException e) {
			logger.error("Chyba pri zatvarani prepared statementu", e);

		}

	}

	/**
	 * Nacitavanie udajov pre pripojenie k databaze
	 * 
	 * @param configFile
	 *            konfiguracny subor
	 * @return udaje
	 */
	public Properties loadProperties(String configFile) {
		Properties properties = new Properties();
		InputStream in = null;

		in = this.getClass().getResourceAsStream(configFile);

		// citanie z konfiguracneho suboru
		try {
			properties.load(in);

		} catch (IOException e) {
			logger.error("Chyba IO pri citani konfiguracneho suboru", e);

		}

		// zatavranie konfiguracneho suboru
		try {
			in.close();

		} catch (IOException e) {
			logger.error("Chyba pri zatvarani konfiguracneho suboru", e);

		}

		return properties;
	}

	/**
	 * Pripajanie sa k databaze, vytvaranie connection
	 * 
	 * @param configFile
	 *            konfiguracny subor
	 * @throws SQLException
	 */
	private void createConnection(String configFile) throws SQLException {

		Properties properties = loadProperties(configFile);

		// ziskavanie parametrov connection
		String url = properties.getProperty("jdbc.url");
		String username = properties.getProperty("jdbc.username");
		String password = properties.getProperty("jdbc.password");

		logger.info("Vytvaram spojenie s databazou");

		// pripajanie sa k databaze
		connection = DriverManager.getConnection(url, username, password);
		connection.setAutoCommit(false);

		logger.info("User: " + username + ", Pripojene k: " + url);

	}

	/**
	 * Zatvorenie spojenia s databazou
	 */
	public void closeConnection() {

		// odpojenie statementov a resulsetu
		disconnect();

		// uzatvaranie spojenia
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
			logger.info("Database connection bolo odpojene");

		} catch (SQLException e) {
			logger.error("Chyba pri odpajani databazy", e);

		}
	}

	/**
	 * vytvori zo Stringu dopyt, vykona ho a vysledok vrati
	 * 
	 * @return vysledok dopytu
	 */
	public ResultSet executeDbQuery(String query) {

		try {

			logger.info("Vykonavam dopyt: " + query);
			statement = getDBConnection().createStatement();
			resultSet = statement.executeQuery(query);

		} catch (SQLException e) {
			logger.error("Doslo k chybe pri vykonavani dopytov", e);
			JOptionPane.showMessageDialog(MainWindow.getParentFrame(),
					"Chyba pri vykonavani dopytu.", "SQL Query Error",
					JOptionPane.ERROR_MESSAGE);
		}

		return resultSet;
	}

}