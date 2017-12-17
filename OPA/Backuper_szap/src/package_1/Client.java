package package_1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;

import package_.FileImpl;
import package_.Metadata;
import package_.MetadataInterface;

public class Client {
	
	private static Socket clientSocket = null;
	private static int packetSize = 8192;
	private static byte[] buffer = new byte[packetSize];
	private static long fileSize;
	private static long wholeFileSize;
	private static Metadata metadata = new Metadata();
	public static Registry registry;
	private static FileImpl tempImpl;
	private static File file;

	public void clientSendingMethod(File f) throws Exception {
		
		file = f;
		registry = LocateRegistry.getRegistry("localhost", 1099);
		
		clientSocket = new Socket("localhost", Config.PORT);
		
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
		
		fileSize = file.length();
		wholeFileSize = fileSize;

		createMetadata();
		
		tempImpl = new FileImpl(1, 0, metadata.fileName, metadata.fileSize, metadata.modificationTime, metadata.creationTime, metadata.accessTime);
		registry.rebind("controller", tempImpl);
		
		System.out.println("Asking if file is already uploaded...");
		System.out.println(metadata.fileName);
		
		MetadataInterface existenceStub = (MetadataInterface) registry.lookup("controller");
		
		while (existenceStub.returnClientAction() == 0) {
			
			existenceStub = (MetadataInterface) registry.lookup("controller");
			Thread.sleep(20);
		}
		
		if (existenceStub.returnClientAction() == 1) {
			
			sendFile(dos);
			dos.close();
			clientSocket.close();
			JOptionPane.showMessageDialog(null, "Sending completed!");
			System.out.println("Sending completed!");
			
			existenceStub = (MetadataInterface) registry.lookup("controller");
			
			while (existenceStub.returnClientAction() != 2) {
				
				existenceStub = (MetadataInterface) registry.lookup("controller");
				Thread.sleep(20);
			}
			
			FileImpl endStub = new FileImpl(0, 0);
			registry.rebind("controller", endStub);
		}
		else if (existenceStub.returnClientAction() == 2) {
			
			JOptionPane.showMessageDialog(null, "File already backuped!");
			System.out.println("File already backuped!");
			dos.close();
			clientSocket.close();
			
			FileImpl endStub = new FileImpl(0, 0);
			registry.rebind("controller", endStub);
		}
	}
	
	public void clientReceivingMethod(String path) {
		
		//tu na razie jest œciernisko...
		
		try {
			
			registry = LocateRegistry.getRegistry("localhost", 1099);
			clientSocket = new Socket("localhost", Config.PORT);
			
			System.out.println("Waiting for server...");
			
			MetadataInterface processStub = (MetadataInterface) registry.lookup("controller");
			
			while (processStub.returnClientAction() == 0) {
				
				processStub = (MetadataInterface) registry.lookup("controller");
				Thread.sleep(20);
			}
			
			if (processStub.returnClientAction() == 1) {
				
				System.out.println("Downloading the file...");

				DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
				
				metadata = processStub.returnMetadata();
				fileSize = metadata.fileSize;
				wholeFileSize = fileSize;
				
				saveFile(dis, path);	
				dis.close();
				
				clientSocket.close();
				FileImpl endStub = new FileImpl(3, 0);
				registry.rebind("controller", endStub);
			}
			else if (processStub.returnClientAction() == 2) {
				
				JOptionPane.showMessageDialog(null, "This file is already in this directory!");
				
				clientSocket.close();
				
				FileImpl endStub = new FileImpl(0, 0);
				registry.rebind("controller", endStub);
			}	
			else if (processStub.returnClientAction() == 3) {
				
				JOptionPane.showMessageDialog(null, "File deleted!");
				
				clientSocket.close();
				
				FileImpl endStub = new FileImpl(0, 0);
				registry.rebind("controller", endStub);
			}	
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createMetadata() {		
		try {
			
			metadata.fileSize = fileSize;
			metadata.fileName = file.getName();
			FileTime modificationDate = (FileTime) Files.getAttribute(file.toPath(), "lastModifiedTime");
			metadata.modificationTime = modificationDate.toMillis();
			FileTime creationDate = (FileTime) Files.getAttribute(file.toPath(), "creationTime");
			metadata.creationTime = creationDate.toMillis();
			FileTime accessDate = (FileTime) Files.getAttribute(file.toPath(), "lastAccessTime");
			metadata.accessTime = accessDate.toMillis();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private static void sendFile(DataOutputStream dos) {
		
		try {
			
			FileInputStream fis = new FileInputStream(file);
			int n = 0;
			int i = 0;

			while (fileSize > 0 && (n = fis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {	
				
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
	
	private static void saveFile(DataInputStream dis, String path) {
		
		try {
			
			File test = new File(path + "/" + metadata.fileName);
			test.createNewFile();
			FileOutputStream fos = new FileOutputStream(test);
			
			int n = 0;
			int i = 0;
			
			while (fileSize > 0 && (n = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1) {
				
				fos.write(buffer, 0, n);
				fos.flush();
				fileSize -= n;
				System.out.println("Processed: " + (100 - fileSize * 100 / wholeFileSize) + "%");
				ClientWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
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
}