package package_;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;


public class ClientHandler implements Runnable
{
	private Socket socket;
    static int byteread;
    static int current = 0;
    
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
				int packetSize = 8192;
				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				String fileName = dis.readUTF();//tu sie wysyla nazwa pliku, moze sie pozniej przyda
				long fileSize = dis.readLong();
				byte[] buffer = new byte[packetSize];//tutaj ustalany jest rozmiar chunka
		        File test = new File("C:\\Users\\mati0\\Desktop\\test\\simpsons.avi");
		        test.createNewFile();
		        FileOutputStream fos = new FileOutputStream(test);     
		        int n = 0;
		        
		        while (fileSize > 0 && (n = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
		        {
		        	fos.write(buffer,0,n);
		            fos.flush();
		            fileSize -= n;
		        }	
		        
		        fos.close();
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
}
