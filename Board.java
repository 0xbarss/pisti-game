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
}
