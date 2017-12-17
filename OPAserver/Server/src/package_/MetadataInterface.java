package package_;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MetadataInterface extends Remote {

	Metadata returnMetadata(String name) throws RemoteException;
	Metadata returnMetadata() throws RemoteException;
	int returnServerAction() throws RemoteException;
	int returnClientAction() throws RemoteException;
	String[] returnFiles() throws RemoteException;
}
