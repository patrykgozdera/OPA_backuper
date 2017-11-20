package package_;

import package_1.ClientWindow;
import package_1.Metadata;
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
import java.text.SimpleDateFormat;

public class ClientHandler implements Runnable
{
	private static Socket socket;
    private static int packetSize = 8192;	
    private static byte[] buffer = new byte[packetSize];
    private static long fileSize;
    private static long wholeFileSize;
    private static Metadata metadata = new Metadata();
    private static File metaFile; 
    
	public ClientHandler(Socket s)
	{
		socket = s;
	}

	public void run()
	{
		try
		{
			try
			{
				int i=0;
				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				while(dis.available() > 0)
				{
					metaFile = new File("temp/metadata"+i+".ser");
					metaFile.createNewFile();				
					//wczytujemy metadane
					loadMetadata(dis);
					fileSize = metadata.fileSize;
					wholeFileSize = fileSize;
					File maybe = new File("C:\\Users\\Patryk-student ELKI\\Desktop\\test\\" + metadata.fileName);
					System.out.println(maybe.exists());
					System.out.println(maybe.length() == metadata.fileSize);
					//TUTAJ
					//TUTAJ
					//TUTAJ
					//nie wiem dlaczego te stringi sie roznia, nawet jesli kopiuje te same pliki :C
					System.out.println(maybe.getName() == metadata.fileName);//zwraca false, chujek
					//
					//
					//
					if (maybe.exists() == true && maybe.getName() != metadata.fileName && maybe.length() == metadata.fileSize)
					{
						System.out.println("Plik o takiej nazwie istnieje!");
						FileTime modificationDate = (FileTime) Files.getAttribute(maybe.toPath(), "lastModifiedTime");
						long modTime = modificationDate.toMillis();
						FileTime creationDate = (FileTime) Files.getAttribute(maybe.toPath(), "creationTime");
						long crTime = creationDate.toMillis();
						FileTime accessDate = (FileTime) Files.getAttribute(maybe.toPath(), "lastAccessTime");
						long accTime = accessDate.toMillis();
						
						if (modTime == metadata.modificationTime && crTime == metadata.creationTime && accTime == metadata.accessTime)
						{
							System.out.println("Taki sam plik juz istnieje!");
							//przerwij polaczenie
						}
						else
						{
							System.out.println("Plik w tej wersji jeszcze nie istnieje!");
							saveFile(dis);
						}
					}
					else
					{
						System.out.println("Taki plik jeszcze nie istnieje!");
						//dalej standard, odczytujemy to co zostalo w strumieniu (czyli nasz plik)
						saveFile(dis);
					}
					i++;
				
				}
				dis.close();
			}
			finally
			{
				System.out.println("File already archieved.");
				socket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
			File test = new File("C:\\Users\\Patryk-student ELKI\\Desktop\\test\\" + metadata.fileName);//tutaj uzywam juz wlasciwej nazwy pliku
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