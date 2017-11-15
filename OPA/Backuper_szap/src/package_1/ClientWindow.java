package package_1;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClientWindow extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	public static File myFile;
	//private JTextField address_field, port_field;
	private JButton ok, ok2, cancel, send;
	JFileChooser fileChooser = new JFileChooser();
	
	public ClientWindow()
	{
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(400,300));
		setLayout(new GridLayout(3,1));
		setLocation(10,10);		
		ok = new JButton("Add File");
		ok.addActionListener(this);
		add(ok);
		ok2 = new JButton("Add directory");
		ok2.addActionListener(this);
		add(ok2);
		cancel = new JButton("CANCEL");
		cancel.addActionListener(this);
		add(cancel);
		send = new JButton("send");
		send.addActionListener(this);
		add(send);
		pack();
		this.setVisible(true);
		
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if(source == cancel)
		{
			dispose();			
		}
		else if(source == ok)
		{				
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getSelectedFile();
				myFile = file;
				JOptionPane.showMessageDialog(null, "Choosen file is: " + file.getName());
			}
		}
		else if(source == ok2)
		{	
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				File file = fileChooser.getCurrentDirectory();
				JOptionPane.showMessageDialog(null, "Choosen directory is: " + file.getPath());
			}
		}
		else if(source == send)
		{				
			Client cl = new Client();
			try
			{
				cl.clientMethod();
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
