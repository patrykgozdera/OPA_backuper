package package_;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class MainWindow extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 8732770117623931572L;
	
	private JButton start, cancel;
	static JProgressBar progressBar;
	private Boolean isRunning;
	ServerConfiguration server;
	
	public MainWindow() throws Exception
	{
		super("Backuper");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(400,200));
		server = null;
		isRunning = Boolean.valueOf(false);
		
		start = new JButton("START");
		start.addActionListener(this);
		add(start);// Integer.valueOf(0), 5);
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		add(progressBar);
		
		cancel = new JButton("CANCEL");
		cancel.addActionListener(this);
		add(cancel);
		
		setLayout(new GridLayout(3,1, 7, 7));		
		pack();
		setVisible(true);
	}
	
	public void startServer()
	{
		if(!isRunning.booleanValue())
		{
			server = new ServerConfiguration(Config.PORT);
			Thread thread = new Thread(server);
			thread.start();
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == cancel)
		{
			System.exit(0);	
		}
		else if(source == start)
		{
		startServer();
		}
	}
	
	
}
