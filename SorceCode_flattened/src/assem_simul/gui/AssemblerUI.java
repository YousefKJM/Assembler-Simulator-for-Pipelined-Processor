package assem_simul.gui;

import java.awt.EventQueue;

import java.io.*;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import com.alee.laf.WebLookAndFeel;

import assem_simul.src.*;
import assembler.entity.*;
import assembler.exception.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.TextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class AssemblerUI extends JFrame {

	private JPanel contentPane;
	private JLabel lblWelcomeToMy;
	private java.io.File fileToSave;
	private JTextField txtError;
	private JFileChooser fileChooser;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WebLookAndFeel.install();
					
					AssemblerUI frame = new AssemblerUI();

					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AssemblerUI() {
		setResizable(false);

		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		setTitle("172_COE301_ICS233_Assembler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 532, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtError = new JTextField();
		txtError.setForeground(Color.GRAY);
		txtError.setText("If Error detected, It will Prompt here");
		txtError.setEditable(false);
		txtError.setBounds(28, 354, 459, 45);
		contentPane.add(txtError);
		txtError.setColumns(10);

		lblWelcomeToMy = new JLabel("Welcome to ICS233 Project Assembler");
		lblWelcomeToMy.setBounds(10, 11, 493, 46);
		lblWelcomeToMy.setFont(new Font("Verdana", Font.BOLD, 17));
		lblWelcomeToMy.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblWelcomeToMy);

		TextArea textArea = new TextArea();
		textArea.setBounds(32, 63, 455, 183);
		contentPane.add(textArea);

		JButton btnGetFile = new JButton("Load an Assembly File");
		btnGetFile.setBounds(178, 252, 162, 23);
		btnGetFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				StringBuilder sb = new StringBuilder();

				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

					// get the file
					java.io.File file = fileChooser.getSelectedFile();

					// create a scanner for the file
					Scanner input = null;
					try {
						input = new Scanner(file);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// read text from file
					while (input.hasNext()) {
						sb.append(input.nextLine());
						sb.append("\n");
					}

					input.close();
				} else
					sb.append("No file was selected");

				textArea.setText(sb.toString());
			}
		});
		contentPane.add(btnGetFile);

		JButton btnAssemble = new JButton("Assemble");
		btnAssemble.setBounds(45, 320, 424, 23);
		btnAssemble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String assembledCode, assembledCodeA;
				Assembler assembler = new Assembler();
				Simulator simulator = new Simulator();

				// ////////////// Assembler //////////////
				try {
					assembler.parse(textArea.getText());
				} catch (SyntaxException e3) { // must inclueded in GUI
					System.err.println(e3.getMessage());
					txtError.setForeground(Color.RED);
					txtError.setText(e3.getMessage());

					return;
				}
				try {
					assembledCodeA = assembler.assembleA();
					assembledCode = assembler.assemble();
				} catch (LabelNotFoundException e2) { // must inclueded in GUI
					System.err.println(e2.getMessage());
					txtError.setForeground(Color.RED);
					txtError.setText(e2.getMessage());

					return;
				}
				System.out.println("===== Assembly Result =====");
				System.out.println(assembledCode);


				try {
					FileWriter fw = new FileWriter("outputResult.hex", false);
					// FileWriter fw = new FileWriter(fileToSave, false);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(assembledCodeA.toString());

					bw.flush();
					bw.close();

				} catch (Exception e1) {
					e1.printStackTrace();
				}

				// ////////////// Simulator //////////////
				try {
					simulator.decode(assembledCode);
				} catch (SyntaxException e0) {
					System.err.println(e0.getMessage()); // must inclueded in GUI
					txtError.setForeground(Color.ORANGE);
					txtError.setText(e0.getMessage());
					return;
				} catch (InvalidInstructionException e7) { // must inclueded in GUI
					System.err.println(e7.getMessage());
					txtError.setForeground(Color.ORANGE);
					txtError.setText(e7.getMessage());
					return;
				}
				simulator.setMemory(0, 5);
				Thread t = new Thread(simulator);
				t.start();
				try {
					t.join(1000); // wait for 1 second
					if (t.isAlive()) {
						// still running, wait for 3 seconds
						System.err.println("Simulation is running, will be killed in 3 seconds...");
						t.join(3000);
						simulator.kill();
					}
				} catch (InterruptedException e6) {
					return;
				}
				System.out.println("===== Simulation Result =====");
				System.out.println("PC = " + simulator.getPc() /* + " * 1" */);
				try {
					FileWriter fw = new FileWriter("RegsTable.txt", false);
					BufferedWriter bw = new BufferedWriter(fw);
					RegisterFile regfile = simulator.getRegfile();
					for (int i = 0, size = regfile.getSize(); i < size; i++) {
						bw.write("Regfile[" + i + "] = " + regfile.get(i) + "\n");
					}
					bw.write("Program Counter [PC] = " + simulator.getPc());

					bw.flush();
					bw.close();

				} catch (Exception e1) {
					e1.printStackTrace();
				}

				SimulatorUI wn = new SimulatorUI();
				wn.NewScreen();

			}

		});
		contentPane.add(btnAssemble);

		JButton btnChooseFileTo = new JButton("Choose File to Save O/P in");
		btnChooseFileTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fileChooser.setDialogTitle("Specify a file to save");

				int userSelection = fileChooser.showSaveDialog(null);

				if (userSelection == JFileChooser.APPROVE_OPTION) {
					fileToSave = fileChooser.getSelectedFile();
					Assembler assembler = new Assembler();
					String assembledCodeA;

					try {
						assembler.parse(textArea.getText());
					} catch (SyntaxException e3) { // must inclueded in GUI
						System.err.println(e3.getMessage());
						txtError.setForeground(Color.RED);
						txtError.setText(e3.getMessage());

						return;
					}
					try {
						assembledCodeA = assembler.assembleA();
						// assembledCode = assembler.assemble();
					} catch (LabelNotFoundException e2) { // must inclueded in GUI
						System.err.println(e2.getMessage());
						txtError.setForeground(Color.RED);
						txtError.setText(e2.getMessage());

						return;
					}

					try {
						FileWriter fw = new FileWriter(fileToSave, false);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(assembledCodeA.toString());

						bw.flush();
						bw.close();

					} catch (Exception e1) { // must inclueded in GUI
						e1.printStackTrace();
					}

				}

			}
		});
		btnChooseFileTo.setBounds(149, 286, 214, 23);
		contentPane.add(btnChooseFileTo);

		JLabel lblDevelopedByYusef = new JLabel("Developed By Yousef Majeed, Supervised By Mr. Saleh AlSaleh");
		lblDevelopedByYusef.setForeground(Color.BLUE);
		lblDevelopedByYusef.setHorizontalAlignment(SwingConstants.CENTER);
		lblDevelopedByYusef.setBackground(Color.WHITE);
		lblDevelopedByYusef.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		lblDevelopedByYusef.setBounds(0, 418, 516, 23);
		contentPane.add(lblDevelopedByYusef);
		setLocationRelativeTo(null);
	}
}
