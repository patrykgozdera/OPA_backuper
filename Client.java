package package_1;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

public class Client 
{
	private static Socket clientSocket = null;
	private static int packetSize = 8192;
	private static byte[] buffer = new byte[packetSize];
	private static long fileSize;
	private static long wholeFileSize;
	

	public void clientMethod() throws Exception
	{
				clientSocket = new Socket("localhost", Config.PORT);
				FileInputStream fis = new FileInputStream(ClientWindow.myFile);
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
				System.out.println("Sending...");
				//zaczynam od wrzucania metadanych do strumienia
				writeMetadata(dos);
				//dalej bez zmian, do strumienia wrzucamy nasz pliczek
				sendFile(fis, dos);
				dos.close();
				fis.close();
				clientSocket.close();
				System.out.println("Finished.");			
	}
	
	private static void writeMetadata(DataOutputStream dos)
	{		
		try {
			//pobieram dlugosc pliku i zapisuje ja w strumieniu
			fileSize = ClientWindow.myFile.length();//robie zmienna, bo potem jest potrzebna w petli
			wholeFileSize = fileSize;
			dos.writeLong(fileSize);
			dos.flush();
			//zapisuje nazwe pliku w strumieniu
			dos.writeUTF(ClientWindow.myFile.getName());
			dos.flush();
			//zapisuje date modyfikacji w strumieniu
			FileTime modificationDate = (FileTime) Files.getAttribute(ClientWindow.myFile.toPath(), "lastModifiedTime");
			long modDate = modificationDate.toMillis();
			dos.writeLong(modDate);
			dos.flush();
			//zapisuje date utworzenia w strumieniu
			FileTime creationDate = (FileTime) Files.getAttribute(ClientWindow.myFile.toPath(), "creationTime");
			long crDate = creationDate.toMillis();
			dos.writeLong(crDate);
			dos.flush();
			//zapisuje date ostatniego uzywania w strumieniu
			FileTime accessDate = (FileTime) Files.getAttribute(ClientWindow.myFile.toPath(), "lastAccessTime");
			long acDate = accessDate.toMillis();
			dos.writeLong(acDate);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void sendFile(FileInputStream fis, DataOutputStream dos)
	{		
		try {
			int n = 0;
			int i = 0;
	
				while (fileSize > 0 && (n = fis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
				{					
					dos.write(buffer,0,n);
					dos.flush();						
					fileSize -= n;		
					ClientWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
					System.out.println("Processed: " + (100 - fileSize * 100 / wholeFileSize) + "%");					
					i++;
				}
			System.out.println("Pakiety: " + i);		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
		
}