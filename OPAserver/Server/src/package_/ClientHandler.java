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
    	double i = 0;
	public ClientHandler(Socket s)
	{
		socket = s;
	}
	
	public void turnon()
	{
		try
		{
			try
			{
				
				int packetsize = 50;
				File test = new File("C:\\Users\\Patryk-student ELKI\\Desktop\\test\\BOT_LAB2_Patryk_Gozdera.pdf");
				FileOutputStream fos = new FileOutputStream(test);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				int nosofpackets = ((int) (new File("D:\\aaa STUDIA SEM.5\\bot\\BOT_LAB2_Patryk_Gozdera.pdf")).length())/packetsize;
				InputStream is = socket.getInputStream();
				byte[] mybytearray = new byte[packetsize];
				//for (double i=0; i<Math.abs(nosofpackets) + 1; i++)
				
					
					while ((byteread = is.read(mybytearray, 0, mybytearray.length)) != -1 && i<(Math.abs(nosofpackets)))
							{
						System.out.println("Packet: " + (i+1));
					bos.write(mybytearray, 0, mybytearray.length);
					i++;
				
							}
					
							
				bos.flush();
				fos.close();
		      	
				
				/*
				byte[] buffer = new byte[maxsize];
				InputStream is = socket.getInputStream();
		        File test = new File("C:\\Users\\Patryk-student ELKI\\Desktop\\test\\e.ova");
		        test.createNewFile();
		        FileOutputStream fos = new FileOutputStream(test);
		        BufferedOutputStream bos = new BufferedOutputStream(fos);      

		        while ((byteread = is.read(buffer, 0, buffer.length)) != -1)
		        {
		        	 bos.write(buffer, 0, byteread);
		        }

		        bos.flush();		        
		        fos.close();
		        is.close();
		        */
		        
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
	
	public void run()
	{
		try 
		{
			turnon();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
