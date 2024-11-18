package net.minecraftforge.fml.crashy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Do NOT call Directly instead use {@link Crashy#crash(String, Throwable, boolean)}
 * @author jredfox
 */
public class Crash {
	
	public static void main(String[] args)
	{
		final CrashReportUI ui = new CrashReportUI(args[0].replace("\\n", "\n"));
		ui.setVisible(true);
	}
	
	public static class CrashReportUI extends JFrame
	{	
	    public CrashReportUI(String crashReport) {
	        setTitle("Crash Report");
	        setSize(900, 550);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	        
	        JTextArea textArea = new JTextArea(crashReport);
	        textArea.setEditable(false);
	        textArea.setForeground(Color.WHITE); // Set text color to white
	        textArea.setBackground(Color.BLACK);
	        textArea.setFont(new Font("Arial", Font.PLAIN, 18));
	        
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        
	        JButton closeButton = new JButton("Close");
	        closeButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                dispose();
	            }
	        });
	        closeButton.setBackground(Color.BLACK);
	        closeButton.setForeground(Color.WHITE);
	        
	        JPanel buttonPanel = new JPanel();
	        buttonPanel.setBackground(Color.BLACK); // Set panel background to black
	        buttonPanel.add(closeButton);
	        
	        getContentPane().setLayout(new BorderLayout());
	        getContentPane().setBackground(Color.BLACK); // Set frame background to black
	        getContentPane().add(scrollPane, BorderLayout.CENTER);
	        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	    }
	}
}
