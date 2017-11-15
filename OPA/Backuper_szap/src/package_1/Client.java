package package_1;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client
{
	static Socket clientSocket = null;
	static OutputStream outputStream = null;
	static byte[] buffer = new byte[(int) ClientWindow.myFile.length()];
	
	public void clientMethod() throws Exception
	{
		
				Socket clientSocket = new Socket("localhost", Config.PORT);
				FileInputStream fis = new FileInputStream(ClientWindow.myFile);
				BufferedInputStream bin = new BufferedInputStream(fis);
				bin.read(buffer,0,buffer.length);
				outputStream = clientSocket.getOutputStream();
				System.out.println("Sending...");
				outputStream.write(buffer, 0, buffer.length);
				outputStream.flush();
				outputStream.close();
				bin.close();
				clientSocket.close();
				System.out.println("Finished.");			
	
		
		/*
		try
		{
			try
			{
				clientSocket = new Socket("localhost", Config.PORT);
				int packetsize = 8192;
				double nosofpackets = Math.ceil(((int) ClientWindow.myFile.length()) / packetsize);
				FileInputStream fis = new FileInputStream(ClientWindow.myFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				for(double i=0; i<nosofpackets; i++)
				{
					byte[] mybytearray = new byte[packetsize];
					bis.read(mybytearray, 0, mybytearray.length);
					System.out.println("Packet: " + (i+1));
					outputStream = clientSocket.getOutputStream();
					outputStream.write(mybytearray, 0, mybytearray.length);
					outputStream.flush();
				}
				bis.close();
			}
			finally 
			{
				
				clientSocket.close();
				System.out.println("Finished.");
			}
		}		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		//outputStream.close();
		*/
			
		
	}
	
}
