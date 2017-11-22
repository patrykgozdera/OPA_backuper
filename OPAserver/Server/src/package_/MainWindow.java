package package_;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e){
				int x = JOptionPane.showConfirmDialog(null, "Close app?","", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
				if(x == JOptionPane.YES_OPTION){
					System.exit(0);					
				}
			}
		});		
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
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void startServer() throws RemoteException
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
			try {
				startServer();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
}
