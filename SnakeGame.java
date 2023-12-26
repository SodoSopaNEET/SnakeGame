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
    private int score; // 分數
    // private boolean gridDrawn = false;
    private boolean isAIPlaying = false;
    private boolean isTwoPlayer; // 是否有玩家2


    public SnakeGame(int speed, boolean isAIPlaying, boolean isTwoPlayer) { // 建構函數，設定遊戲速度
        this.isAIPlaying = !isAIPlaying;
        this.isTwoPlayer = isTwoPlayer;
        if (isTwoPlayer) {
            snake2 = new ArrayList<>();
            snake2.add(new Point(GRID_SIZE / 2 - 5, GRID_SIZE / 2));
            direction2 = 1; // 蛇2往右移動
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

        score = 0; // 初始化分數
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
            score += 10; // 增加分數
        } else {
            snake.add(0, newHead); // 蛇移動，增加新的頭部
            snake.remove(snake.size() - 1); // 移除尾部
        }

        checkCollision(); // 檢查碰撞
    }

    // 檢查碰撞
    private void checkCollision() {
        Point head = snake.get(0); // 獲取蛇頭位置

        if (head.x < 0 || head.x >= GRID_SIZE || head.y < 0 || head.y >= GRID_SIZE) {
            gameOver(); // 如果碰到邊界，遊戲結束
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver(); // 如果碰到自己身體，遊戲結束
            }
        }
        if (isTwoPlayer) {
            Point head2 = snake2.get(0);

            if (head2.x < 0 || head2.x >= GRID_SIZE || head2.y < 0 || head2.y >= GRID_SIZE) {
                gameOver();
            }

            for (int i = 0; i < snake2.size(); i++) {
                if (head.equals(snake2.get(i))) {
                    gameOver();
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
        JOptionPane.showMessageDialog(this, "Game Over, Score：" + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
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

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!isTwoPlayer) {
            handleSinglePlayerInput(key);
        } else {
            handleTwoPlayerInput(key);
        }
    }
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
                    direction2 = 1; // D key: 玩家2往右
                }
                break;
            case KeyEvent.VK_S:
                if (direction2 != 0) {
                    direction2 = 2; // S key: 玩家2往下
                }
                break;
            case KeyEvent.VK_A:
                if (direction2 != 1) {
                    direction2 = 3; // A key: 玩家2往左
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

    private void movePlayer2() {
        Point head2 = snake2.get(0);
        Point newHead2;

        switch (direction2) {
            case 0: // Up
                newHead2 = new Point(head2.x, (head2.y - 1 + GRID_SIZE) % GRID_SIZE);
                break;
            case 1: // Right
                newHead2 = new Point((head2.x + 1) % GRID_SIZE, head2.y);
                break;
            case 2: // Down
                newHead2 = new Point(head2.x, (head2.y + 1) % GRID_SIZE);
                break;
            case 3: // Left
                newHead2 = new Point((head2.x - 1 + GRID_SIZE) % GRID_SIZE, head2.y);
                break;
            default:
                return;
        }

        if (newHead2.equals(food)) {
            snake2.add(0, newHead2);
            spawnFood();
            score += 10;
        } else {
            snake2.add(0, newHead2);
            snake2.remove(snake2.size() - 1);
        }

        checkCollision();
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 計算實際視窗大小
        Insets insets = getInsets();
        int actualTileSize = (getWidth() - insets.left - insets.right) / GRID_SIZE;

        // 繪製蛇身
        g.setColor(Color.GREEN);
        for (Point point : snake) {
            g.fillRect(point.x * actualTileSize + insets.left, point.y * actualTileSize + insets.top, actualTileSize, actualTileSize);
        }
        // 繪製蛇身2
        if (isTwoPlayer) {
            g.setColor(Color.BLUE);
            for (Point point : snake2) {
                g.fillRect(
                        point.x * actualTileSize + insets.left, point.y * actualTileSize + insets.top,
                        actualTileSize, actualTileSize
                );
            }
        }
        // 繪製食物
        g.setColor(Color.RED);
        g.fillRect(food.x * actualTileSize + insets.left, food.y * actualTileSize + insets.top, actualTileSize, actualTileSize);


        // 顯示分數
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10 + insets.left, 20 + insets.top);

        // 繪製網格
//        g.setColor(Color.BLACK);
//        for (int i = 0; i <= GRID_SIZE; i++) {
//            g.drawLine(i * actualTileSize + insets.left, 1 * actualTileSize + insets.top, i * actualTileSize + insets.left, GRID_SIZE * actualTileSize + insets.top);
//            g.drawLine(1 * actualTileSize + insets.left, i * actualTileSize + insets.top, GRID_SIZE * actualTileSize + insets.left, i * actualTileSize + insets.top);
//        }
    }




    // AI 控制蛇移動
    private void aiMove() {
        Point head = snake.get(0); // 獲取蛇頭位置
        Point foodPosition = food.getLocation(); // 獲取食物位置
        boolean danger_up = false;
        boolean danger_down = false;
        boolean danger_right = false;
        boolean danger_left = false;
        for (Point point : snake) {
            // System.out.println("point:" + point.equals(new Point(head.x, (head.y - 1 +
            // GRID_SIZE) % GRID_SIZE)));
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

        // 檢查蛇頭與食物的相對位置
        if (head.x < foodPosition.x && !danger_right) {
            if (isValidMove(1)) {
                direction = 1; // 如果食物在右側，且向右移動是有效的，則向右移動
            }
        } else if (head.x > foodPosition.x && !danger_left) {
            if (isValidMove(3)) {
                direction = 3; // 如果食物在左側，且向左移動是有效的，則向左移動
            }
        } else if (head.y < foodPosition.y && !danger_down) {
            if (isValidMove(2)) {
                direction = 2; // 如果食物在下方，且向下移動是有效的，則向下移動
            }
        } else if (head.y > foodPosition.y && !danger_up) {
            if (isValidMove(0)) {
                direction = 0; // 如果食物在上方，且向上移動是有效的，則向上移動
            }
        } else {
            if (danger_down && danger_left && danger_up) {
                direction = 1;
            } else if (danger_down && danger_right && danger_right) {
                direction = 3;
            } else if (danger_left && danger_right && danger_up) {
                direction = 2;
            } else if (danger_down && danger_left && danger_right) {
                direction = 0;
            }
        }
    }

    // 檢查移動是否有效（不與蛇身相撞或超出邊界）
    private boolean isValidMove(int dir) {
        Point head = snake.get(0); // 獲取蛇頭位置
        Point newHead;

        switch (dir) {
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
                return false;
        }

        // 檢查新位置是否與蛇身重疊或超出邊界
        return !snake.contains(newHead)
                && !(newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE);
    }

}
