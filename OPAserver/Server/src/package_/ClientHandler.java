package package_;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

public class ClientHandler implements Runnable {
	
	private static Socket socket;
    private static int packetSize = 8192;	
    private static byte[] buffer = new byte[packetSize];
    private static long fileSize;
    private static long wholeFileSize;
    private static Metadata metadata;
    
	public ClientHandler(Socket s) throws RemoteException {
		
		socket = s;
	}

	public void run() {
		
		try	{
				
			MetadataInterface actionStub = (MetadataInterface)ServerConfiguration.rmiRegistry.lookup("controller");
				
			while (actionStub.returnServerAction() == 0) {
					
				actionStub = (MetadataInterface)ServerConfiguration.rmiRegistry.lookup("controller");
				Thread.sleep(20);
			}
				
			if (actionStub.returnServerAction() == 1) {
					
				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
					
				MetadataInterface metaStub = (MetadataInterface)ServerConfiguration.rmiRegistry.lookup("controller");
				metadata = metaStub.returnMetadata();
					
				System.out.println("Metadane wczytane!");
					
				fileSize = metadata.fileSize;
				wholeFileSize = fileSize;
					
				String[] list = metaStub.returnFiles();
				int verOfFile = 0;
				String noExtension = metadata.fileName.substring(0, metadata.fileName.lastIndexOf("."));
				String extension = metadata.fileName.substring(metadata.fileName.lastIndexOf("."), metadata.fileName.length());
					
				for (int i = 0; i < list.length; i++) {
					
					if (list[i].contains(noExtension) && list[i].contains(extension)) {
						
						verOfFile++;
					}
				}
					
				if (verOfFile == 0) {
					
					System.out.println("File is not backuped yet!");
						
					FileImpl infoStub = new FileImpl(1, 1);
					ServerConfiguration.rmiRegistry.rebind("controller", infoStub);
						
					saveFile(dis);	
					dis.close();
				}
				else {
					
					System.out.println("There is file called same name! Checking other metadata...");
						
					boolean isTheSame = false;
						
					if (verOfFile == 1)	{
						
						String newName = noExtension + extension;
							
						Metadata tempMetadata = metaStub.returnMetadata(newName);
						long modTime = tempMetadata.modificationTime;
						long crTime = tempMetadata.creationTime;
						long accTime = tempMetadata.accessTime;
						long size = tempMetadata.fileSize;
							
						if (size == metadata.fileSize && modTime == metadata.modificationTime && 
								crTime == metadata.creationTime && accTime == metadata.accessTime) {
							
							JOptionPane.showMessageDialog(null, "This file is already backuped!");
							isTheSame = true;
						}
					}
					else {
						
						String newName = noExtension + extension;
							
						Metadata tempMetadata = metaStub.returnMetadata(newName);
						long modTime = tempMetadata.modificationTime;
						long crTime = tempMetadata.creationTime;
						long accTime = tempMetadata.accessTime;
						long size = tempMetadata.fileSize;
							
						if (size == metadata.fileSize && modTime == metadata.modificationTime && 
								crTime == metadata.creationTime && accTime == metadata.accessTime) {
							
							isTheSame = true;
						}
							
						for (int i = 1; i < verOfFile; i++)	{
							
							newName = noExtension + "(" + i + ")" + extension;
							
							tempMetadata = metaStub.returnMetadata(newName);
							modTime = tempMetadata.modificationTime;
							crTime = tempMetadata.creationTime;
							accTime = tempMetadata.accessTime;
							size = tempMetadata.fileSize;
							
							if (size == metadata.fileSize && modTime == metadata.modificationTime && 
									crTime == metadata.creationTime && accTime == metadata.accessTime) {
								
								isTheSame = true;
							}
						}
					}
						
					if (isTheSame == false)	{
						
						System.out.println("This version of file is not backuped yet!");
						FileImpl infoStub = new FileImpl(1, 1);
						ServerConfiguration.rmiRegistry.rebind("controller", infoStub);
							
						metadata.fileName = noExtension + "(" + verOfFile + ")" + extension;
							
						saveFile(dis);	
						dis.close();
					}
					else {
						
						JOptionPane.showMessageDialog(null, "This file is already backuped!");
						FileImpl infoStub = new FileImpl(0, 2);
						ServerConfiguration.rmiRegistry.rebind("controller", infoStub);
					}		
				}
					
				FileImpl endStub = new FileImpl(0, 2);
				ServerConfiguration.rmiRegistry.rebind("controller", endStub);
			}
			else if (actionStub.returnServerAction() == 2)
			{
				
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				
				MetadataInterface metaStub = (MetadataInterface)ServerConfiguration.rmiRegistry.lookup("controller");
				metadata = metaStub.returnMetadata();
				
				System.out.println("Metadane wczytane!");
				
				fileSize = metadata.fileSize;
				wholeFileSize = fileSize;
				
				FileImpl infoStub = new FileImpl(2, 1, metadata.fileName, metadata.fileSize, metadata.modificationTime, metadata.creationTime, metadata.accessTime);
				ServerConfiguration.rmiRegistry.rebind("controller", infoStub);
				
				sendFile(dos);
				dos.close();
				
				JOptionPane.showMessageDialog(null, "Sending completed!");
				
				actionStub = (MetadataInterface) ServerConfiguration.rmiRegistry.lookup("controller");
				
				while (actionStub.returnServerAction() != 3) {
					
					actionStub = (MetadataInterface) ServerConfiguration.rmiRegistry.lookup("controller");
					Thread.sleep(20);
				}
				
				FileImpl endStub = new FileImpl(0, 0);
				ServerConfiguration.rmiRegistry.rebind("controller", endStub);
			}
			else if (actionStub.returnServerAction() == 3)
			{
				
				MetadataInterface metaStub = (MetadataInterface)ServerConfiguration.rmiRegistry.lookup("controller");
				metadata = metaStub.returnMetadata();
				File df = new File("backuped_files/" + metadata.fileName);
				Files.delete(df.toPath());
				FileImpl endStub = new FileImpl(0, 3);
				ServerConfiguration.rmiRegistry.rebind("controller",  endStub);
			}
		} catch (RemoteException e) {
				
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (NotBoundException e) {
				
			e.printStackTrace();
		} catch (InterruptedException e) {
				
			e.printStackTrace();
		}
		finally
		{

		}
	}
	
	private static void saveFile(DataInputStream dis) {
		
		try {
			
			File test = new File("backuped_files/" + metadata.fileName);
			test.createNewFile();
			FileOutputStream fos = new FileOutputStream(test);
			
			int n = 0;
			int i = 0;
			
			while (fileSize > 0 && (n = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
				
				fos.write(buffer, 0, n);
				fos.flush();
				fileSize -= n;
				System.out.println("Processed: " + (100 - fileSize * 100 / wholeFileSize) + "%");
				MainWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
				i++;
			}
			
			fos.close();

	        Files.setAttribute(test.toPath(), "creationTime", FileTime.fromMillis(metadata.creationTime));
	        Files.setAttribute(test.toPath(), "lastAccessTime", FileTime.fromMillis(metadata.accessTime));	
	        Files.setAttribute(test.toPath(), "lastModifiedTime", FileTime.fromMillis(metadata.modificationTime));

	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			System.out.println("Nazwa: " + metadata.fileName + " Data: " + sdf.format(metadata.modificationTime) + " " +
					sdf.format(metadata.creationTime) + " " + sdf.format(metadata.accessTime) + " - Pakiety: " + i);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private static void sendFile(DataOutputStream dos) {
		
		try {
			
			FileInputStream fis = new FileInputStream("backuped_files/" + metadata.fileName);
			int n = 0;
			int i = 0;

			while (fileSize > 0 && (n = fis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {	
				
				dos.write(buffer,0,n);
				dos.flush();	
				
				fileSize -= n;		
				MainWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
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