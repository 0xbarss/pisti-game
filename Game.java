import java.util.Scanner;
import java.util.Random;

public class Game {
    public static void main(String[] args) {
        // Create the instances and variables which are required
		Scanner sc = new Scanner(System.in);
		Random r = new Random(System.currentTimeMillis());
		Player player1 = new Player();
		Player player2 = new Player();
		Board board = new Board();
        // Create a deck, shuffle and cut the deck
		Card[] initial_cards = createDeck();
		int initial_cards_size = initial_cards.length;
		initial_cards = shuffleDeck(r, initial_cards);
		initial_cards = cutDeck(r, initial_cards);
        // Distribute 4 cards for each player and the board

        // Game Loop
        while (true) {
            // If player 1 and player 2 have no card and there are enough cards to distribute, distribute 4 cards for each player

            // Print the board

            // Player 1 Turn

            // Calculate Player 1 Score

            // Print the board
            
            // Player 2 Turn

            // Calculate Player 2 Score

            // Check if the game is end

                // Move all the cards on the board to the player who cut or did pisti lastly 

                // Add +3 additional points to the player taken more cards

                // Print the scores

                // Print the winner
        }
    }
    public static Card[] createDeck() {
        // Create a deck
        char[] suits = {'S', 'C', 'H', 'D'};
		char[] ranks = {'A', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'J', 'Q', 'K'};
		Card[] cards = new Card[suits.length*ranks.length];
		for (int i=0; i<suits.length; i++) {
			for (int j=0; j<ranks.length; j++) {
				Card card = new Card(suits[i], ranks[j]);
				cards[i*ranks.length+j] = card;
			}
		}
		return cards;
    }
    public static Card[] shuffleDeck(Random r, Card[] cards) {
		// Shuffle the deck by Fisher-Yates Shuffle Algorithm
		Card temp;
		int randnum;
		for (int i=cards.length-1; i>0; i--) {
			randnum = r.nextInt(i+1);
			temp = cards[i];
			cards[i] = cards[randnum];
			cards[randnum] = temp;
		}
		return cards;
	}
    public static Card[] cutDeck(Random r, Card[] cards) {
		// Cut the deck
		int cardslength = cards.length;
		Card[] new_cards = new Card[cards.length];
		int randnum = r.nextInt(cardslength*3/5)+cardslength*2/5; // Find a value around the middle index of the array
		System.arraycopy(cards, randnum, new_cards, 0, cardslength-randnum);
		System.arraycopy(cards, 0, new_cards, cardslength-randnum, randnum);
		return new_cards;
	}
}