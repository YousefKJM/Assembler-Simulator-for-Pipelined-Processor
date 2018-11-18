package assem_simul.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import java.awt.Color;
import java.awt.Component;

public class SimulatorUI {

	private JFrame frmAssembler;
	private JTable table_1;

	/**
	 * Launch the application.
	 */
	public void NewScreen() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulatorUI window = new SimulatorUI();
					window.frmAssembler.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SimulatorUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAssembler = new JFrame();
		frmAssembler.setResizable(false);

		frmAssembler.setTitle("172_COE301_ICS233_Assembler");
		frmAssembler.setBounds(100, 100, 551, 553);
		frmAssembler.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmAssembler.getContentPane().setLayout(null);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(24, 126, 491, 78);
		frmAssembler.getContentPane().add(textArea);

		JButton btnShowTheHex = new JButton("Display the Contents of O/P file");
		btnShowTheHex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedReader in = null;
				try {
					in = new BufferedReader(new FileReader("outputResult.hex"));
					String str;
					while ((str = in.readLine()) != null) {
						textArea.append(str + "\n");
					}
				} catch (IOException e1) {
				} finally {
					try {
						in.close();
					} catch (Exception ex) {
					}
				}
			}
		});
		btnShowTheHex.setBounds(167, 92, 217, 23);
		frmAssembler.getContentPane().add(btnShowTheHex);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(24, 249, 491, 220);
		frmAssembler.getContentPane().add(scrollPane);

		table_1 = new JTable();
		table_1.setEnabled(false);
		table_1.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Reg. No.", "Contents" }

		));
		scrollPane.setViewportView(table_1);
		

		JButton btnDisplayTheRegisters = new JButton("Display the Registers");
		btnDisplayTheRegisters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String line = null;
				Vector<String> data = null;
				DefaultTableModel dtm = (DefaultTableModel) table_1.getModel();

				try {
					BufferedReader br = new BufferedReader(new FileReader("RegsTable.txt"));

					while ((line = br.readLine()) != null) {
						data = new Vector<String>();// this is important
						StringTokenizer st1 = new StringTokenizer(line, "=");
						while (st1.hasMoreTokens()) {
							String nextToken = st1.nextToken();
							data.add(nextToken);
							System.out.println(nextToken);

						}
						System.out.println(data);
						dtm.addRow(data);// add here
						System.out.println(".................................");
					}

					br.close();

				} catch (Exception ew) {
					ew.printStackTrace();
				}
				//table_1.setDefaultRenderer(getClass(), new YourTableCellRenderer());
			}
		});
		btnDisplayTheRegisters.setBounds(167, 215, 217, 23);
		frmAssembler.getContentPane().add(btnDisplayTheRegisters);

		JLabel lblTheOutputFile = new JLabel(
				"The Output File has been Updated and Saved in the Spicified Location Successfully");
		lblTheOutputFile.setHorizontalAlignment(SwingConstants.CENTER);
		lblTheOutputFile.setFont(new Font("Yu Gothic UI", Font.BOLD, 13));
		lblTheOutputFile.setBounds(10, 11, 519, 39);
		frmAssembler.getContentPane().add(lblTheOutputFile);

		JLabel lblYouCanLoad = new JLabel("You Can Load it Immediately Into Your CPU");
		lblYouCanLoad.setForeground(Color.RED);
		lblYouCanLoad.setHorizontalAlignment(SwingConstants.CENTER);
		lblYouCanLoad.setFont(new Font("Franklin Gothic Medium", Font.BOLD, 15));
		lblYouCanLoad.setBounds(38, 49, 477, 32);
		frmAssembler.getContentPane().add(lblYouCanLoad);

		JLabel label = new JLabel("Developed By Yousef Majeed, Supervised By Mr. Saleh AlSaleh");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.BLUE);
		label.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		label.setBackground(Color.WHITE);
		label.setBounds(10, 480, 535, 23);
		frmAssembler.getContentPane().add(label);

		frmAssembler.setLocationRelativeTo(null);
	}
}

//class YourTableCellRenderer extends DefaultTableCellRenderer {
//	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
//			int row, int column) {
//		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//
//		c.setFont(new FontUIResource("Verdana", Font.PLAIN, 14));
//		// you may want to address isSelected here too
//		c.setForeground(Color.BLUE);
//		// c.setBackground(/* special background color */);
//
//		return c;
//	}
//}
