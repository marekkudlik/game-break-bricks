package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GameBody extends JPanel implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 500, HEIGHT = 500;
	private Ball ball;
	private Brick brick;
	private ArrayList<Brick> bricks;
	private ArrayList<Brick> bricksDraw;
	private Palet palet;
	private Thread thread;
	private Boolean running;
	private int time = 0;
	private Boolean right = false, left = false;
	private int paletXCoor = 150, paletYCoor = 450, paletWidth = 75, paletHeight = 15;
	private int brickXCoor = 20, brickYCoor = 20, brickWidth = 80, brickHeight = 15;
	private int ballXCoor = 150, ballYCoor = 435, ballWidth = 15, ballHeight = 15;
	private int ballDirX = 1;
	private int ballDirY = 1;
	private int speed = 50000;

	public GameBody() {
		setFocusable(true);
		addKeyListener(this);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		bricks = new ArrayList<Brick>();
		bricksDraw = new ArrayList<Brick>();
		start();
	}

	public void play() {
		if (palet == null) {
			palet = new Palet(paletXCoor, paletYCoor, paletWidth, paletHeight);
		}
		if (bricks.size() < 5) {
			for (int i = 0; i < 5; i++) {
				brick = new Brick(brickXCoor, brickYCoor, brickWidth, brickHeight);
				brickXCoor += WIDTH / 5 - 5;
				bricks.add(brick);
				bricksDraw.add(brick);
			}
		}
		if (ball == null) {
			ball = new Ball(ballXCoor, ballYCoor, ballWidth, ballHeight);
		}

		time++;
		if (time > speed) {

			if (paletXCoor > WIDTH - paletWidth) {
				paletXCoor = 0;
			}
			if (paletXCoor < 0) {
				paletXCoor = WIDTH - paletWidth;
			}
			if (left) {
				paletXCoor--;
			}
			if (right) {
				paletXCoor++;
			}
			ballXCoor -= ballDirX;
			ballYCoor -= ballDirY;
			for (int i = 0; i < bricksDraw.size(); i++) {
				if (new Rectangle(bricksDraw.get(i).xCoor, bricksDraw.get(i).yCoor, brickWidth, brickHeight)
						.intersects(new Rectangle(ballXCoor, ballYCoor, ballWidth, ballHeight))) {
					bricksDraw.remove(i);
					System.out.println(bricksDraw.size());
					ballDirY = -ballDirY;
				}
			}

			if (new Rectangle(paletXCoor, paletYCoor, paletWidth, paletHeight)
					.intersects(new Rectangle(ballXCoor, ballYCoor, ballWidth, ballHeight))) {
				ballDirY = -ballDirY;
			}
			if (ballXCoor > WIDTH - ball.width) {
				ballDirX = -ballDirX;
			}
			if (ballXCoor < 0) {
				ballDirX = -ballDirX;
			}
			if (ballYCoor > HEIGHT) {
				stop();
			}
			if (ballYCoor < 0) {
				ballDirY = -ballDirY;
			}

			ball = new Ball(ballXCoor, ballYCoor, ballWidth, ballHeight);
			palet = new Palet(paletXCoor, paletYCoor, paletWidth, paletHeight);
			time = 0;
		}

	}

	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		palet.draw(g);
		ball.draw(g);
		for (int i = 0; i < bricksDraw.size(); i++) {
			bricksDraw.get(i).draw(g);
		}		
		if (bricksDraw.size() == 0) {
			g.drawString("you won!", WIDTH / 2, HEIGHT / 2);
			stop();
		}
		if (ballYCoor > HEIGHT) {
			g.drawString("you lost!", WIDTH / 2, HEIGHT/2);
		}

	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (running) {
			play();
			repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_RIGHT) {
			right = true;
			left = false;
		}
		if (key == KeyEvent.VK_LEFT) {
			left = true;
			right = false;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_RIGHT) {
			right = false;
			left = false;
		}
		if (key == KeyEvent.VK_LEFT) {
			right = false;
			left = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

}
