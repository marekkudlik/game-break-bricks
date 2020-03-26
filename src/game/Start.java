package game;

import javax.swing.JFrame;

public class Start {
	
	public Start() {
		
		JFrame frame = new JFrame("Game Breakout");
		GameBody panel = new GameBody();
		frame.add(panel);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(2);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Start();		
	}

}
