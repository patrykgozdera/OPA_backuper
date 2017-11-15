package package_;

import java.util.Properties;

public class Config 
{
	public static String IP;
	public static int PORT;
	
	/**
     * metoda sczytujaca stale 
     */
    public static void readConstants(Properties config)
    {
    	IP = config.getProperty("IP");
    	PORT = Integer.parseInt(config.getProperty("PORT"));                  
    }
}
