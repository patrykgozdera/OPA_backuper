package package_1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
public class Instruction extends JFrame{
 
    private static final long serialVersionUID = 1L;
   
    JTextArea text;
    JTextField textField;
   
    public Instruction()
    {
    	super("Instruction");
		setPreferredSize(new Dimension(1300,300));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);   
        setResizable(false);
               
        text = new JTextArea();        
        
        this.add(text);
            File file = new File("klient_instrukcja.txt");
            try {
                @SuppressWarnings("resource")
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()){
                    text.append(scanner.nextLine() + "\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            text.setEditable(false);
            text.setForeground(Color.yellow);
            text.setBackground(Color.black);
            text.setOpaque(true);
            Font font = text.getFont();
            float size = font.getSize() + 7.0f;
            text.setFont( font.deriveFont(size) );
            pack();
            this.setVisible(true);
        }
    }
