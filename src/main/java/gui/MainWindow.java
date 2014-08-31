
package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

//graficke rozhranie pre hlavne okno
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 7963043735196726453L;
	private static JFrame frame;
	private JMenuItem mntmLoginEditDatabase;
	private List<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
	private Logger logger = Logger.getLogger(MainWindow.class);
	private JTextField textField;
	private JTable table;
	private JTextArea textArea;
	private JButton btnX;
	private JButton btnPreloi;
	private JButton btnno;
	private JButton btnNie;
	private JTextArea textArea_1;
	private JTextArea textArea_2;
	private JMenuItem mntmSprvaDatabzy;
	private JMenuItem mntmAbout;

	public MainWindow() {
		initialize();

	}

	public List<JCheckBox> getCheckboxes() {
		return checkboxes;
	}

	// inicializuje obsah okna
	private void initialize() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException: ", e);
		} catch (InstantiationException e) {
			logger.error("Instantiation: ", e);
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccess: ", e);
		} catch (UnsupportedLookAndFeelException e) {
			logger.error("UnsupportedLookAndFeel: ", e);
		}

		frame = new JFrame();
		frame.setBounds(100, 100, 751, 552);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 10, 120, 10, 149, 5, 10, 20,
				5, 20, 10, 0 };
		gridBagLayout.rowHeights = new int[] { 10, 20, 5, 70, 5, 70, 5, 70,
				30, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0,
				1.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, 1.0,
				0.0, 1.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblSpojenie = new JLabel("Vstupn\u00E1 veta");
		lblSpojenie.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 14));
		GridBagConstraints gbc_lblSpojenie = new GridBagConstraints();
		gbc_lblSpojenie.insets = new Insets(0, 0, 5, 5);
		gbc_lblSpojenie.gridx = 1;
		gbc_lblSpojenie.gridy = 1;
		frame.getContentPane().add(lblSpojenie, gbc_lblSpojenie);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 4;
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 3;
		gbc_textField.gridy = 1;
		frame.getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);

		btnX = new JButton("X");
		GridBagConstraints gbc_btnX = new GridBagConstraints();
		gbc_btnX.insets = new Insets(0, 0, 5, 5);
		gbc_btnX.gridx = 7;
		gbc_btnX.gridy = 1;
		frame.getContentPane().add(btnX, gbc_btnX);

		btnPreloi = new JButton("Prelo\u017Ei\u0165");
		GridBagConstraints gbc_btnPreloi = new GridBagConstraints();
		gbc_btnPreloi.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnPreloi.insets = new Insets(0, 0, 5, 5);
		gbc_btnPreloi.gridx = 8;
		gbc_btnPreloi.gridy = 1;
		frame.getContentPane().add(btnPreloi, gbc_btnPreloi);

		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		separator.setPreferredSize(new Dimension(5, 1));
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.weighty = 1;
		gbc_separator.gridwidth = 8;
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridx = 1;
		gbc_separator.gridy = 2;
		frame.getContentPane().add(separator, gbc_separator);

		JScrollPane scrollPane_2 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.gridwidth = 3;
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 1;
		gbc_scrollPane_2.gridy = 3;
		frame.getContentPane().add(scrollPane_2, gbc_scrollPane_2);

		textArea_1 = new JTextArea();
		textArea_1.setEditable(false);
		textArea_1.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 12));
		textArea_1.setCaretPosition(0);
		scrollPane_2.setViewportView(textArea_1);

		JScrollPane scrollPane_3 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.gridwidth = 4;
		gbc_scrollPane_3.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 5;
		gbc_scrollPane_3.gridy = 3;
		frame.getContentPane().add(scrollPane_3, gbc_scrollPane_3);

		textArea_2 = new JTextArea();
		textArea_2.setEditable(false);
		textArea_2.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 12));
		textArea_2.setCaretPosition(0);
		scrollPane_3.setViewportView(textArea_2);

		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		separator_1.setPreferredSize(new Dimension(5, 1));
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.weighty = 1;
		gbc_separator_1.gridwidth = 8;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 1;
		gbc_separator_1.gridy = 4;
		frame.getContentPane().add(separator_1, gbc_separator_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 8;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 5;
		frame.getContentPane().add(scrollPane_1, gbc_scrollPane_1);

		textArea = new JTextArea();
		textArea.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 14));
		textArea.setEditable(false);

		scrollPane_1.setViewportView(textArea);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 8;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 7;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);

		JLabel lblNewLabel = new JLabel(
				"Zhoduje sa v\u00FDsledok s o\u010Dak\u00E1vaniami?");
		lblNewLabel.setFont(new Font("Franklin Gothic Medium", Font.PLAIN, 14));
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 4;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 8;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		btnno = new JButton("\u00C1no");
		GridBagConstraints gbc_btnno = new GridBagConstraints();
		gbc_btnno.gridwidth = 2;
		gbc_btnno.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnno.insets = new Insets(0, 0, 0, 5);
		gbc_btnno.gridx = 6;
		gbc_btnno.gridy = 8;
		frame.getContentPane().add(btnno, gbc_btnno);

		btnNie = new JButton("Nie");
		GridBagConstraints gbc_btnNie = new GridBagConstraints();
		gbc_btnNie.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNie.insets = new Insets(0, 0, 0, 5);
		gbc_btnNie.gridx = 8;
		gbc_btnNie.gridy = 8;
		frame.getContentPane().add(btnNie, gbc_btnNie);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		mntmSprvaDatabzy = new JMenuItem("Spr\u00E1va datab\u00E1zy");
		mnNewMenu.add(mntmSprvaDatabzy);

		JMenuItem mntmNewMenuItem = new JMenuItem("Exit");
		mnNewMenu.add(mntmNewMenuItem);

		JMenu mnNewMenu_1 = new JMenu("Options");
		menuBar.add(mnNewMenu_1);

		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mnAbout.add(mntmHelp);

		mntmAbout = new JMenuItem("About");
		mnAbout.add(mntmAbout);
	}

	public JMenuItem getMenuItemLoginEditDatabase() {
		return mntmLoginEditDatabase;
	}

	public JFrame getFrame() {
		return frame;
	}

	public JTable getTable() {
		return table;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public JButton getBtnX() {
		return btnX;
	}

	public JButton getBtnPrelozit() {
		return btnPreloi;
	}

	public JButton getBtnAno() {
		return btnno;
	}

	public JButton getBtnNie() {
		return btnNie;
	}

	public JTextField getTextFieldInput() {
		return textField;
	}

	public static JFrame getParentFrame() {
		return frame;

	}

	public JTextArea getTextArea_1() {
		return textArea_1;
	}

	public JTextArea getTextArea_2() {
		return textArea_2;
	}

	public JMenuItem getMntmSprvaDatabzy() {
		return mntmSprvaDatabzy;
	}
	public JMenuItem getMntmAbout() {
		return mntmAbout;
	}
}