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
import java.util.ArrayList;
import java.util.List;

public class Client 
{
	private static Socket clientSocket = null;
	private static int packetSize = 8192;
	private static byte[] buffer = new byte[packetSize];
	private static long fileSize;
	private static long wholeFileSize;
	private static Metadata metadata = new Metadata();
	private static File metaFile; 
	private static long metadataSize;
	public static List<File> metaFiles = new ArrayList<File>();

	public void clientMethod() throws Exception
	{
				clientSocket = new Socket("localhost", Config.PORT);
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
				//fileSize = ClientWindow.myFile.length();
				//wholeFileSize = fileSize;
				//zaczynam od utworzenia metadanych
				//metaFile.createNewFile();
				createMetadata();
				System.out.println("Sending...");
				for (int j=0; j <ClientWindow.filesToSend.size(); j++)
				{
					serializeMetadata(metaFiles.get(j));
					sendMetadata(dos, metaFiles.get(j));
					sendFile(dos, ClientWindow.filesToSend.get(j));
					
					
				}
				
				//serializacja metadanych
				//serializeMetadata();
				
				//wysylanie metadanych
				//sendMetadata(dos);
				//dalej bez zmian, do strumienia wrzucamy nasz pliczek
				//SendFiles(dos);
				//SendAll2(dos);
				dos.close();
				clientSocket.close();
				System.out.println("Finished.");			
	}
	
	private static void createMetadata()
	{		
		try {
			int i = 0;
			for (File file : ClientWindow.filesToSend)
			{
				metadata.fileSize = fileSize;
				metadata.fileName = file.getName();
				FileTime modificationDate = (FileTime) Files.getAttribute(file.toPath(), "lastModifiedTime");
				metadata.modificationTime = modificationDate.toMillis();
				FileTime creationDate = (FileTime) Files.getAttribute(file.toPath(), "creationTime");
				metadata.creationTime = creationDate.toMillis();
				FileTime accessDate = (FileTime) Files.getAttribute(file.toPath(), "lastAccessTime");
				metadata.accessTime = accessDate.toMillis();
				metaFile = new File("temp/metadata"+i+".ser");
				metaFile.createNewFile();
				metaFiles.add(metaFile);
				i++;
			}
		} catch (IOException e) 
		{		

			e.printStackTrace();
		}
	}
	
	private static void serializeMetadata(File file)
	{
        FileOutputStream fos;
		try {
						
				fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(metadata);
				oos.close();
				fos.close();
				metadataSize = file.length();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private static void sendMetadata(DataOutputStream dos, File file)
	{
		try{
			
			
				//strumien do oczytu zserializowanych metadanych
				FileInputStream metafis = new FileInputStream(file);
				//tablica na odczytane metadane
				byte[] metaBuffer = new byte[(int)file.length()];
				dos.writeLong(file.length());
				dos.flush();
				metafis.read(metaBuffer);
				dos.write(metaBuffer);
				metafis.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static void sendFile(DataOutputStream dos, File file)
	{
		try {
			fileSize = file.length();
			wholeFileSize = fileSize;
			FileInputStream fis = new FileInputStream(file);
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
	
	private static void SendFiles(DataOutputStream dos)
	{
		try
		{
			for (File file : ClientWindow.filesToSend)
			{
				try
				{
					fileSize = file.length();
					wholeFileSize = fileSize;
					FileInputStream fis = new FileInputStream(file);
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
		}catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private static void SendAll2(DataOutputStream dos)
	{
		try
		{
			FileOutputStream fos;
			for (int j=0; j <ClientWindow.filesToSend.size(); j++)
			{
				fos = new FileOutputStream(metaFiles.get(j));
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(metadata);
				oos.close();
				fos.close();
				FileInputStream metafis = new FileInputStream(metaFiles.get(j));
				//tablica na odczytane metadane
				byte[] metaBuffer = new byte[(int)metaFiles.get(j).length()];
				dos.writeLong(metaFiles.get(j).length());
				dos.flush();
				metafis.read(metaBuffer);
				dos.write(metaBuffer);
				
				
				metafis.close();
				
				
				
				fileSize = ClientWindow.filesToSend.get(j).length();
				wholeFileSize = fileSize;
				metafis = new FileInputStream(ClientWindow.filesToSend.get(j));
				int n = 0;
				int i = 0;
					//dale normalnie wysylamy plik
				while (fileSize > 0 && (n = metafis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
					{									
						dos.write(buffer,0,n);
						dos.flush();						
						fileSize -= n;		
						ClientWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
						System.out.println("Processed: " + (100 - fileSize * 100 / wholeFileSize) + "%");					
						i++;
					}
				metafis.close();
				System.out.println("Pakiety: " + i);		
										
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void SendAll(DataOutputStream dos)
	{
		try
		{
			int j = 0;
			for (File file : ClientWindow.filesToSend)
			{
				FileInputStream metafis = new FileInputStream(metaFiles.get(j));
				//tablica na odczytane metadane
				byte[] metaBuffer = new byte[(int)metaFiles.get(j).length()];
				dos.writeLong(metaFiles.get(j).length());
				dos.flush();
				metafis.read(metaBuffer);
				dos.write(metaBuffer);
				metafis.close();
				j++;
				
				try
				{
					fileSize = file.length();
					wholeFileSize = fileSize;
					FileInputStream fis = new FileInputStream(file);
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
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
							
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
		
}