package package_;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConfiguration implements Runnable
{
	private int port;
	static ServerSocket serverSocket = null;
	
	public ServerConfiguration(int port_) 
	{
		this.port = port_;
	} 
	
	public void start() throws IOException
	{
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) 
		{
			System.out.println("Cannot listen on this port:" + port);
		}
		
		System.out.println("Waiting for incoming clients' connection...");
		for(;;)
		{
			try
			{
				Socket clientSocket = serverSocket.accept();
				Runnable r = new ClientHandler(clientSocket);
				Thread t = new Thread(r);
				t.start();
			}
			catch (Exception e) 
			{
				System.err.println("Cannot accept connection.");
				System.exit(1);
			}
			System.out.println("Connection succesfull");
			System.out.println("Waiting for input...");
		}
		
	}
	
	public void run()
	{
		try
		{
			start();
			System.out.println("Server app is running");
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
