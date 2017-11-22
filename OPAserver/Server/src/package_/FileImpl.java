package package_;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.RemoteException;

public class FileImpl implements MetadataInterface, Serializable{

	int state;
	String name;
	
	public FileImpl (int s, String n)
	{
		this.state = s;
		this.name = n;
	}
	
	@Override
	public int fileState() throws RemoteException {
		switch (state)
		{
		case 0:
			System.out.println("File does not exist!");
			break;
		case 1: 
			System.out.println("File unreceived!");
			break;
		case 2:
			System.out.println("File received!");
			break;
		case 3:
			System.out.println("File backuped!");
			break;
		}
		
		return state;
	}

	@Override
	public Metadata returnMetadata() {
		Metadata meta = new Metadata();
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
