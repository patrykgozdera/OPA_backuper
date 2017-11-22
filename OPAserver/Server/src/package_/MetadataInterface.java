package package_;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MetadataInterface extends Remote{

	Metadata returnMetadata() throws RemoteException;
	int fileState() throws RemoteException;
}
