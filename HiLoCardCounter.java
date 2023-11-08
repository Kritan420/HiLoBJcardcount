import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class HiLoCardCounter {
    private int count = 0;  
    private int decks = 8; 
    private int cards = 52 * decks;
    private int lowcards = 20 * decks;
    private int midcards = 12 * decks;
    private int highcards = 20 * decks;
    private int baselineBet = 10;

    private double trueCount = 0; 
    private JLabel emptyLabel;
    private JLabel countLabel;
    private JLabel trueCountLabel;
    private JLabel advantageLabel;
    private JLabel predictionLabel;
    private JLabel betSizeLabel;
    private JLabel lowCountLabel;
    private JLabel midCountLabel;
    private JLabel highCountLabel;
    private JButton resetButton;
    private JButton plusButton;
    private JButton zeroButton;
    private JButton minusButton;

    public HiLoCardCounter() {
        JFrame frame = new JFrame("Card Counting");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - frame.getWidth()) / 3;
        int centerY = (int) (screenSize.getHeight() - frame.getHeight()) / 3;
        frame.setLocation(centerX, centerY);
        JPanel topPanel = new JPanel();
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count = 0;
                trueCount = 0;
                decks = 8;
                cards = 52 * decks;
                lowcards = 20 * decks;
                midcards = 12 * decks;
                highcards = 20 * decks;
                updateCountLabels();
            }
        });
        topPanel.add(resetButton);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(5, 1));
        countLabel = new JLabel("Count: " + count);
        emptyLabel = new JLabel(" ");
        trueCountLabel = new JLabel("True Count: " + trueCount);
        predictionLabel = new JLabel("Card Prediction: " + getCardPrediction());
        advantageLabel = new JLabel("No Advantage");
        betSizeLabel = new JLabel("Bet Size: " + baselineBet);
        lowCountLabel = new JLabel("Low Cards: " + lowcards);
        midCountLabel = new JLabel("Mid Cards: " + midcards);
        highCountLabel = new JLabel("High Cards: " + highcards);
        advantageLabel.setForeground(Color.GRAY);
        //middlePanel.add(countLabel);
        middlePanel.add(trueCountLabel);
        middlePanel.add(predictionLabel);
        middlePanel.add(emptyLabel);
        middlePanel.add(betSizeLabel);
        middlePanel.add(advantageLabel);

        InputMap inputMap = middlePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = middlePanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "plusButtonPressed");
        actionMap.put("plusButtonPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plusButton.doClick();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "zeroButtonPressed");
        actionMap.put("zeroButtonPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zeroButton.doClick();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "minusButtonPressed");
        actionMap.put("minusButtonPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minusButton.doClick();
            }
        });
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "resetAction");
        actionMap.put("resetAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButton.doClick();
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 3));
        plusButton = new JButton("2 3 4 5 6");
        plusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count++;
                lowcards--;
                cards--;
                updateCountLabels();
            }
        });
        zeroButton = new JButton("7 8 9");
        zeroButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count += 0;
                midcards--;
                cards--;
                updateCountLabels();
            }
        });
        minusButton = new JButton("10 J Q K A");
        minusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                count--;
                highcards--;
                cards--;
                updateCountLabels();
            }
        });
        bottomPanel.add(lowCountLabel);
        bottomPanel.add(midCountLabel);
        bottomPanel.add(highCountLabel);
        bottomPanel.add(plusButton);
        bottomPanel.add(zeroButton);
        bottomPanel.add(minusButton);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(middlePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();
        setBaselineBet();
        frame.setVisible(true);

        
    }

    private void updateCountLabels() {
        countLabel.setText("Count: " + count);
        double remainingCards = cards;
        double remainingDecks = remainingCards / 52.0;
        trueCount = (double) count / remainingDecks; 
        trueCountLabel.setText("True Count: " + trueCount);
        predictionLabel.setText("Card Prediction: " + getCardPrediction());
        advantageLabel.setForeground(Color.GRAY);
        advantageLabel.setText("No Advantage");  
        if (getAdvantage() > 0 && getAdvantage() < 0.99) {
            advantageLabel.setForeground(Color.GRAY);
            advantageLabel.setText("Small Player Advantage");
        } else if (getAdvantage() > 0.99) {
            advantageLabel.setForeground(Color.BLUE);
            advantageLabel.setText("Player Advantage");
        } else if (getAdvantage() < 0 && getAdvantage() > -0.99) {
            advantageLabel.setForeground(Color.GRAY);
            advantageLabel.setText("Small Dealer Advantage");
        } else if (getAdvantage() < -0.99) {
            advantageLabel.setForeground(Color.RED);
            advantageLabel.setText("Dealer Advantage");
        } else {
            advantageLabel.setForeground(Color.GRAY);
            advantageLabel.setText("No Advantage");
        }

        lowCountLabel.setText("Low Cards: " + lowcards);
        midCountLabel.setText("Mid Cards: " + midcards);
        highCountLabel.setText("High Cards: " + highcards);

        int betSize = 0;
        if (trueCount < 1) {
            betSize = baselineBet;
        } else if (trueCount >= 1 && trueCount < 2) {
            betSize = baselineBet * 2;
        } else if (trueCount >= 2 && trueCount < 3) {
            betSize = baselineBet * 3;
        } else if (trueCount >= 3) {
            betSize = baselineBet * 4;
        }
        betSizeLabel.setText("Bet Size: " + betSize);

    }

    private double getAdvantage() {
        return trueCount;
    }
    private String getCardPrediction() {
        int lowValueCount = lowcards;
        int midValueCount = midcards;
        int highValueCount = highcards;
    
        if (trueCount > 1.0 && highValueCount > lowValueCount && highValueCount > midValueCount) {
            return "High Value Card (10, J, Q, K, A) likely";
        } else if (trueCount < -1.0 && lowValueCount > highValueCount && lowValueCount > midValueCount) {
            return "Low Value Card (2, 3, 4, 5, 6) likely";
        } else if (midValueCount > highValueCount && midValueCount > lowValueCount) {
            return "Neutral Cards (7, 8, 9) likely";
        } else {
            return "No Prediction";
        }
    }
    private void setBaselineBet() {
        String input = JOptionPane.showInputDialog("Enter the baseline bet:");
        if (input != null) { 
            try {
                baselineBet = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Using default baseline bet.");
                baselineBet = 10;
            }
        } else {
            baselineBet = 10;
        }
        betSizeLabel.setText("Bet Size: " + baselineBet);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HiLoCardCounter();
            }
        });
    }
}