package package_;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private static String fileName;
    private static long modificationDate;
    private static long creationDate;
    private static long accessDate;    
    
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
				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				buffer = new byte[packetSize];//tutaj ustalany jest rozmiar chunka
				//wczytujemy metadane
				loadMetadata(dis); 
				//dalej standard, odczytujemy to co zostalo w strumieniu (czyli nasz plik)
				saveFile(dis);
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
			//odczytuje wszystkie metadane, wazna jest kolejnosc, odczyt w tej samej co wczesniej zapis do strumienia
			//po odczytaniu dane sa usuwane ze strumienia
			//odczytuje rozmiar przesylanego pliku
			fileSize = dis.readLong();
			wholeFileSize = fileSize;
			//odczytuje nazwe pliku
			fileName = dis.readUTF();		        
			//odczytuje date modyfikacji
			modificationDate = dis.readLong();
			//odczytuje date utworzenia
			creationDate = dis.readLong();
			//odczytuje date ostatniego uzywania
			accessDate = dis.readLong();		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveFile(DataInputStream dis)
	{				        
		try {
			File test = new File("C:\\Users\\Patryk-student ELKI\\Desktop\\test\\" + fileName);//tutaj uzywam juz wlasciwej nazwy pliku
			test.createNewFile();
			FileOutputStream fos = new FileOutputStream(test);
						
			int n = 0;
			int i = 0;
			while (fileSize > 0 && (n = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
			{
				fos.write(buffer,0,n);
				fos.flush();
				fileSize -= n;
				System.out.println("Processed: " + (100 - fileSize * 100 / wholeFileSize) + "%");
				MainWindow.progressBar.setValue((int)(100 - fileSize * 100 / wholeFileSize));
				i++;
			}		       
			fos.close();
			dis.close();
	        //strumien jest pusty, plik gotowy, zmieniamy daty naszego pliku
	        Files.setAttribute(test.toPath(), "creationTime", FileTime.fromMillis(creationDate));
	        Files.setAttribute(test.toPath(), "lastAccessTime", FileTime.fromMillis(accessDate));	
	        Files.setAttribute(test.toPath(), "lastModifiedTime", FileTime.fromMillis(modificationDate));
	        //tutaj mozna sobie zobaczyc te informacje na ekranie konsoli
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			System.out.println("Nazwa: " + fileName + " Data: " + sdf.format(modificationDate) + " " +
					sdf.format(creationDate) + " " + sdf.format(accessDate) + " - Pakiety: " + i);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}