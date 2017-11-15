package package_1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client
{
	static Socket clientSocket = null;
	static DataOutputStream dos = null;
	static int packetSize = 8192;
	static byte[] buffer = new byte[packetSize];
	
	public void clientMethod() throws Exception
	{
				Socket clientSocket = new Socket("localhost", Config.PORT);
				FileInputStream fis = new FileInputStream(ClientWindow.myFile);
				dos = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
				System.out.println("Sending...");
				long fileSize = ClientWindow.myFile.length();
				dos.writeLong(fileSize);
				dos.writeUTF(ClientWindow.myFile.getName());
				int n = 0;
				
				while (fileSize > 0 && (n = fis.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)
				{
					dos.write(buffer,0,n);
					dos.flush();	
					fileSize -= n;
				}
				
				dos.close();
				fis.close();
				clientSocket.close();
				System.out.println("Finished.");			
	}
	
}
