package package_;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainWindow extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 8732770117623931572L;
	
	private JButton start;
	private Boolean isRunning;
	ServerConfiguration server;
	
	public MainWindow() 
	{
		super("Backuper");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(400,200));
		server = null;
		isRunning = Boolean.valueOf(false);
		start = new JButton("START");
		start.addActionListener(this);
		setLayout(new GridLayout(1, 1));
		add(start);// Integer.valueOf(0), 5);
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
		startServer();		
	}
	
	
}
