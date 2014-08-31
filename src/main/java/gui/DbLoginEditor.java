package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


//okno pre upravu nastaveni pripojenia k databaze
public class DbLoginEditor {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JButton btnUloi;
	private JButton btnZrui;
	private JLabel lblDatabaseConnector;
	private JTextField textField_5;

	public DbLoginEditor() {
		initialize();
	}

	private void initialize() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {

			e.printStackTrace();
		}

		frame = new JFrame();
		frame.setBounds(100, 100, 394, 450);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				MainWindow.getParentFrame().setVisible(true);
				frame.setVisible(false);
			}
		});

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 10, 0, 10, 169, 84, 10, 0 };
		gridBagLayout.rowHeights = new int[] { 10, 15, 0, 15, 0, 15, 0, 15, 0,
				15, 10, 0, 5, 50, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0,
				0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, 1.0,
				0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.WEST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 1;
		gbc_lblUsername.gridy = 1;
		frame.getContentPane().add(lblUsername, gbc_lblUsername);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 2;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		frame.getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.WEST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 1;
		gbc_lblPassword.gridy = 3;
		frame.getContentPane().add(lblPassword, gbc_lblPassword);

		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 2;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 3;
		gbc_textField_1.gridy = 3;
		frame.getContentPane().add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);

		JLabel lblDatabbaseName = new JLabel("Database name:");
		GridBagConstraints gbc_lblDatabbaseName = new GridBagConstraints();
		gbc_lblDatabbaseName.anchor = GridBagConstraints.WEST;
		gbc_lblDatabbaseName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabbaseName.gridx = 1;
		gbc_lblDatabbaseName.gridy = 5;
		frame.getContentPane().add(lblDatabbaseName, gbc_lblDatabbaseName);

		textField_2 = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.gridwidth = 2;
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 3;
		gbc_textField_2.gridy = 5;
		frame.getContentPane().add(textField_2, gbc_textField_2);
		textField_2.setColumns(10);

		JLabel lblHostName = new JLabel("Host name:");
		GridBagConstraints gbc_lblHostName = new GridBagConstraints();
		gbc_lblHostName.anchor = GridBagConstraints.WEST;
		gbc_lblHostName.insets = new Insets(0, 0, 5, 5);
		gbc_lblHostName.gridx = 1;
		gbc_lblHostName.gridy = 7;
		frame.getContentPane().add(lblHostName, gbc_lblHostName);

		textField_3 = new JTextField();
		GridBagConstraints gbc_textField_3 = new GridBagConstraints();
		gbc_textField_3.gridwidth = 2;
		gbc_textField_3.insets = new Insets(0, 0, 5, 5);
		gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_3.gridx = 3;
		gbc_textField_3.gridy = 7;
		frame.getContentPane().add(textField_3, gbc_textField_3);
		textField_3.setColumns(10);

		JLabel lblPort = new JLabel("Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.WEST;
		gbc_lblPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblPort.gridx = 1;
		gbc_lblPort.gridy = 9;
		frame.getContentPane().add(lblPort, gbc_lblPort);

		textField_4 = new JTextField();
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.gridwidth = 2;
		gbc_textField_4.insets = new Insets(0, 0, 5, 5);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 3;
		gbc_textField_4.gridy = 9;
		frame.getContentPane().add(textField_4, gbc_textField_4);
		textField_4.setColumns(10);

		lblDatabaseConnector = new JLabel("Database connector");
		GridBagConstraints gbc_lblDatabaseConnector = new GridBagConstraints();
		gbc_lblDatabaseConnector.insets = new Insets(0, 0, 5, 5);
		gbc_lblDatabaseConnector.gridx = 1;
		gbc_lblDatabaseConnector.gridy = 11;
		frame.getContentPane().add(lblDatabaseConnector,
				gbc_lblDatabaseConnector);

		textField_5 = new JTextField();
		GridBagConstraints gbc_textField_5 = new GridBagConstraints();
		gbc_textField_5.gridwidth = 2;
		gbc_textField_5.insets = new Insets(0, 0, 5, 5);
		gbc_textField_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_5.gridx = 3;
		gbc_textField_5.gridy = 11;
		frame.getContentPane().add(textField_5, gbc_textField_5);
		textField_5.setColumns(10);

		btnUloi = new JButton("Ulo\u017Ei\u0165");
		GridBagConstraints gbc_btnUloi = new GridBagConstraints();
		gbc_btnUloi.anchor = GridBagConstraints.EAST;
		gbc_btnUloi.insets = new Insets(0, 0, 0, 5);
		gbc_btnUloi.gridx = 3;
		gbc_btnUloi.gridy = 13;
		frame.getContentPane().add(btnUloi, gbc_btnUloi);

		btnZrui = new JButton("Zru\u0161i\u0165");
		GridBagConstraints gbc_btnZrui = new GridBagConstraints();
		gbc_btnZrui.anchor = GridBagConstraints.EAST;
		gbc_btnZrui.insets = new Insets(0, 0, 0, 5);
		gbc_btnZrui.gridx = 4;
		gbc_btnZrui.gridy = 13;
		frame.getContentPane().add(btnZrui, gbc_btnZrui);
	}

	public JFrame getFrame() {
		return frame;
	}

	public JButton getBtnUlozit() {
		return btnUloi;
	}

	public JButton getBtnZrusit() {
		return btnZrui;
	}

	public JTextField getTextFieldUserName() {
		return textField;
	}

	public JTextField getTextField_1_Password() {
		return textField_1;
	}

	public JTextField getTextField_2_DbName() {
		return textField_2;
	}

	public JTextField getTextField_3_HostName() {
		return textField_3;
	}

	public JTextField getTextField_4_Port() {
		return textField_4;
	}

	public JTextField getTextField_5_DbConnector() {
		return textField_5;
	}
}
