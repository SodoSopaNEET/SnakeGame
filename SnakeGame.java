import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame implements ActionListener, KeyListener {

    private static final int TILE_SIZE = 15; // 每個方塊的大小
    private static final int GRID_SIZE = 30; // 網格大小

    private ArrayList<Point> snake; // 蛇的身體
    private ArrayList<Point> snake2; // 蛇2的身體
    private Point food; // 食物位置
    private int direction; // 移動方向
    private int direction2; // 蛇2移動方向
    private Timer timer; // 定時器
    private int score1; // 分數1
    private int score2; // 分數2
    private boolean isAIPlaying = false;
    private boolean isTwoPlayer; // 是否有玩家2

    public SnakeGame(int speed, boolean isAIPlaying, boolean isTwoPlayer) { // 建構函數，設定遊戲速度
        this.isAIPlaying = !isAIPlaying;
        this.isTwoPlayer = isTwoPlayer;
        if (isTwoPlayer) {
            snake2 = new ArrayList<>();
            snake2.add(new Point(GRID_SIZE / 2 - 5, GRID_SIZE / 2 - 5));
            direction2 = 1; // 蛇2往右移動
            score2 = 0; // 初始化分數
        }
        setTitle("Snake Game"); // 設定視窗標題
        setSize(GRID_SIZE * TILE_SIZE - 12, GRID_SIZE * TILE_SIZE + 10); // 設定視窗大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 設定關閉視窗時的操作
        setLocationRelativeTo(null); // 設定視窗位置在螢幕中央
        setResizable(false); // 不可調整視窗大小

        snake = new ArrayList<>(); // 初始化蛇身體
        snake.add(new Point(GRID_SIZE / 2, GRID_SIZE / 2)); // 蛇初始位置在中央
        direction = 1; // 蛇初始移動方向向右

        spawnFood(); // 放置食物

        timer = new Timer(speed, this); // 使用速度參數初始化定時器
        timer.start(); // 啟動定時器

        addKeyListener(this); // 監聽鍵盤事件
        setFocusable(true); // 可以獲取焦點

        score1 = 0; // 初始化分數
    }

    // 放置食物
    private void spawnFood() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(GRID_SIZE);
            y = random.nextInt(GRID_SIZE);
        } while (snake.contains(new Point(x, y)) || (food != null && food.equals(new Point(x, y))));
        // 檢查食物是否和蛇身體位置重疊
        food = new Point(x, y);
    }

    // 處理蛇的移動
    private void move() {
        Point head = snake.get(0); // 獲取蛇頭位置
        Point newHead;

        switch (direction) {
            case 0: // 上
                newHead = new Point(head.x, (head.y - 1 + GRID_SIZE) % GRID_SIZE);
                break;
            case 1: // 右
                newHead = new Point((head.x + 1) % GRID_SIZE, head.y);
                break;
            case 2: // 下
                newHead = new Point(head.x, (head.y + 1) % GRID_SIZE);
                break;
            case 3: // 左
                newHead = new Point((head.x - 1 + GRID_SIZE) % GRID_SIZE, head.y);
                break;
            default:
                return;
        }

        if (newHead.equals(food)) { // 如果吃到食物
            snake.add(0, newHead); // 蛇長度增加
            spawnFood(); // 放置新的食物
            score1 += 10; // 增加分數
        } else {
            snake.add(0, newHead); // 蛇移動，增加新的頭部
            snake.remove(snake.size() - 1); // 移除尾部
        }

        checkCollision(); // 檢查碰撞
    }

    private void movePlayer2() {
        Point head2 = snake2.get(0);
        Point newHead2;

        switch (direction2) {
            case 0: // 上
                newHead2 = new Point(head2.x, (head2.y - 1 + GRID_SIZE) % GRID_SIZE);
                break;
            case 1: // 右
                newHead2 = new Point((head2.x + 1) % GRID_SIZE, head2.y);
                break;
            case 2: // 下
                newHead2 = new Point(head2.x, (head2.y + 1) % GRID_SIZE);
                break;
            case 3: // 左
                newHead2 = new Point((head2.x - 1 + GRID_SIZE) % GRID_SIZE, head2.y);
                break;
            default:
                return;
        }

        if (newHead2.equals(food)) {
            snake2.add(0, newHead2);
            spawnFood();
            score2 += 10;
        } else {
            snake2.add(0, newHead2);
            snake2.remove(snake2.size() - 1);
        }

        checkCollision();
    }

    // 檢查碰撞
    private void checkCollision() {
        Point head = snake.get(0); // 獲取蛇頭位置

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver(); // 如果自己碰到p1身體，遊戲結束
            }
        }
        if (isTwoPlayer) {
            Point head2 = snake2.get(0);

            if (head == head2) {// 如果頭相同，遊戲結束
                gameOver();
            }

            for (int i = 1; i < snake2.size(); i++) {// 如果自己頭、敵人頭碰到p2身體，
                if (head.equals(snake2.get(i)) || head2.equals(snake2.get(i))) {
                    gameOver();
                }
            }
            for (int i = 1; i < snake.size(); i++) {
                if (head2.equals(snake.get(i))) {
                    gameOver(); // 如果自己頭碰到敵人身體，遊戲結束
                }
            }
        }
    }

    // 更新分數顯示
    private void updateScore() {
        repaint(0, 0, getWidth(), 20); // 重新繪製分數區域
    }

    // 遊戲結束
    private void gameOver() {
        timer.stop(); // 停止計時器
        if (isTwoPlayer) {
            if (!isAIPlaying) {
                if (score1 > score2) {
                    JOptionPane.showMessageDialog(this, "Game Over, AI Wins!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (score2 > score1) {
                    JOptionPane.showMessageDialog(this, "Game Over, Player Wins!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Game Over, Tie", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                if (score1 > score2) {
                    JOptionPane.showMessageDialog(this, "Game Over, Player1 Wins!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (score2 > score1) {
                    JOptionPane.showMessageDialog(this, "Game Over, Player2 Wins!", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Game Over, Tie", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Game Over, Score:" + score1, "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        System.exit(0); // 顯示遊戲結束訊息並退出遊戲
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isAIPlaying) {
            aiMove(); // AI 控制蛇移動
        }
        move(); // 玩家控制蛇移動
        if (isTwoPlayer) {
            movePlayer2();
        }
        updateScore(); // 更新分數
        repaint(); // 重新繪製遊戲畫面
    }

    // 判斷鍵盤輸入
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!isTwoPlayer) {
            handleSinglePlayerInput(key);
        } else {
            handleTwoPlayerInput(key);
        }
    }

    // 單人模式的方向控制
    private void handleSinglePlayerInput(int key) {
        switch (key) {
            case KeyEvent.VK_UP:
                if (direction != 2) {
                    direction = 0; // 上
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 3) {
                    direction = 1; // 右
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 0) {
                    direction = 2; // 下
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 1) {
                    direction = 3; // 左
                }
                break;
        }
    }

    // 雙人模式的方向控制
    private void handleTwoPlayerInput(int key) {
        switch (key) {
            // 玩家1控制方向
            case KeyEvent.VK_UP:
                if (direction != 2) {
                    direction = 0; // 上
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 3) {
                    direction = 1; // 右
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 0) {
                    direction = 2; // 下
                }
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 1) {
                    direction = 3; // 左
                }
                break;

            // 玩家2控制方向
            case KeyEvent.VK_W:
                if (direction2 != 2) {
                    direction2 = 0; // W: 玩家2往上
                }
                break;
            case KeyEvent.VK_D:
                if (direction2 != 3) {
                    direction2 = 1; // D: 玩家2往右
                }
                break;
            case KeyEvent.VK_S:
                if (direction2 != 0) {
                    direction2 = 2; // S: 玩家2往下
                }
                break;
            case KeyEvent.VK_A:
                if (direction2 != 1) {
                    direction2 = 3; // A: 玩家2往左
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // 畫出視窗
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 計算實際視窗大小
        Insets insets = getInsets();
        int actualTileSize = (getWidth() - insets.left - insets.right) / GRID_SIZE;

        // 繪製蛇身
        g.setColor(Color.GREEN);
        for (Point point : snake) {
            g.fillRect(point.x * actualTileSize + insets.left, point.y * actualTileSize + insets.top, actualTileSize,
                    actualTileSize);
        }
        // 繪製蛇身2
        if (isTwoPlayer) {
            g.setColor(Color.BLUE);
            for (Point point : snake2) {
                g.fillRect(
                        point.x * actualTileSize + insets.left, point.y * actualTileSize + insets.top,
                        actualTileSize, actualTileSize);
            }
        }
        // 繪製食物
        g.setColor(Color.RED);
        g.fillRect(food.x * actualTileSize + insets.left, food.y * actualTileSize + insets.top, actualTileSize,
                actualTileSize);

        // 顯示分數
        g.setColor(Color.BLACK);
        // 分開顯示分數
        if (!isAIPlaying && !isTwoPlayer) {
            g.drawString("AI : " + score1, 10 + insets.left, 20 + insets.top);
        }
        if (isTwoPlayer) {
            if (isAIPlaying) {
                g.drawString("Player1 : " + score1, 10 + insets.left, 20 + insets.top);
                g.drawString("Player2 : " + score2, 200 + insets.left, 20 + insets.top);
            } else {
                g.drawString("Player : " + score2, 10 + insets.left, 20 + insets.top);
                g.drawString("AI : " + score1, 200 + insets.left, 20 + insets.top);
            }
        } else if (isAIPlaying) {
            g.drawString("Score: " + score1, 10 + insets.left, 20 + insets.top);
        }
    }

    // AI 控制蛇移動
    private void aiMove() {
        Point head = snake.get(0); // 獲取蛇頭位置
        Point foodPosition = food.getLocation(); // 獲取食物位置
        boolean danger_up = false;
        boolean danger_down = false;
        boolean danger_right = false;
        boolean danger_left = false;
        // 判斷撞到自己的危險判定(頭的四個方位是否有自己身體)
        for (Point point : snake) {
            if (point.equals(new Point(head.x, (head.y - 1 + GRID_SIZE) % GRID_SIZE))) {
                danger_up = true;
            } else if (point.equals(new Point(head.x, (head.y + 1) % GRID_SIZE))) {
                danger_down = true;
            } else if (point.equals(new Point((head.x + 1) % GRID_SIZE, head.y))) {
                danger_right = true;
            } else if (point.equals(new Point((head.x - 1 + GRID_SIZE) % GRID_SIZE, head.y))) {
                danger_left = true;
            }
        }
        // 撞到玩家的危險判定
        if (isTwoPlayer) {
            for (Point point : snake2) {
                if (point.equals(new Point(head.x, (head.y - 1 + GRID_SIZE) % GRID_SIZE))) {
                    danger_up = true;
                } else if (point.equals(new Point(head.x, (head.y + 1) % GRID_SIZE))) {
                    danger_down = true;
                } else if (point.equals(new Point((head.x + 1) % GRID_SIZE, head.y))) {
                    danger_right = true;
                } else if (point.equals(new Point((head.x - 1 + GRID_SIZE) % GRID_SIZE, head.y))) {
                    danger_left = true;
                }
            }
        }
        // 檢查蛇頭與食物的相對位置
        if (head.x < foodPosition.x && !danger_right) {

            direction = 1; // 如果食物在右側，且向右移動是安全的，則向右移動

        } else if (head.x > foodPosition.x && !danger_left) {
            direction = 3; // 如果食物在左側，且向左移動是安全的，則向左移動
        } else if (head.y < foodPosition.y && !danger_down) {
            direction = 2; // 如果食物在下方，且向下移動是安全的，則向下移動
        } else if (head.y > foodPosition.y && !danger_up) {
            direction = 0; // 如果食物在上方，且向上移動是安全的，則向上移動

        } else {// 如果三個方向是危險，方向改成為一安全方向
            if (danger_down && danger_left && danger_up) {
                direction = 1;
            } else if (danger_down && danger_right && danger_up) {
                direction = 3;
            } else if (danger_left && danger_right && danger_up) {
                direction = 2;
            } else if (danger_down && danger_left && danger_right) {
                direction = 0;
            } else {// 如果行進方向是危險，改變方向
                if ((direction == 0 && danger_up)) {
                    direction = 1;
                } else if ((direction == 2 && danger_down)) {
                    direction = 3;
                } else if ((direction == 1 && danger_right)) {
                    direction = 2;
                } else if ((direction == 3 && danger_left)) {
                    direction = 0;
                }
            }
        }
    }

}
