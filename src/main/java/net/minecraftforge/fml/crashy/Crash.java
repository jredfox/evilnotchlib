package net.minecraftforge.fml.crashy;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * Do NOT call Directly instead use {@link Crashy#crash(String, Throwable, boolean)}
 * @author jredfox
 */
public class Crash {
	
	public static void main(String[] args)
	{
		String msg = args[0].replace("\\n", "\n");
		boolean dark = Boolean.parseBoolean(System.getProperty("crashy.darkui", "false"));
		final JFrame ui = dark ? new CrashReportDarkUI(msg) : new CrashFrame(msg);
		ui.validate();
		ui.pack();
		ui.setVisible(true);//Makes it visible
		ui.requestFocus();//Set's the Frame's Focus
	}
	
	public static class CrashFrame extends JFrame
	{
		public CrashFrame(String msg)
		{
			try 
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Throwable e) {}
			this.setTitle("Crash Report");
			int width = 950;
			int height = 550;
	        this.setSize(width, height);
	        this.setPreferredSize(new Dimension(width, height));
	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        this.setLocationRelativeTo(null);
	        this.add(new PanelCrashReport(msg));
	        this.setFocusable(true);
		}
	}
	
	public static class PanelCrashReport extends JPanel
	{  
	    public PanelCrashReport(String msg)
	    {
	        this.setBackground(new Color(3028036));
	        this.setLayout(new BorderLayout());
	        this.add(new CanvasCrashReport(80), "East");
	        this.add(new CanvasCrashReport(80), "West");
	        this.add(new CanvasCrashReport(100), "South");
	        
	        JTextArea textarea = new JTextArea(msg);
	        textarea.setEditable(false);
	        textarea.setLineWrap(false);
	        textarea.setFont(new Font("Monospaced", 0, 16));
	        
	        JScrollPane scrollPane = new JScrollPane(textarea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem copyMenuItem = new JMenuItem("Copy");
            JMenuItem copyAllMenuItem = new JMenuItem("Copy All");
            
            // Add action listener for "Copy"
            copyMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedText = textarea.getSelectedText();
                    if (selectedText != null && !selectedText.isEmpty()) {
                    	textarea.copy();
                    }
                }
            });

            // Add action listener for "Copy All"
            copyAllMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	textarea.selectAll();
                	textarea.copy();
                	textarea.select(0, 0);
                }
            });
            
            // Add menu items to the popup menu
            popupMenu.add(copyMenuItem);
            popupMenu.add(copyAllMenuItem);
            textarea.setComponentPopupMenu(popupMenu);
            
            this.add(scrollPane, "Center");
	    }
	}
	
	public static class CanvasCrashReport extends Canvas
	{
	    public CanvasCrashReport(int par1)
	    {
	        this.setPreferredSize(new Dimension(par1, par1));
	        this.setMinimumSize(new Dimension(par1, par1));
	    }
	}
	
    public static String toString(Throwable t)
    {
        StringWriter stringwriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stringwriter));
        return stringwriter.toString();
    }
    
	/**
	 * EvilNotchLib's Dark Crash Report UI
	 */
	public static class CrashReportDarkUI extends JFrame
	{
	    public CrashReportDarkUI(String crashReport) {
			try 
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Throwable e) {}
	        this.setTitle("Crash Report");
	        int width = 950;
	        int height = 550;
	        this.setSize(width, height);
	        this.setPreferredSize(new Dimension(width, height));
	        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        this.setLocationRelativeTo(null);
	        
	        JTextArea textArea = new JTextArea(crashReport);
	        textArea.setEditable(false);
	        textArea.setForeground(Color.WHITE); // Set text color to white
	        textArea.setBackground(Color.BLACK);
	        textArea.setFont(new Font("Arial", Font.PLAIN, 18));
	        
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem copyMenuItem = new JMenuItem("Copy");
            JMenuItem copyAllMenuItem = new JMenuItem("Copy All");
            
            // Add action listener for "Copy"
            copyMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedText = textArea.getSelectedText();
                    if (selectedText != null && !selectedText.isEmpty()) {
                    	textArea.copy();
                    }
                }
            });

            // Add action listener for "Copy All"
            copyAllMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	textArea.selectAll();
                	textArea.copy();
                	textArea.select(0, 0);
                }
            });
            
            // Add menu items to the popup menu
            popupMenu.add(copyMenuItem);
            popupMenu.add(copyAllMenuItem);
            textArea.setComponentPopupMenu(popupMenu);
	        
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	        this.getContentPane().setLayout(new BorderLayout());
	        this.getContentPane().setBackground(Color.BLACK); // Set frame background to black
	        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
	    }
	}
	
	/**
	 * EvilNotchLib's Original Crash Report's UI. Doesn't Have Copy / Copy All and has the Unnecessary Close Button
	 */
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
