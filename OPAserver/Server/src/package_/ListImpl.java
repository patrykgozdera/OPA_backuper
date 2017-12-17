package package_;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

public class ListImpl implements MetadataInterface, Serializable {

	private static final long serialVersionUID = 1057330569824131421L;
	public String path = new File("backuped_files/").getAbsolutePath();
	
	@Override
	public Metadata returnMetadata(String name) throws RemoteException {
		
		Metadata meta = new Metadata ();
		
		try {
			
			System.out.print(path + "\\" + name);
			File test = new File(path + "\\" + name);	
			FileTime modificationDate = (FileTime) Files.getAttribute(test.toPath(), "lastModifiedTime");
			long modTime = modificationDate.toMillis();
			FileTime creationDate = (FileTime) Files.getAttribute(test.toPath(), "creationTime");
			long crTime = creationDate.toMillis();
			FileTime accessDate;
			accessDate = (FileTime) Files.getAttribute(test.toPath(), "lastAccessTime");		
			long accTime = accessDate.toMillis();
			meta.creationTime = crTime;		
			meta.modificationTime = modTime;	
			meta.accessTime = accTime;
			meta.fileName = test.getName();
			meta.fileSize = test.length();
		} catch (IOException e) {
			
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "This file has been removed, open window once again!");
		}

		return meta;
	}

	@Override
	public Metadata returnMetadata() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int returnServerAction() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int returnClientAction() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] returnFiles() throws RemoteException {
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		String[] list = new String[listOfFiles.length];
		
		for (int i = 0; i < listOfFiles.length; i++)
			list[i] = listOfFiles[i].getName();
		
		return list;
	}

}
