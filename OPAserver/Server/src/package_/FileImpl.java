package package_;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.RemoteException;

public class FileImpl implements MetadataInterface, Serializable {

	private static final long serialVersionUID = -5253364070056919890L;
	
	public int serverAction, clientAction;
	public long fileSize;
	public String fileName;
	public long modificationTime;
	public long creationTime;
	public long accessTime;
	
	public FileImpl (int sA, int cA, String n, long fS, long mT, long cT, long aT) {
		
		this.serverAction = sA;
		this.clientAction = cA;
		this.accessTime = aT;
		this.creationTime = cT;
		this.fileName = n;
		this.fileSize = fS;
		this.modificationTime = mT;
	}
	
	public FileImpl (int s, int c) {
		
		this.serverAction = s;
		this.clientAction = c;
	}
	
	@Override
	public int returnServerAction() throws RemoteException {

		return serverAction;
	}
	
	@Override
	public int returnClientAction() throws RemoteException {
	
		return clientAction;
	}
	
	public String[] returnFiles() {

		File folder = new File("backuped_files/");
		File[] listOfFiles = folder.listFiles();
		String[] list = new String[listOfFiles.length];
		
		for (int i = 0; i < listOfFiles.length; i++)
			list[i] = listOfFiles[i].getName();
		
		return list;
	}

	@Override
	public Metadata returnMetadata() {
		
		Metadata metadata = new Metadata();
		metadata.accessTime = accessTime;
		metadata.creationTime = creationTime;
		metadata.fileName = fileName;
		metadata.fileSize = fileSize;
		metadata.modificationTime = modificationTime;
		
		return metadata;
	}
	@Override
	public Metadata returnMetadata(String name) throws RemoteException {
		
		Metadata meta = new Metadata ();
		
		try {
			File test = new File("backuped_files/" + name);	
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
		}

		return meta;
	}
}
