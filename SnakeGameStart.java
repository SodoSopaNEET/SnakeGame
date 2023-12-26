import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnakeGameStart extends JFrame {

    private JComboBox<String> speedComboBox; // 速度選擇的下拉式選單
    private JComboBox<String> modeComboBox; // 遊玩模式的下拉式選單

    public SnakeGameStart() {
        setTitle("Snake Game - Start"); // 設定視窗標題
        setSize(450, 350); // 設定視窗大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 設定關閉視窗時的預設操作
        setLocationRelativeTo(null); // 設定視窗置中
        setResizable(false); // 防止使用者調整視窗大小

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10)); // 創建一個面板，使用網格布局管理器

        JLabel speedLabel = new JLabel("Speed:"); // 速度標籤
        String[] speedOptions = { "Slow", "Medium", "Fast" }; // 速度選項
        speedComboBox = new JComboBox<>(speedOptions); // 創建速度選擇的下拉式選單
        JLabel modeLabel = new JLabel("Game Mode:"); // 遊玩模式標籤
        String[] modeOptions = { "Single Player", "Two Players", "Play with AI" }; // 遊玩模式選項
        modeComboBox = new JComboBox<>(modeOptions);

        JButton startButton = new JButton("Start"); // 開始按鈕
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(); // 當按鈕被點擊時，呼叫 startGame() 方法
            }
        });

        // 將元件加入面板中
        panel.add(speedLabel);
        panel.add(speedComboBox);
        panel.add(modeLabel);
        panel.add(modeComboBox);
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(startButton);

        add(panel); // 將面板加入到視窗中
    }

    private void startGame() {
        String selectedSpeed = (String) speedComboBox.getSelectedItem(); // 取得選擇的速度
        String selectedMode = (String) modeComboBox.getSelectedItem(); // 取得選擇的遊玩模式
        int speed;
        boolean isTwoPlayer = false;
        boolean isAIPlaying = false;

        // 根據選擇的速度設定遊戲速度
        switch (selectedSpeed) {
            case "Slow":
                speed = 150;
                break;
            case "Medium":
                speed = 100;
                break;
            case "Fast":
                speed = 70;
                break;
            default:
                speed = 100; // 預設速度為中等速度
        }

        // 根據選擇的遊玩模式設定相應的標誌
        switch (selectedMode) {
            case "Single Player":
                isAIPlaying = false;
                break;
            case "Two Players":
                isTwoPlayer = true;
                break;
            case "Play with AI":
                isAIPlaying = true;
                isTwoPlayer = true;
                break;
        }

        // 創建 SnakeGame 物件，傳遞所選速度和遊玩模式作為參數
        SnakeGame snakeGame = new SnakeGame(speed, isAIPlaying, isTwoPlayer);
        snakeGame.setVisible(true); // 顯示遊戲畫面

        // 關閉起始畫面
        this.dispose(); // 關閉目前的 SnakeGameStart 視窗
    }

    public static void main(String[] args) {
        // 使用 SwingUtilities.invokeLater() 來確保 Swing 界面元件在 EDT 中執行
        SwingUtilities.invokeLater(() -> {
            SnakeGameStart startFrame = new SnakeGameStart(); // 創建起始畫面物件
            startFrame.setVisible(true); // 顯示起始畫面
        });
    }
}
