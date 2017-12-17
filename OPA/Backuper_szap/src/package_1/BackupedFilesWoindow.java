package package_1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import package_.FileImpl;
import package_.Metadata;
import package_.MetadataInterface;

public class BackupedFilesWoindow extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private JTextArea description;
	private JButton copyButton;
	private JButton deleteButton;
	private JList<String> jlist;
	private Metadata meta;
	MetadataInterface tempStub;
	
	MouseListener mouseListener = new MouseAdapter() {
		
        public void mouseClicked(MouseEvent mouseEvent) {
        	
          @SuppressWarnings("unchecked")
		JList<String> theList = (JList<String>) mouseEvent.getSource();
          
          if (mouseEvent.getClickCount() == 1) {	
        	  
            int index = theList.locationToIndex(mouseEvent.getPoint());
            
            if (index >= 0) {
            	
            	description.setText(null);
            	Object o = theList.getModel().getElementAt(index);
            	
            	try {
            		
					meta = tempStub.returnMetadata(o.toString());
				} catch (RemoteException e) {

					e.printStackTrace();
				}           	
            	
            		description.append("File name: " + meta.fileName + "\n");
            		description.append("File size: " + meta.fileSize/1024 + "KB\n");
            		description.append("Last modification time: " + new Date(meta.modificationTime) + "\n");
            		description.append("Last access time: " + new Date(meta.accessTime) + "\n");
            		description.append("Creation time: " + new Date(meta.creationTime) + "\n");           		
            	
            }
          }
        }
      };
    
	public BackupedFilesWoindow() {
		
		super("Backuped files");
		setPreferredSize(new Dimension(750,800));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);			
		
		String[] mL = null;
		
		try {
			
			Client.registry = LocateRegistry.getRegistry("localhost", 1099);
			tempStub = (MetadataInterface)Client.registry.lookup("files");
			mL = tempStub.returnFiles();		
		} catch (AccessException e1) {		
			
			JOptionPane.showMessageDialog(null, "Server is unreachable!", null, JOptionPane.WARNING_MESSAGE);
			setVisible(false);
		} catch (RemoteException e1) {		
			
			JOptionPane.showMessageDialog(null, "Server is unreachable!", null, JOptionPane.WARNING_MESSAGE);
			setVisible(false);
		} catch (NotBoundException e) {
			
			e.printStackTrace();
		}	
		
	    jlist = new JList<String>(mL);
	    jlist.addMouseListener(mouseListener);
	    JScrollPane scrollPane1 = new JScrollPane(jlist);
	    getContentPane().add(scrollPane1, BorderLayout.WEST);
	    
	    description = new JTextArea();
	    description.setEditable(false);
		Font font = description.getFont();
		float size = font.getSize() + 10.0f;
		description.setFont( font.deriveFont(size) );
	    getContentPane().add(description, BorderLayout.EAST);
	    
	    copyButton = new JButton("Get file");
	    copyButton.addActionListener(this);
	    
	    deleteButton = new JButton("Delete file");
	    deleteButton.addActionListener(this);
	    
	    JPanel subpanel = new JPanel();
	    subpanel.add(copyButton);
	    subpanel.add(deleteButton);
	    getContentPane().add(subpanel, BorderLayout.PAGE_END);
	    
	    pack();
	    this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		
		Object source = e.getSource();
		
		if (source == copyButton) {
			
			JFileChooser fileChooser = new JFileChooser();
			
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);

			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				
				File folder = fileChooser.getSelectedFile();
				JOptionPane.showMessageDialog(null, "Choosen directory is: " + folder.getPath());
				File[] listOfFiles = folder.listFiles();
				String[] list = new String[listOfFiles.length];
				for (int i = 0; i < listOfFiles.length; i++)
					list[i] = listOfFiles[i].getName();
				
				int verOfFile = 0;
				String noExtension = meta.fileName.substring(0, meta.fileName.lastIndexOf("."));
				if (meta.fileName.lastIndexOf(")") == meta.fileName.length())
					noExtension = noExtension.substring(0, noExtension.lastIndexOf("("));

				String extension = meta.fileName.substring(meta.fileName.lastIndexOf("."), meta.fileName.length());
					
				for (int i = 0; i < list.length; i++) {
					
					if (list[i].contains(noExtension) && list[i].contains(extension)) {
						
						verOfFile++;
					}
				}
				
				if (verOfFile == 0)
				{
					Thread thread = new Thread(new Runnable() {
						
						public void run() {
		
							Client cl = new Client();
							try {
								
								cl.clientReceivingMethod(folder.getPath());
							} catch (Exception e1) {
									
								e1.printStackTrace();						
							}
						}
					});
					
					thread.start();	
					
					FileImpl dlStub = new FileImpl(2, 0, meta.fileName, meta.fileSize, meta.modificationTime, meta.creationTime, meta.accessTime);
		
					try {
						
						Client.registry.rebind("controller", dlStub);
					} catch (RemoteException e1) {
		
						e1.printStackTrace();
					}
				}
				else
				{
					
					try {
						
						String newName = noExtension + extension;
						File f = new File(folder + "/" + newName);
						
						long size = f.length();
						FileTime modificationDate = (FileTime) Files.getAttribute(f.toPath(), "lastModifiedTime");
						long modTime = modificationDate.toMillis();
						FileTime creationDate = (FileTime) Files.getAttribute(f.toPath(), "creationTime");
						long crTime = creationDate.toMillis();
						FileTime accessDate = (FileTime) Files.getAttribute(f.toPath(), "lastAccessTime");
						long accTime = accessDate.toMillis();
						
						if (size == meta.fileSize && modTime == meta.modificationTime && 
								crTime == meta.creationTime && accTime == meta.accessTime) {
							
							JOptionPane.showMessageDialog(null, "The same file is already in this directory!");
						}
						else
						{
							Thread thread = new Thread(new Runnable() {
								
								public void run() {
				
									Client cl = new Client();
									try {
										
										cl.clientReceivingMethod(folder.getPath());
									} catch (Exception e1) {
											
										e1.printStackTrace();						
									}
								}
							});
							
							thread.start();	
							
							FileImpl dlStub = new FileImpl(2, 0, meta.fileName, meta.fileSize, meta.modificationTime, meta.creationTime, meta.accessTime);
				
							try {
								
								Client.registry.rebind("controller", dlStub);
							} catch (RemoteException e1) {
				
								e1.printStackTrace();
							}
						}
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		}
		else if (source == deleteButton)
		{
			
			Thread thread = new Thread(new Runnable() {
				
				public void run() {

					Client cl = new Client();
					try {
						cl.clientReceivingMethod(null);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			thread.start();	
			
			FileImpl deleteStub = new FileImpl(3, 0, meta.fileName, meta.fileSize, meta.modificationTime, meta.creationTime, meta.accessTime);
			
			try {
				
				Client.registry.rebind("controller", deleteStub);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
