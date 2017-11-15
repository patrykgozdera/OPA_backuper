package package_;

import java.io.BufferedOutputStream;
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
				
				
				byte[] buffer = new byte[maxsize];
				InputStream is = socket.getInputStream();
		        File test = new File("C:\\Users\\Patryk-student ELKI\\Desktop\\test\\e.ova");
		        test.createNewFile();
		        FileOutputStream fos = new FileOutputStream(test);
		        BufferedOutputStream out = new BufferedOutputStream(fos);      

		        while ((byteread = is.read(buffer, 0, buffer.length)) != -1)
		        {
		        	 out.write(buffer, 0, byteread);
		        }

		        out.flush();		        
		        fos.close();
		        is.close();
		        
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
