package package_1;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class BackupedFilesWoindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	JTextArea text;
	
	public BackupedFilesWoindow()
	{
		super("Backuped files");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
		
		text = new JTextArea();
		this.add(text);
		try
		{
			Client.registry = LocateRegistry.getRegistry("localhost", 1099);
			String[] mL = Client.registry.list();
			for (int i = 0; i < mL.length; i++)
			{	
				if(!mL[i].equals("Server"))											
					text.append(mL[i] + "\n");					
			}
			setVisible(true);
		} catch (AccessException e1)
		{							
			JOptionPane.showMessageDialog(null,"Server in unreachable!",null,JOptionPane.WARNING_MESSAGE);
			setVisible(false);
		} catch (RemoteException e1)
		{			
			JOptionPane.showMessageDialog(null,"Server in unreachable!",null,JOptionPane.WARNING_MESSAGE);
			setVisible(false);
		}			
			text.setEditable(false);
			Font font = text.getFont();
			float size = font.getSize() + 10.0f;
			text.setFont( font.deriveFont(size) );
			text.setForeground(Color.yellow);
			text.setBackground(Color.black);
			text.setOpaque(true);
			pack();
			
	}
}
