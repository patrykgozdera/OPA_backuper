package package_;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerConfiguration extends UnicastRemoteObject implements Runnable
{
	private static final long serialVersionUID = 3482685354083246197L;
	private int port;
	static ServerSocket serverSocket = null;
	public static Registry rmiRegistry;
	
	public ServerConfiguration(int port) throws RemoteException
	{
		this.port = Config.PORT;
	} 
	
	public void startRMI() throws Exception {
		rmiRegistry = LocateRegistry.createRegistry(1099);
		rmiRegistry.bind("Server",this);
		System.out.println("server started");
		File folder = new File("backuped_files/");
		File[] listOfFiles = folder.listFiles();
		for(int i = 0; i < listOfFiles.length; i++)
		{
			FileImpl tempImpl = new FileImpl(3, listOfFiles[i].getName());
			rmiRegistry.bind(listOfFiles[i].getName(), (MetadataInterface) exportObject(tempImpl, 0));
		}	
	}
	
	public void start() throws IOException
	{
		try
		{
			serverSocket = new ServerSocket(port);
			startRMI();
			System.out.println("Waiting for incoming clients' connection...");
		}
		catch (Exception e) 
		{
			System.out.println("Cannot listen on this port:" + port);
		}
				
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
			System.out.println("Server app is running");
			start();
			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
