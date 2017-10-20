import java.awt.BorderLayout; 
import java.awt.Color; 
import java.awt.Dimension; 
import java.awt.Font; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collections; 
import java.util.Random; 

import javax.swing.JButton; 
import javax.swing.JFrame; 
import javax.swing.JList; 
import javax.swing.JMenu; 
import javax.swing.JMenuBar; 
import javax.swing.JMenuItem; 
import javax.swing.JPanel; 
import javax.swing.JScrollPane; 
import javax.swing.JTextField; 
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@SuppressWarnings("serial")
public class FlashCard extends JFrame implements Runnable 
{ 
  ArrayList<String> sideOneArray = new ArrayList<String>();
  ArrayList<String> sideTwoArray = new ArrayList<String>();
  JList<String> list = null;
  JMenuBar mbar = new JMenuBar(); 
  JTextField cardField = new JTextField(); 
  JPanel cardPanel = new JPanel(); 
  JScrollPane termPanel = new JScrollPane(list); 
  JPanel buttonPanel = new JPanel();
  boolean isNew;
  int currentSide = 1;
  String sideOne;
  String sideTwo;
  JPanel app = new JPanel(); 
  JPanel cardButtons = new JPanel(); 
  int currentCard = 0;
  boolean isSaved = false;
  File currentFile;
  
  public void run() 
  { 
    setSize(650,470); 
    setTitle("Flash Cards Application"); 
    setJMenuBar(mbar); 
    makeFileMenu(); 
    makeColorMenu(); 
    makeBgMenu(); 
    getContentPane().add(app); 
    app.add(cardPanel); 
    app.add(termPanel); 
    app.add(buttonPanel, BorderLayout.SOUTH); 
    makeButtons(); 
    cardPanel.add(cardField); 
    cardField.setDisabledTextColor(Color.BLACK); 
    cardField.setForeground(Color.BLACK); 
    Font font = new Font("SansSerif", Font.BOLD, 20); 
    cardField.setFont(font); 
    cardField.setBackground(Color.WHITE); 
    cardField.setPreferredSize(new Dimension(450,350)); 
    cardField.setHorizontalAlignment(JTextField.CENTER); 
    cardField.setEditable(false); 
    termPanel.setPreferredSize(new Dimension(150, 350)); 
    setVisible(true); 
  } 
  
  public static void main(String [] args) 
  { 
    FlashCard g = new FlashCard(); 
    javax.swing.SwingUtilities.invokeLater(g); 
  } 
  
  class BackgroundMenuItem extends JMenuItem 
  { 
    final Color color; 
    public BackgroundMenuItem(Color c, String name) 
    { 
      super(name); 
      color = c; 
      addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent e) 
        { 
          cardField.setBackground(color); 
          cardField.repaint(); 
        } 
      }); 
    } 
  } 
  
  class ColorMenuItem extends JMenuItem 
  { 
    final Color color; 
    public ColorMenuItem(Color c, String name) 
    { 
      super(name); 
      color = c; 
      addActionListener(new ActionListener(){ 
        public void actionPerformed(ActionEvent e) 
        { 
          cardField.setDisabledTextColor(color); 
          cardField.setForeground(color); 
        } 
      }); 
    } 
  } 
  
  
  public void makeFileMenu() 
  { 
    JMenu fileMenu = new JMenu("File");
    JMenuItem newItem = new JMenuItem("New");
    JMenuItem openItem = new JMenuItem("Open");
    JMenuItem saveItem = new JMenuItem("Save");
    JMenuItem saveAsItem = new JMenuItem("Save as...");
    JMenuItem quitItem = new JMenuItem("Quit"); 
    
    newItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(!isSaved)
        {
          int select = offerToRescueWindowContents();
          if(select == 1)
          {
        	sideOneArray.clear();
          	sideTwoArray.clear();
          	cardField.setText("");
          	reloadList();
          	currentFile = null;
          	isSaved = false;
          }
        }
        else if(isSaved)
        {
        	sideOneArray.clear();
        	sideTwoArray.clear();
        	cardField.setText("");
        	reloadList();
        	currentFile = null;
        	isSaved = false;
        }
      }
    });
    openItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(!isSaved)
        {
          int choice = offerToRescueWindowContents();
          if(choice == 1)
          {
        	sideOneArray.clear();
          	sideTwoArray.clear();
          	cardField.setText("");
          	reloadList();
          	currentFile = null;
          	isSaved = false;
          }
        }
        if(!selectCurrentFileForOpening())
        {
          return;
        }
        loadCurrentFile();
        reloadList();
      }
    });
    saveItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(currentFile == null)
        {
          if(!selectCurrentFileForSaving())
          {
            return;
          }
        }
        writeToCurrentFile();
        isSaved = true;
      }
    });
    saveAsItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {       
        if(!selectCurrentFileForSaving())
        {
          return;
        }
        writeToCurrentFile();
        isSaved = true;
      }
    });
    quitItem.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(isSaved)
        {
          System.exit(0);
          return;
        }
        int select = offerToRescueWindowContents();
        if(select == JOptionPane.YES_OPTION)
        {
          if(currentFile == null)
          {
            if(selectCurrentFileForSaving() == true)
            {
              writeToCurrentFile();
              System.exit(0);
            }
            else
            {
              return;
            }
          }
        }
        if(select == JOptionPane.NO_OPTION)
        {
          System.exit(0);
        }
      }
    });
    
    fileMenu.add(newItem);
    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.add(saveAsItem);
    fileMenu.add(quitItem); 
    mbar.add(fileMenu); 
  } 
  public void makeColorMenu() 
  { 
    JMenu colorMenu = new JMenu("Text-Color"); 
    mbar.add(colorMenu); 
    colorMenu.add(new ColorMenuItem(Color.RED, "Red")); 
    colorMenu.add(new ColorMenuItem(Color.GREEN, "Green")); 
    colorMenu.add(new ColorMenuItem(Color.BLUE, "Blue")); 
    colorMenu.add(new ColorMenuItem(Color.YELLOW, "Yellow")); 
    colorMenu.add(new ColorMenuItem(Color.BLACK, "Black")); 
    colorMenu.add(new ColorMenuItem(Color.WHITE, "White")); 
  } 
  public void makeBgMenu() 
  { 
    JMenu bgMenu = new JMenu("Background"); 
    mbar.add(bgMenu); 
    bgMenu.add(new BackgroundMenuItem(Color.RED, "Red")); 
    bgMenu.add(new BackgroundMenuItem(Color.BLUE, "Blue")); 
    bgMenu.add(new BackgroundMenuItem(Color.GREEN, "Green")); 
    bgMenu.add(new BackgroundMenuItem(Color.YELLOW, "Yellow")); 
    bgMenu.add(new BackgroundMenuItem(Color.BLACK, "Black")); 
    bgMenu.add(new BackgroundMenuItem(Color.WHITE, "White")); 
  } 
  
  public void reloadList()
  {
    String[] tempList = new String[sideOneArray.size()];
    tempList = sideOneArray.toArray(tempList);
    list = new JList<String>(tempList);
    termPanel.setViewportView(list);
  }
  
  public void makeButtons() 
  { 
    JButton shuffleButton = new JButton("Shuffle"); 
    JButton removeButton = new JButton("Remove Card"); 
    JButton previousButton = new JButton("<- Previous"); 
    JButton newButton = new JButton("New"); 
    JButton flipButton = new JButton("Flip"); 
    JButton nextButton = new JButton("Next ->"); 
    
    cardButtons.add(previousButton); 
    cardButtons.add(newButton); 
    cardButtons.add(flipButton); 
    cardButtons.add(nextButton); 
    buttonPanel.add(shuffleButton); 
    buttonPanel.add(removeButton); 
    buttonPanel.add(cardButtons); 
    
    previousButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(currentCard > 0)
        {
          currentCard -= 1;
          sideOne = sideOneArray.get(currentCard);
          sideTwo = sideTwoArray.get(currentCard);
          cardField.setText(sideOne);
        }
        else
        {
          return;
        }
      }
    });
    newButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(isNew != true)
        {
          isNew = true;
          currentSide = 1;
          cardField.setText("");
          cardField.setEditable(true);
          cardField.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
              if(currentSide == 1)
              {
                sideOneArray.add(cardField.getText());
                currentSide = 2;
                cardField.setText("");
              }
              else if(currentSide == 2)
              {
                currentSide = 1;
                sideTwoArray.add(cardField.getText());
                for(ActionListener act : cardField.getActionListeners())
                {
                  cardField.removeActionListener(act);
                }
                cardField.setEditable(false);
                currentCard = sideOneArray.size() - 1;
                sideOne = sideOneArray.get(currentCard);
                sideTwo = sideTwoArray.get(currentCard);
                cardField.setText(sideOne);
                isNew = false;
                isSaved = false;
                reloadList();
              }
            }
          });
        }
      }
    });
    flipButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(currentSide == 1)
        {
          cardField.setText(sideTwo);
          currentSide = 2;
        }
        else if(currentSide == 2)
        {
          cardField.setText(sideOne);
          currentSide = 1;
        }
      }
    });
    nextButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(currentCard < sideOneArray.size() - 1)
        {
          currentCard += 1;
          sideOne = sideOneArray.get(currentCard);
          sideTwo = sideTwoArray.get(currentCard);
          cardField.setText(sideOne);
        }
        else
        {
          return;
        }
      }
    });
    shuffleButton.addActionListener(new ActionListener(){ 
      public void actionPerformed(ActionEvent e) 
      { 
        long seed = System.nanoTime(); 
        Collections.shuffle(sideOneArray, new Random(seed)); 
        Collections.shuffle(sideTwoArray, new Random(seed)); 
        sideOne = sideOneArray.get(0); 
        sideTwo = sideTwoArray.get(0); 
        cardField.setText(sideOne); 
        currentCard = 0;
        isSaved = false;
        reloadList();
      } 
    }); 
    removeButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        if(currentCard == 0)
        {
          if(sideOneArray.size() > 0)
          {
            sideOne = "";
            sideTwo = "";
            sideOneArray.remove(0);
            sideTwoArray.remove(0);
            currentCard -= 1;
            cardField.setText(sideOne);
            isSaved = false;
            reloadList();
          }
          else
          {
            return;
          }
        }
        else if(currentCard > 0)
        {
          sideOneArray.remove(currentCard);
          sideTwoArray.remove(currentCard);
          currentCard -= 1;
          sideOne = sideOneArray.get(currentCard);
          sideTwo = sideTwoArray.get(currentCard);
          cardField.setText(sideOne);
          isSaved = false;
          reloadList();
        }
        else
        {
          return;
        }
      }
    });
  } 
  public boolean selectCurrentFileForSaving()
  {
    JFileChooser jfc = new JFileChooser();
    int choice = jfc.showSaveDialog(FlashCard.this);
    if(choice == JFileChooser.APPROVE_OPTION)
    {
      currentFile = jfc.getSelectedFile();
      return true;
    }
    return false;
  }
  public boolean selectCurrentFileForOpening()
  {
    JFileChooser jfc = new JFileChooser();
    int choice = jfc.showOpenDialog(FlashCard.this);
    if(choice == JFileChooser.APPROVE_OPTION)
    {
      currentFile = jfc.getSelectedFile();
      return true;
    }
    return false;
  }
  public boolean writeToCurrentFile()
  {
    try
    {
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(currentFile));
      oos.writeObject(sideOneArray);
      oos.writeObject(sideTwoArray);
      return true;
    }
    catch(IOException ex)
    {
      return false;
    }
  }
  @SuppressWarnings("unchecked")
  public boolean loadCurrentFile()
  {
    try
    {
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(currentFile));
      ArrayList<String> one = (ArrayList<String>)ois.readObject();
      ArrayList<String> two = (ArrayList<String>)ois.readObject();
      sideOneArray = one;
      sideTwoArray = two;
      sideOne = sideOneArray.get(0);
      sideTwo = sideTwoArray.get(0);
      cardField.setText(sideOne);
      isSaved = true;
      return true;
    }
    catch(IOException ex)
    {
      return false;
    }
    catch(ClassNotFoundException ex)
    {
      return false;
    }
  }
  private int offerToRescueWindowContents()
  {
    int select = JOptionPane.showConfirmDialog(FlashCard.this, "Save set?");
    if(select == JOptionPane.YES_OPTION)
    {
      if(currentFile == null)
      {
        if(selectCurrentFileForSaving() == true)
        {
          writeToCurrentFile();
          isSaved = true;
        }
        else
        {
          return JOptionPane.CANCEL_OPTION;
        }
      }
      else
      {
        writeToCurrentFile();
        isSaved = true;
      }
    }
    return select;
  }
  
  
  
}