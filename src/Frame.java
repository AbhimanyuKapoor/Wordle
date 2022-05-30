import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Frame extends JFrame implements ActionListener, KeyListener
{
	JButton buttons[][]=new JButton[6][5];
	JButton resetButton;
	JButton nextButton;
	
	JLabel label;
	JLabel score;
	
	JPanel buttonsPanel=new JPanel();
	JPanel lowerButtons=new JPanel();
	
	boolean canContinue;
	boolean checked[];
	boolean correct[];
	boolean gameOver;
	
	int chance;
	int letter;
	int points;
	String word;
	
	ImageIcon icon;
	File iconImage;	
	public Frame(int points)
	{		
		iconImage=new File("Wordle.png");
		if(iconImage.exists())
		{
				icon=new ImageIcon("Wordle.png");
				this.setIconImage(icon.getImage());
		}	
		this.points=points;	
		word=new Words().getWord();

		checked=new boolean[5];
		correct=new boolean[5];
		chance=0;
		letter=0;
		canContinue=true;
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setSize(500,600);
		
		label=new JLabel();
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(new Font("MV Boli",Font.BOLD,20));
		label.setForeground(Color.YELLOW);		
		label.setOpaque(true);
		label.setText("WORDLE");
		label.setBackground(Color.BLACK);
		
		score=new JLabel();
		score.setHorizontalAlignment(JLabel.CENTER);
		score.setFont(new Font("MV Boli",Font.BOLD,15));		
		score.setOpaque(true);
		score.setText("Score: "+points+" "); //Space after to make it look clear
		score.setBackground(Color.CYAN);
		
		resetButton=new JButton();
		resetButton.setForeground(Color.BLUE);
		resetButton.setText("Reset");
		resetButton.setFont(new Font("MV Boli", Font.BOLD, 20));
		resetButton.setBackground(Color.BLACK);
		resetButton.setFocusable(false);
		resetButton.addActionListener(this);
		
		nextButton=new JButton();
		nextButton.setForeground(Color.BLUE);
		nextButton.setText("Next");
		nextButton.setFont(new Font("MV Boli", Font.BOLD, 20));
		nextButton.setBackground(Color.BLACK);
		nextButton.setFocusable(false);
		nextButton.addActionListener(this);
		nextButton.setEnabled(false);
		
		buttonsPanel.setVisible(true);
		buttonsPanel.setLayout(new GridLayout(6,5));
		
		lowerButtons.setVisible(true);
		lowerButtons.setLayout(new GridLayout(1,2));
		lowerButtons.add(resetButton);
		lowerButtons.add(nextButton);
		
		for(int i=0; i<buttons.length; i++)
		{
			for(int j=0; j<buttons[0].length; j++)
			{
				buttons[i][j]=new JButton();
				buttons[i][j].setBackground(Color.WHITE);
				buttons[i][j].setEnabled(false);
				buttons[i][j].setFont(new Font("MV Boli", Font.BOLD, 20));
				buttonsPanel.add(buttons[i][j]);
			}
		}
		
		this.add(lowerButtons,BorderLayout.SOUTH);
		this.add(label, BorderLayout.NORTH);
		this.add(score, BorderLayout.EAST);
		this.getContentPane().setBackground(Color.WHITE);
		this.add(buttonsPanel);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.setTitle("Wordle");
		this.repaint();
		this.revalidate(); 
		this.setLocationRelativeTo(null);
	}	
	@Override
	public void keyTyped(KeyEvent e) 
	{			
		
	}
	//Using KeyPressed since it doesn't differentiate between upper and lower-case. Gives same KeyCode
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(!gameOver)
		{
			int code=e.getKeyCode();
			if(code>=65 && code<=90)
			{
				if(canContinue)
				{
					if(letter<5)
					{
						buttons[chance][letter].setText(String.valueOf((char)code));
						letter++;
					}
					else 
						canContinue=false;
				}
			}
			else if(code==10) //10 is enter key
			{
				if(chance<6)
				{
					if(letter==5)
					{
						String wordTyped="";
						for(int i=0; i<5; i++)
							wordTyped=wordTyped+buttons[chance][i].getText();
						
						if(new Words().wordExists(wordTyped.toLowerCase()))
						{
							label.setForeground(Color.YELLOW);
							label.setText("WORDLE");
							
							checkWord(wordTyped.toLowerCase());					
							checkWinner();
							
							letter=0;
							chance++;
							canContinue=true;
						}
						else 
						{
							label.setForeground(Color.RED);
							label.setText("Word Doesn't Exist!");
						}
					}
					else 
					{
						label.setForeground(Color.RED);
						label.setText("Less Letters!");
					}
				}
			}
			else if(code==8) //8 is backspace
			{
				if(letter>0)
				{
					letter--;
					canContinue=true;
					buttons[chance][letter].setText("");
				}
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource()==resetButton)
		{
			this.dispose();
			new Frame(0);
		}
		if(e.getSource()==nextButton)
		{
			this.dispose();
			new Frame(++points);
			//If I do points++, it first passes points then increments, so never increments
		}
	}
	
	public void checkWord(String toBeChecked)
	{
		//Frequency is the number of times the letter of the correct word corresponds to the typed word
		//For example word=super, typed word=seven
		
		//So after the 'e' of super matches with 'e' in seven, 
		//the second e wont have a place since frequency of 'e'(3rd index) in super is 1,
		//so it won't consider the second 'e' of seven
		int[] frequency=new int[5];	
		for(int i=0; i<5; i++)
		{
			checked[i]=false; //Clearing checked for new word;
			correct[i]=false;
			frequency[i]=0; //Making frequency empty
		}
		
		ArrayList<Character> finalWord=new ArrayList<Character>();
		ArrayList<Character> toCharChecked=new ArrayList<Character>();		
		for(int i=0; i<word.length(); i++)
			finalWord.add(word.charAt(i));
		for(int i=0; i<toBeChecked.length(); i++)
			toCharChecked.add(toBeChecked.charAt(i));	
		
		for(int i=0; i<5; i++) //For same position (Green color)
		{
			if((buttons[chance][i].getText().toLowerCase()).equals(finalWord.get(i).toString()))
			{
				buttons[chance][i].setBackground(Color.GREEN);
				checked[i]=true;
				correct[i]=true;
				frequency[i]=1;
			}
		}
		
		for(int i=0; i<toCharChecked.size(); i++) //For if the word just contains that letter (Yellow color)
		{
			for(int j=0; j<finalWord.size(); j++)
			{
				if(finalWord.get(j)==toCharChecked.get(i))
				{
					if(frequency[j]<1) //If the letter of finalWord isn't matched with another at the same position
					{
						if(checked[i]==false) //If that buttons isn't already changed
						{
							buttons[chance][i].setBackground(Color.YELLOW);
							checked[i]=true;
							frequency[j]=1;
							break;
						}
					}
				}
			}
		}
		
		for(int i=0; i<5; i++) //For letters that are not there (GREY)
		{
			if(checked[i]==false)
				buttons[chance][i].setBackground(Color.lightGray);
		}
	}
	
	public void checkWinner()
	{
		if(chance==5)
		{
			label.setBackground(Color.RED);
			label.setForeground(Color.BLACK);
			label.setText("Oops! You Lost. The word is: "+word);
			
			gameOver=true;
			//If person loses they have to restart score doesn't continue
		}
		
		int count=0;
		//Using another array for correct as checked is for both yellow and green
		//Correct is only complete when position and letter is correct: Person Wins
		for(int i=0; i<correct.length; i++)
			if(correct[i]==true)
				count++;
		
		if(count==5)
		{
			label.setBackground(Color.GREEN);
			label.setForeground(Color.BLACK);
			label.setText("Congratulations! You have won!");
			
			gameOver=true;
			nextButton.setEnabled(true);
		}
	}
}