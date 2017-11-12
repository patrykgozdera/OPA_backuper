package package_1;

import java.util.Properties;

public class Config 
{
	public static String IP;
	public static int PORT;
	
	/**
     * metoda sczytuj¹ca sta³e 
     */
    public static void readConstants(Properties config)
    {
    	IP = config.getProperty("IP");
    	PORT = Integer.parseInt(config.getProperty("PORT"));                  
    }
}
