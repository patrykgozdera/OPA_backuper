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
	static int maxsize = 999999999;
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
				/*
				int packetsize = 8192;
				File test = new File("C:\\Users\\Patryk-student ELKI\\Desktop\\test\\e.ova");
				FileOutputStream fos = new FileOutputStream(test);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				double nosofpackets = Math.ceil(((int) (new File("D:\\Kali.ova")).length()) / packetsize);
				for (double i=0; i<nosofpackets + 1; i++)
				{
					InputStream is = socket.getInputStream();
					byte[] mybytearray = new byte[packetsize];
					int bytesRead = is.read(mybytearray, 0, mybytearray.length);
					System.out.println("Packet: " + (i+1));
					bos.write(mybytearray, 0, mybytearray.length);
				}				
				bos.flush();
				fos.close();
		      	*/
				
				
				
				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				String fileName = dis.readUTF();//tu sie wysyla nazwa pliku, moze sie pozniej przyda
				long longFileSize = dis.readLong();
				int intFileSize = dis.readInt();
				byte[] buffer = new byte[intFileSize];//zamiast maxsize
		        File test = new File("C:\\Users\\mati0\\Desktop\\test\\simpsons.avi");
		        test.createNewFile();
		        FileOutputStream fos = new FileOutputStream(test);     
		        int n = 0;
		        
		        while (longFileSize > 0 && (n = dis.read(buffer, 0, (int)Math.min(buffer.length, longFileSize))) != -1)
		        {
		        	fos.write(buffer,0,n);
		            fos.flush();
		            longFileSize -= n;
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
