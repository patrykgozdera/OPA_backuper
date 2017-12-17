package package_1;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TimeWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 6684812666279941748L;
	
	private static JComboBox<String> comboBoxDate;
	private static JComboBox<String> comboBoxTime;
	private static JComboBox<String> comboBoxPeriod; 
	private static JButton button;
	private static long period;
	private static String time;
	private static String date_ = "2017-12-09";
	private static List<File> listF = new ArrayList<File>();
	
	public TimeWindow() {
		
		
		super("Schedule");
		setPreferredSize(new Dimension(400,400));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {

			e.printStackTrace();
		}
		
		setLayout(new GridLayout(4, 1, 7, 7));
		setLocation(10, 10);

		String[] dates = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
		String[] times = { "now", "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00",
							"12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" };
		String[] periods = { "30s", "1h", "12h", "24h", "7 days" };
		
		comboBoxDate = new JComboBox<String>(dates);
		comboBoxDate.setSelectedIndex(-1);
		comboBoxDate.addActionListener(this);
		((JLabel)comboBoxDate.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		comboBoxTime = new JComboBox<String>(times);
		comboBoxTime.setSelectedIndex(-1);
		comboBoxTime.addActionListener(this);
		((JLabel)comboBoxTime.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		comboBoxPeriod = new JComboBox<String>(periods);
		comboBoxPeriod.setSelectedIndex(-1);
		comboBoxPeriod.addActionListener(this);
		((JLabel)comboBoxPeriod.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		button = new JButton("Accept");
		button.addActionListener(this);
		
		add(comboBoxPeriod);
		add(comboBoxTime);
		add(comboBoxDate);
		add(button);
		
		pack();
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		Object source = e.getSource();
		
		if (source == comboBoxPeriod)
		{
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>)source;
	        String select = (String)cb.getSelectedItem();
	        
	        if(select.equals("30s"))
	        {
	        	period = 30000;
	        }
	        else if(select.equals("1h"))
	        {
	        	period = 3600000;
	        }
	        else if(select.equals("12h"))
	        {
	        	period = 43200000;
	        }
	        else if(select.equals("24h"))
	        {
	        	period = 86400000;
	        }
	        else if(select.equals("7 days"))
	        {
	        	period = 604800000;
	        }
	        System.out.println(period);
		}
		else if (source == comboBoxTime)
		{
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>)source;
	        String select = (String)cb.getSelectedItem();
	        time = select;
	        System.out.println(select);
		}
		else if (source == comboBoxDate)
		{
			@SuppressWarnings("unchecked")
			JComboBox<String> cb = (JComboBox<String>)source;
	        String select = (String)cb.getSelectedItem();
	        
	        if(select.equals("Monday"))
	        {
	        	date_ = "2017-12-11";
	        }
	        else if(select.equals("Tuesday"))
	        {
	        	date_ = "2017-12-12";
	        }
	        else if(select.equals("Wednesday"))
	        {
	        	date_ = "2017-12-13";
	        }
	        else if(select.equals("Thursday"))
	        {
	        	date_ = "2017-12-14";
	        }
	        else if(select.equals("Friday"))
	        {
	        	date_ = "2017-12-15";
	        }
	        else if(select.equals("Saturday"))
	        {
	        	date_ = "2017-12-16";
	       	}
	        else if(select.equals("Sunday"))
	        {
	        	date_ = "2017-12-17";
	        }
		}
		else if (source == button)
		{			
			System.out.println(period);
			System.out.println(time);
		    DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		    Date date = null;
		    if (time == "now")
		    {
		    	date = new Date();
		    }
		    else
		    {
				try {
					date = dateFormatter.parse(date_ + " " + time);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    }
		    System.out.println(date);
		    System.out.println(period);
			Timer timer = new Timer();
			timer.schedule(new MyTimeTask(), date, period);
		}
	}
	
	private static class MyTimeTask extends TimerTask
	{

	    public void run()
	    {	    	
	    	listF.addAll(ClientWindow.filesToSend);
	    	ClientWindow.filesToSend.clear();
	    	
			for (int i = 0; i < listF.size(); i++) {
				
		    	Client cl = new Client();
				try {
					
					cl.clientSendingMethod(listF.get(i));
				} catch (Exception e1) {
					
					e1.printStackTrace();						
				}
			}
	    }
	}
}
