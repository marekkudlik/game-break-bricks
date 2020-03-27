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
	private int speed = 30000;
	private boolean pause = false;
	private boolean gameStarted = true;
	private boolean level1 = true, level2 = false, level3;
	private int bricksInRow = 5;
	private int oneRow = 1, twoRows = 2, threeRows = 3;

	public GameBody() {
		setFocusable(true);
		addKeyListener(this);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		bricks = new ArrayList<Brick>();
		bricksDraw = new ArrayList<Brick>();
		makePaletAndBall();
		start();
	}

	private void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		while (running) {
			play();
			repaint();
			if (pause) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void play() {
		makeRowIfNeeded();
		time++;
		makeAllThingsHappen();
		checkPlayMore();
	}

	private void checkPlayMore() {
		if (checkIfLost()) {
			stop();
		}
		if (checkIfWon()) {
			stop();
		}
	}

	private boolean checkIfLost() {
		if (ballYCoor > HEIGHT) {
			return true;
		}
		return false;
	}

	private boolean checkIfWon() {
		if (!level1 && !level2 && !level3 && paletIntersectsTheBall()) {
			return true;
		}
		return false;
	}

	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		palet.draw(g);
		ball.draw(g);
		if (level1 || level2 || level3) {
			drawBricks(g);
		}
	}

	private void drawBricks(Graphics g) {
		for (int i = 0; i < bricksDraw.size(); i++) {
			bricksDraw.get(i).draw(g);
		}
	}

	private void makeAllThingsHappen() {
		if (time > speed) {
			makePaletMove();
			movePaletIfCrossedTheWidth();
			moveBall();
			removeBrickIfHit();
			changeLevelIfPassed();
			changeDirectionIfHitThePalet();
			changeDirectionIfHitWall();
			makeBallInNewCoordinates();
			makePaletInNewCoordinates();
			time = 0;
		}
	}

	private void makePaletInNewCoordinates() {
		palet = new Palet(paletXCoor, paletYCoor, paletWidth, paletHeight);
	}

	private void makeBallInNewCoordinates() {
		ball = new Ball(ballXCoor, ballYCoor, ballWidth, ballHeight);
	}

	private void changeDirectionIfHitWall() {
		if (ballXCoor > WIDTH - ball.width) {
			changeDirectionX();
		}
		if (ballXCoor < 0) {
			changeDirectionX();
		}
		if (ballYCoor < 0) {
			changeDirectionY();
		}
	}

	private void changeDirectionIfHitThePalet() {

		if (paletIntersectsTheBall()) {
			changeDirectionY();
		}
	}

	private boolean paletIntersectsTheBall() {
		if (new Rectangle(paletXCoor, paletYCoor, paletWidth, paletHeight)
				.intersects(new Rectangle(ballXCoor, ballYCoor, ballWidth, ballHeight))) {
			return true;
		}
		return false;
	}

	private void changeLevelIfPassed() {
		finishLevel3();
		finishLevel2();
		finishLevel1();
	}

	private void finishLevel3() {
		if (level3 == true && bricksDraw.size() == 0 && paletIntersectsTheBall()) {
			level3 = false;
		}
	}

	private void finishLevel2() {
		if (level2 == true && bricksDraw.size() == 0 && paletIntersectsTheBall()) {
			level2 = false;
			level3 = true;
		}
	}

	private void finishLevel1() {
		if (level1 == true && bricksDraw.size() == 0 && paletIntersectsTheBall()) {
			level1 = false;
			level2 = true;
		}
	}

	private void removeBrickIfHit() {
		for (int i = 0; i < bricksDraw.size(); i++) {
			if ((new Rectangle(bricksDraw.get(i).xCoor, bricksDraw.get(i).yCoor, brickWidth, brickHeight)
					.intersects(new Rectangle(ballXCoor, ballYCoor, ballWidth, ballHeight)))) {
				bricksDraw.remove(i);
				changeDirectionY();
			}
		}
	}

	private void changeDirectionY() {
		ballDirY = -ballDirY;
	}

	private void changeDirectionX() {
		ballDirX = -ballDirX;
	}

	private void moveBall() {
		moveBallY();
		moveBallX();
	}

	private void moveBallY() {
		ballYCoor -= ballDirY;
	}

	private void moveBallX() {
		ballXCoor -= ballDirX;
	}

	private void makePaletMove() {
		if (left) {
			paletXCoor--;
		}
		if (right) {
			paletXCoor++;
		}
	}

	private void movePaletIfCrossedTheWidth() {
		if (paletXCoor > WIDTH - paletWidth) {
			paletXCoor = 0;
		}
		if (paletXCoor < 0) {
			paletXCoor = WIDTH - paletWidth;
		}
	}

	private void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void makePaletAndBall() {
		if (gameStarted) {
			palet = new Palet(paletXCoor, paletYCoor, paletWidth, paletHeight);
			ball = new Ball(ballXCoor, ballYCoor, ballWidth, ballHeight);
			gameStarted = false;
		}
	}

	private void makeRowIfNeeded() {
		if (level1 && bricks.size() != bricksInRow * oneRow) {
			makeNRowOfBricks(oneRow);
		}
		if (level2 && bricks.size() != bricksInRow * twoRows) {
			makeNRowOfBricks(twoRows);
		}
		if (level3 && bricks.size() != bricksInRow * threeRows) {
			makeNRowOfBricks(threeRows);
		}
	}

	private void makeNRowOfBricks(int n) {
		bricks.clear();
		for (int i = 0; i < n; i++) {
			brickXCoor = 20;
			for (int j = 0; j < 5; j++) {
				brick = new Brick(brickXCoor, brickYCoor + i * 25, brickWidth, brickHeight);
				brickXCoor += WIDTH / 5 - 5;
				bricksDraw.add(brick);
				bricks.add(brick);
			}
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
		if (key == KeyEvent.VK_SPACE) {
			if (pause == false) {
				pause = true;
			} else
				pause = false;
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
