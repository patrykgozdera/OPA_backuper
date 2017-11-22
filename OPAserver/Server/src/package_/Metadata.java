package package_;

import java.io.Serializable;

public class Metadata implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1232195441651422792L;
	public long fileSize;
	public String fileName;
	public long modificationTime;
	public long creationTime;
	public long accessTime;
	
	public Metadata()
	{
		
	}
	
}
