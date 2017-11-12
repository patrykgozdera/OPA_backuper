package package_1;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
		outputStream.write(buffer,0,buffer.length);
		outputStream.flush();
		outputStream.close();
		bin.close();
		clientSocket.close();
		System.out.println("Finished.");
	}
	
}
