public class Player {
	// Attributes
	private Card[] cards; // Player's cards
	private int size; // The size of the cards array, it is represented as integer variable
	private int cards_count; // The number of taken cards
	private int score; // The sum of the values of the cards which are taken without doing pisti
	// Constructor
	public Player() {
		this.cards = new Card[4];
		this.size = 0;
		this.cards_count = 0;
		this.score = 0;
	}
	// Methods
	public Card[] getCards() {return this.cards;}
	public int getSize() {return this.size;}
	public int getTakenCardsCount() {return this.cards_count;}
	public int getScore() {return this.score;}
	public void setCards(Card[] cards) {this.cards = cards;}
    public void setSize(int size) {this.size = size;}
	public void setTakenCardsCount(int cards_count) {this.cards_count = cards_count;}
	public void setScore(int score) {this.score = score;}
}
