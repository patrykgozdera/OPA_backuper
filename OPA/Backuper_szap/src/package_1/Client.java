package package_1;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client
{
	static Socket clientSocket = null;
	static int len;
	double i = 0;
	//static OutputStream outputStream = null;
	//static byte[] buffer = new byte[(int) ClientWindow.myFile.length()];
	
	public void clientMethod() throws IOException
	{
			/*
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
			*/
		
		
		
				clientSocket = new Socket("localhost", Config.PORT);
				int packetsize = 50;
				int nosofpackets = ((int) ClientWindow.myFile.length())/packetsize;
				FileInputStream fis = new FileInputStream(ClientWindow.myFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				DataInputStream dis = new DataInputStream(bis);
				len = 0;
				
				//for(double i=0; i<Math.abs(nosofpackets) + 1; i++)
				
					byte[] mybytearray = new byte[packetsize];
					bis.read(mybytearray, 0, mybytearray.length);					
					OutputStream outputStream = clientSocket.getOutputStream();
					while ((len = dis.read(mybytearray, 0, mybytearray.length)) != -1 || i<(Math.abs(nosofpackets)))
					{
					System.out.println("Packet: " + (i+1));
					outputStream.write(mybytearray, 0, mybytearray.length);
					i++;
					}
					outputStream.flush();
				
				bis.close();
		
				
				clientSocket.close();
				System.out.println("Finished.");
				//outputStream.close();

		
		
		
			
		
	}
	
}
