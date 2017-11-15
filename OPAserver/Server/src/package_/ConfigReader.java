package package_;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

public class ConfigReader 
{
	/**
	 * Metoda s�u��ca do wczytywania zawarto�ci z pliku konfiguracyjnego z pliku properties.
	 * 
	 * @param propFilePath �cie�ka dost�pu do pliku konfiguracyjnego.
	 * @return obiekt Properties zawieraj�cy dane z pliku
	 * @throws IOException	w przypadku niepowodzenia z wczytywaniem pliku.
	 */
    public final Properties getProperties(String propFilePath) throws IOException 
    {
        Properties prop = new Properties();
        
        try (InputStream inputStream = new FileInputStream(propFilePath)) 
        {
            prop.load(inputStream);
        }
        catch (Exception e)
        {
        	JOptionPane.showMessageDialog(null,e,null,JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }
        
        return prop; 
 
    }
    
    /**
     * metoda s�u��ca do zamiany listy �a�cuch�w znak�w na obiekt klasy Properties 
     * @param recievedData - lista string�w z parametrami w formacie key=value
     */

	public static Properties parseProperties(ArrayList<String> recievedData) 
	{
		Properties prop = new Properties();
		
		for(int i=0; i<recievedData.size(); i++)
		{
			String[] keyAndValue=new String[2];
			String line = recievedData.get(i);
			keyAndValue = line.split("=");
			prop.setProperty(keyAndValue[0], keyAndValue[1]);
		}
		
		return prop;		
	}
}
