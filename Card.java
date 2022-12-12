public class Card {
    // Attributes
	private char suit; // ♠(Spades), ♣(Clubs), ♥(Hearts), ♦(Diamonds)
	private char rank; // A(Ace) 1 2 3 4 5 6 7 8 9 10 J(Jack) Q(Queen) K(King)  *10 is represeneted as 0
	private int point; // D0 has 3 points, C2 has 2 points, A & J have 1 point, others have 0
	// Constructor
	public Card(char suit, char rank) {
		this.suit = suit;
		this.rank = rank;
		this.point = setDefaultPoint();
	}
	// Methods
	public char getSuit() {return this.suit;}
	public char getRank() {return this.rank;}
	public int getPoint() {return this.point;}
	private int setDefaultPoint() {
		if (this.suit == 'D' && this.rank == '0') return 3;
		if (this.suit == 'C' && this.rank == '2') return 2;
		return 1;
	}
}
