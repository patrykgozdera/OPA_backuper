package package_1;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class ClientWindow extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	public static List<File> filesToSend;
	public static File myFile;	
	private JButton add_file, add_directory, cancel, send, b_files;
	static JProgressBar progressBar;
	JFileChooser fileChooser = new JFileChooser();

	
	public ClientWindow() throws Exception
	{
		super("Client");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 		//moze to jej urok, a moze to ladny dizajn
				
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e)
			{				
				int x = JOptionPane.showConfirmDialog(null, "Close app?","", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
				if(x == JOptionPane.YES_OPTION)
				{
					System.exit(0);					
				}
			}
		});
		setPreferredSize(new Dimension(400,400));
		setLayout(new GridLayout(6,1, 7, 7));
		setLocation(10,10);				
		
		add_file = new JButton("Add File");
		add_file.addActionListener(this);
		add(add_file);
		
		add_directory = new JButton("Add directory");
		add_directory.addActionListener(this);
		add(add_directory);		
				
		send = new JButton("SEND");
		send.addActionListener(this);
		add(send);
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		add(progressBar);
		
		b_files = new JButton("Backuped files...");
		b_files.addActionListener(this);
		add(b_files);
		
		cancel = new JButton("CANCEL");
		cancel.addActionListener(this);
		add(cancel);
		
		pack();
		this.setVisible(true);
		
		ClientWindow.filesToSend = new ArrayList<File>();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{	
		Object source = e.getSource();
		if(source == cancel)
		{
			System.exit(0);		
		}
		else if(source == b_files)
		{				
			new BackupedFilesWoindow();			
		}
		else if(source == add_file)
		{				
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();
				myFile = file;
				JOptionPane.showMessageDialog(null, "Choosen file is: " + file.getName());
				filesToSend.add(file);			
				System.out.println(filesToSend);
			}
		}
		else if(source == add_directory)
		{	
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setAcceptAllFileFilterUsed(false);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();
				JOptionPane.showMessageDialog(null, "Choosen directory is: " + file.getPath());
				listf(file.getPath());
			}
		}
		else if(source == send)
		{				
			Thread thread = new Thread(new Runnable()
			{					
				public void run()
				{	
					ClientWindow.this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
					//mysle, ze to dobry pomysl
					//robimy i-razy akcje klienta
					for (int i = 0; i < filesToSend.size(); i++)
					{
						myFile = filesToSend.get(i);
						Client cl = new Client();
						try
						{
							cl.clientMethod();
						} catch (Exception e1)
						{
							e1.printStackTrace();						
						}
					}
					filesToSend.clear();
				}
			});
			thread.start();				
		}		
	}
	
	private static List<File> listf(String directoryName)
	{
        File directory = new File(directoryName);      
        File[] fList = directory.listFiles();
        filesToSend.addAll(Arrays.asList(fList));
        for (File file : fList)
        {
            if (file.isFile()) 
            {
                //System.out.println(file.getAbsolutePath());
            } 
            else if (file.isDirectory()) 
            {
                filesToSend.addAll(listf(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        System.out.println(filesToSend);
        return filesToSend;
    } 
}
