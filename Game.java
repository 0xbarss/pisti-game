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
}