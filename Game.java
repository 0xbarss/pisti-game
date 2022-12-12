import java.util.Random;

public class Game {
    public static void main(String[] args) {
        // Game Loop
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
    public static Card[] cutDeck(Card[] cards) {
		// Cut the deck
		int cardslength = cards.length;
		Card[] new_cards = new Card[cards.length];
		Random r = new Random(System.currentTimeMillis());
		int randnum = r.nextInt(cardslength*3/5)+cardslength*2/5; // Find a value around the middle index of the array
		System.arraycopy(cards, randnum, new_cards, 0, cardslength-randnum);
		System.arraycopy(cards, 0, new_cards, cardslength-randnum, randnum);
		return new_cards;
	}
}