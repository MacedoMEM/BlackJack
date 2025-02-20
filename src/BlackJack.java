import java.awt.*; //Para componentes gráficos (Ex: cor, fonte)
import java.awt.event.*; //Para click de botões
import java.util.ArrayList; //Para guardar as cartas do jogador
import java.util.Random; //Para embaralhar o baralho
import javax.swing.*; //Para criar a interface do jogo


public class BlackJack {
    private class Card {
        String value;
        String type;
    
        Card(String value, String type) {
            this.value = value; //Valor da carta
            this. type = type; //Naipe da carta 
        }

        public String toString() { //Para puxar como está nomeada as cartas Ex: (A-S)
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) { // A J Q K
                if (value =="A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); //2-10
        }
        public boolean isAce() {
            return value == "A";
        }

        public String getImagePath() {

            return "./cards/" + toString() + ".png";
        }
    }

    ArrayList<Card> deck;
    Random random = new Random(); //Para embaralhar as cartas

    //dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    //Jogador
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    //Janela
    int boardWidth = 700;
    int boardHeight = 600;

    int cardWidth = 110;
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

                try {
                //Desenhar carta escondida
                Image hiddenCardImg = new ImageIcon (getClass().getResource("/cards/BACK.png.png")).getImage();
                if (!stayButton.isEnabled()) { 
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                //Desenhar mão do dealer
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5)*i, 20, cardWidth, cardHeight, null);
                }

                //Desenhar mão do player
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);
                }

                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("FICAR: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum > 21) {
                        message = "Você Perdeu! :C";
                    }
                    else if (dealerSum > 21) {
                        message = "Você Ganhou! :D";
                    }
                    //Quando o player e o dealer <= 21
                    else if (playerSum == dealerSum) {
                        message = "Empate! -_-";
                    }
                    else if (playerSum > dealerSum) {
                        message = "Você Ganhou! :D";
                    }
                    else if (playerSum < dealerSum) {
                        message = "Você Perdeu! :C";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 200, 250);
                }

           } catch (Exception e) {
             e.printStackTrace();
           }
        }   
    }; 
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Pedir");
    JButton stayButton = new JButton("Parar");
    JButton resetButton = new JButton("Reiniciar");

    BlackJack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        hitButton.setFocusable(false);
        buttonPanel.add(resetButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        resetButton.addActionListener (new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerHand.clear();
                dealerHand.clear();
        
                // Reseta valores
                playerSum = 0;
                playerAceCount = 0;
                dealerSum = 0;
                dealerAceCount = 0;
        
                // Reconstrói e embaralha o baralho
                buildDeck();
                shuffleDeck();
        
                // Distribui novas cartas
                startGame();
        
                // Reabilita os botões "Pedir" e "Parar"
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
        
                // Atualiza a interface
                gamePanel.repaint();
                frame.revalidate();
            }
        });
            

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce()?1 : 0;
                playerHand.add(card);
                if(reducePlayerAce() > 21) { //A + 2 + J ---> 1 + 2 + j
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();

            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce()? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
            }
        });

        gamePanel.repaint();

    }

    public void startGame() {
        //DECK
        buildDeck();
        shuffleDeck();

        //DEALER
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1); //Remover carta do baralho
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER: ");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        //Jogador
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;

        for(int i =0; i< 2; i++) {
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }
        System.out.println("JOGADOR: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);

    }

    public void buildDeck() { 
        deck = new ArrayList<Card>();
        String[] values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] types = {"C","D","H","S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }

        System.out.println("CONSTRUIR DECK:");
        System.out.println(deck);
    }

    public void shuffleDeck() { 
        for (int i = 0; i < deck.size(); i++) { 
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }

        System.out.println("EMBARALHAR: ");
        System.out.println(deck);
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) { 
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }
    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            playerAceCount -= 1;
        }
        return dealerSum;
    }
}

