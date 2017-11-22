package package_1;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import package_.Metadata;
import package_.MetadataInterface;

public class Client 
{
	private static Socket clientSocket = null;
	private static int packetSize = 8192;
	private static byte[] buffer = new byte[packetSize];
	private static long fileSize;
	private static long wholeFileSize;
	private static Metadata metadata = new Metadata();
	private static File metaFile = new File("temp/metadata.ser");
	private static long metadataSize;
	private boolean isFileReceived = false;
	private boolean checkingIfFileExisting = true;
	public static Registry registry;

	public void clientMethod() throws Exception
	{
		clientSocket = new Socket("localhost", Config.PORT);
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
		fileSize = ClientWindow.myFile.length();
		wholeFileSize = fileSize;
		//zaczynam od utworzenia metadanych
		metaFile.createNewFile();
		createMetadata();
		//serializacja metadanych
		serializeMetadata();
		System.out.println("Sending metadata...");
		//wysylanie metadanych
		sendMetadata(dos);
		System.out.println("Asking if file is already uploaded...");
		//otwieramy rejestr RMI
		//String url = "rmi://localhost/Server";
		registry = LocateRegistry.getRegistry("localhost", 1099);
		MetadataInterface existenceStub;
		MetadataInterface receivingStub;
		while (checkingIfFileExisting)
		{
			boolean done = false;
			while (done == false)
			{
				String[] mList = registry.list();
				for (int i = 0; i < mList.length; i++)
				{
					System.out.println(mList[i].compareTo(ClientWindow.myFile.getName()));
					if (mList[i].compareTo(ClientWindow.myFile.getName()) == 0)
					{
						done = true;
					}
				}
			}
			existenceStub = (MetadataInterface) registry.lookup(ClientWindow.myFile.getName());
			if (existenceStub.fileState() == 0)//plik nie istnieje, kontynuujemy wysylanie pliku
			{
				//dalej bez zmian, do strumienia wrzucamy nasz pliczek
				sendFile(dos);
				dos.close();
				//czekamy az serwer w pelni odbierze wyslany plik
				while (isFileReceived == false)
				{
					//wykonujemy az flaga zmieni sie na true
					try{
						receivingStub = (MetadataInterface) registry.lookup(ClientWindow.myFile.getName());
						if (receivingStub.fileState() == 2 || receivingStub.fileState() == 3)
						{
							checkingIfFileExisting = false;
							isFileReceived = true;
						}
					}
					catch (NotBoundException e) {
						e.printStackTrace();
					}
				}
				//ponownie ustawiamy flage na false i zamykamy socket
				isFileReceived = false;
				clientSocket.close();
				System.out.println("Sending completed.");
			}
			else if (existenceStub.fileState() == 3)//plik juz istnieje, nie wysylamy pliku
				checkingIfFileExisting = false;//plik istnieje => wychodzimy z petli
		}
		checkingIfFileExisting = true;
	}
	
	private static void createMetadata()
	{		
		try {
			metadata.fileSize = fileSize;
			metadata.fileName = ClientWindow.myFile.getName();
			FileTime modificationDate = (FileTime) Files.getAttribute(ClientWindow.myFile.toPath(), "lastModifiedTime");
			metadata.modificationTime = modificationDate.toMillis();
			FileTime creationDate = (FileTime) Files.getAttribute(ClientWindow.myFile.toPath(), "creationTime");
			metadata.creationTime = creationDate.toMillis();
			FileTime accessDate = (FileTime) Files.getAttribute(ClientWindow.myFile.toPath(), "lastAccessTime");
			metadata.accessTime = accessDate.toMillis();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void serializeMetadata()
	{
        FileOutputStream fos;
		try {
			fos = new FileOutputStream(metaFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(metadata);
			oos.close();
			fos.close();
			metadataSize = metaFile.length();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendMetadata(DataOutputStream dos)
	{
		try{
			//strumien do oczytu zserializowanych metadanych
			FileInputStream metafis = new FileInputStream(metaFile);
			//tablica na odczytane metadane
			byte[] metaBuffer = new byte[(int)metaFile.length()];
			dos.writeLong(metadataSize);
			dos.flush();
			metafis.read(metaBuffer);
			dos.write(metaBuffer);
			dos.flush();
			metafis.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static void sendFile(DataOutputStream dos)
	{
		try {
			FileInputStream fis = new FileInputStream(ClientWindow.myFile);
			int n = 0;
			int i = 0;
			//dale normalnie wysylamy plik
				while (fileSize > 0 && (n = fis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
				{					
					dos.write(buffer,0,n);
					dos.flush();						
					fileSize -= n;		
					ClientWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
					System.out.println("Processed: " + (100 - fileSize * 100 / wholeFileSize) + "%");					
					i++;
				}
			fis.close();
			System.out.println("Pakiety: " + i);		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
		
}