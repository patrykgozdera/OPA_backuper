package package_;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

public class MainClass 
{
	public static void main(String[] args)
	{
		new MainWindow();
		System.out.println("elo");
		String configPath = "config.properties";
		ConfigReader readConfig = new ConfigReader();
		try
		{
		Properties prop = readConfig.getProperties(configPath);
        Config.readConstants(prop);        		
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,"exception",null,JOptionPane.WARNING_MESSAGE);
            System.out.println(e);
        }
	}
}
