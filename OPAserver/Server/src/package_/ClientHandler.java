package package_;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;

public class ClientHandler implements Runnable
{
	
	private static final long serialVersionUID = -896079909733233717L;
	private static Socket socket;
    private static int packetSize = 8192;	
    private static byte[] buffer = new byte[packetSize];
    private static long fileSize;
    private static long wholeFileSize;
    private static Metadata metadata = new Metadata();
    private static File metaFile = new File("temp/metadata.ser");
    
	public ClientHandler(Socket s) throws RemoteException
	{
		socket = s;
	}

	public void run()
	{
			try
			{				
				//dalej standardzik, odbieramy metadane
				metaFile.createNewFile();
				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				//wczytujemy metadane
				loadMetadata(dis);
				System.out.println("Metadane wczytane! hehe :V");
				//dalej analizujemy metadane
				fileSize = metadata.fileSize;
				wholeFileSize = fileSize;
				String[] backupList = ServerConfiguration.rmiRegistry.list();
				System.out.println("Odczytano obiekty z rejestru!");
				boolean fileExists = false;
				
				for ( int i = 0; i < backupList.length; i++)
				{
					System.out.println(backupList[i]);
					if (backupList[i].compareTo(metadata.fileName) == 0)
						fileExists = true;	
				}
				
				if (fileExists == false)
				{
					FileImpl tempImpl = new FileImpl(0, metadata.fileName);
					ServerConfiguration.rmiRegistry.bind(metadata.fileName, (MetadataInterface) UnicastRemoteObject.exportObject(tempImpl, 0));
					System.out.println("File is not backuped yet!");
					saveFile(dis);	
					FileImpl tempImpl1 = new FileImpl(1, metadata.fileName);
					ServerConfiguration.rmiRegistry.rebind(metadata.fileName, (MetadataInterface) UnicastRemoteObject.exportObject(tempImpl1, 0));
					dis.close();
					FileImpl tempImpl2 = new FileImpl(2, metadata.fileName);
					ServerConfiguration.rmiRegistry.rebind(metadata.fileName, (MetadataInterface) UnicastRemoteObject.exportObject(tempImpl2, 0));
				}
				else
				{
					System.out.println("There is file called same name! It's got same size too! Checking other metadata...");
					//pobieramy daty
					FileImpl tempImpl = new FileImpl(0, metadata.fileName);
					Metadata tempMeta = tempImpl.returnMetadata();
					long modTime = tempMeta.modificationTime;
					long crTime = tempMeta.creationTime;
					long accTime = tempMeta.accessTime;
					long size = tempMeta.fileSize;
					//sprawdzamy czy pliki roznia sie datami modyfikacji itp.
					if (size == metadata.fileSize && modTime == metadata.modificationTime && crTime == metadata.creationTime && accTime == metadata.accessTime)
					{
						System.out.println("This file is already backuped!");
						FileImpl tempImpl1 = new FileImpl(3, metadata.fileName);
						ServerConfiguration.rmiRegistry.rebind(metadata.fileName, (MetadataInterface) UnicastRemoteObject.exportObject(tempImpl1, 0));
					}
					else
					{
						System.out.println("This version of file does not have a backup!");
						FileImpl tempImpl2 = new FileImpl(0, metadata.fileName);
						ServerConfiguration.rmiRegistry.bind(metadata.fileName, (MetadataInterface) UnicastRemoteObject.exportObject(tempImpl2, 0));
						saveFile(dis);	
						FileImpl tempImpl3 = new FileImpl(1, metadata.fileName);
						ServerConfiguration.rmiRegistry.rebind(metadata.fileName, (MetadataInterface) UnicastRemoteObject.exportObject(tempImpl3, 0));
						dis.close();
						FileImpl tempImpl4 = new FileImpl(2, metadata.fileName);
						ServerConfiguration.rmiRegistry.rebind(metadata.fileName, (MetadataInterface) UnicastRemoteObject.exportObject(tempImpl4, 0));
					}
				}
			} catch (AlreadyBoundException e) {
				e.printStackTrace();
			} catch (AccessException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally
			{
				try{
					//zamykamy socket					
					FileImpl tempImpl = new FileImpl(3, metadata.fileName);
					ServerConfiguration.rmiRegistry.rebind(metadata.fileName, tempImpl);
					System.out.println("File already archieved.");
					socket.close();

				} catch (AccessException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}	
	
	private static void loadMetadata(DataInputStream dis)
	{		
		try {
			long metadataSize = dis.readLong();
			byte[] metaBuffer = new byte[(int)metadataSize];
			dis.read(metaBuffer, 0, (int)metadataSize);
			FileOutputStream fos = new FileOutputStream(metaFile);
			fos.write(metaBuffer);
			fos.flush();
			fos.close();
			FileInputStream fis = new FileInputStream(metaFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			metadata = (Metadata)ois.readObject();
			ois.close();
			fis.close();	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveFile(DataInputStream dis)
	{				        
		try {
			File test = new File("backuped_files/" + metadata.fileName);//tutaj uzywam juz wlasciwej nazwy pliku
			test.createNewFile();
			FileOutputStream fos = new FileOutputStream(test);
			int n = 0;
			int i = 0;
			while (fileSize > 0 && (n = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
			{
				fos.write(buffer, 0, n);
				fos.flush();
				fileSize -= n;
				System.out.println("Processed: " + (100 - fileSize * 100 / wholeFileSize) + "%");
				MainWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
				i++;
			}		       
			fos.close();
			//strumien jest pusty, plik gotowy, zmieniamy daty naszego pliku
	        Files.setAttribute(test.toPath(), "creationTime", FileTime.fromMillis(metadata.creationTime));
	        Files.setAttribute(test.toPath(), "lastAccessTime", FileTime.fromMillis(metadata.accessTime));	
	        Files.setAttribute(test.toPath(), "lastModifiedTime", FileTime.fromMillis(metadata.modificationTime));
	        //tutaj mozna sobie zobaczyc te informacje na ekranie konsoli
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			System.out.println("Nazwa: " + metadata.fileName + " Data: " + sdf.format(metadata.modificationTime) + " " +
					sdf.format(metadata.creationTime) + " " + sdf.format(metadata.accessTime) + " - Pakiety: " + i);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}