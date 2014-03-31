package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

public class ClientGui {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGui window = new ClientGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel inputpanel = new JPanel();
		frame.getContentPane().add(inputpanel, BorderLayout.SOUTH);
		GridBagLayout gbl_inputpanel = new GridBagLayout();
		gbl_inputpanel.columnWidths = new int[]{0};
		gbl_inputpanel.rowHeights = new int[] {40};
		gbl_inputpanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_inputpanel.rowWeights = new double[]{Double.MIN_VALUE};
		inputpanel.setLayout(gbl_inputpanel);
		
		JPanel chat = new JPanel();
		frame.getContentPane().add(chat, BorderLayout.NORTH);
		GridBagLayout gbl_chat = new GridBagLayout();
		gbl_chat.columnWidths = new int[] {0, 10};
		gbl_chat.rowHeights = new int[] {0, 0, 200};
		gbl_chat.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_chat.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		chat.setLayout(gbl_chat);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		chat.add(scrollPane, gbc_scrollPane);
	}

}
