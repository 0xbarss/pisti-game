public class Board {
    // Attributes
    private Card[] cards;
    private int size;
    // Constructor
    public Board() {
        this.cards = new Card[52];
        this.size = 0;
    }
    // Methods
    public Card[] getCards() {return this.cards;}
    public int getSize() {return this.size;}
    public Card getLastCard() {return this.cards[this.size-1];}
    // Special Methods
	public void addCard(Card card) {
        // Add a card to the cards array and increase the size 1
        this.cards[this.size] = card;
		this.size += 1;
	}    
    public void clearBoard() {
        // Re-assign the attributes
        this.cards = new Card[52];
        this.size = 0;
    }
    public void printCard() {
        if (this.size > 0) {
            Card card = this.getLastCard();
		    System.out.println("||Board||                 ++ "  + card.getSuit() + card.getRank() + " ++");
        }
        else {
            System.out.println("||Board||                 ++ Empty ++");
        }
	}
}
