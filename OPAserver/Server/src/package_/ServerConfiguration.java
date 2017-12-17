package package_;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

public class ServerConfiguration extends UnicastRemoteObject implements Runnable {
	
	private static final long serialVersionUID = 3482685354083246197L;
	private int port;
	static ServerSocket serverSocket = null;
	public static Registry rmiRegistry;
	public String path;
	
	public ServerConfiguration(int port) throws RemoteException {
		
		this.port = Config.PORT;
		File tempFile = new File("backuped_files/");
		path = tempFile.getPath();
	} 
	
	public void startRMI() throws Exception {
		
		rmiRegistry = LocateRegistry.createRegistry(1099);
		rmiRegistry.bind("Server", this);
		
		FileImpl controlObject = new FileImpl(0, 0);
		rmiRegistry.bind("controller", controlObject);
		
		ListImpl filesObject = new ListImpl();
		rmiRegistry.bind("files", filesObject);
		
		System.out.println("Server started");
	}
	
	public void start() throws IOException {
		
		try {
			
			serverSocket = new ServerSocket(port);
			startRMI();
			JOptionPane.showMessageDialog(null, "Waiting for incoming clients' connection...");
			System.out.println("Waiting for incoming clients' connection...");
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Cannot listen on this port:" + port);
			System.out.println("Cannot listen on this port:" + port);
		}
				
		for(;;) {
			
			try {
				
				Socket clientSocket = serverSocket.accept();
				Runnable r = new ClientHandler(clientSocket);
				Thread t = new Thread(r);
				t.start();
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Cannot accept connection.");
				System.err.println("Cannot accept connection.");
				System.exit(1);
			}	
			
			System.out.println("Connection succesfull");
			System.out.println("Waiting for input...");
		}
	}
	
	public void run() {
		
		try {
			
			System.out.println("Server app is running");
			start();
			
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
