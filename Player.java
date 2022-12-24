import java.util.Scanner;
import java.util.Random;

public class Player {
	// Attributes
	private Card[] cards; // Player's cards
	private int size; // The size of the cards array, not real size, it will be used instead of removing or adding an element from an array
	private int cards_count; // The number of taken cards
	private int score; // The player's current score
	private static char[] suits = {'S', 'C', 'H', 'D'};
	private static char[] ranks = {'A', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'J', 'Q', 'K'};
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
    public void setSize(int size) {this.size = size;}
	public void setTakenCardsCount(int cards_count) {this.cards_count = cards_count;}
	public void setScore(int score) {this.score = score;}
	// Special Methods
	public void addCard(Card card) {
		this.cards[this.size] = card;
		this.size += 1;
	}
	public int findCard(char suit, char rank) {
		int index = -1;
		Card card;
		for (int i=0; i<this.size; i++) {
			card = this.cards[i];
			if (suit == card.getSuit() && rank == card.getRank()) {
				index = i;
				break;
			}
		}
		return index; // -1-->Does not exist
	}
	public Card removeCard(int index) {
		Card temp = this.cards[this.size-1];
		this.cards[this.size-1] = this.cards[index];
		this.cards[index] = temp;
		this.size -= 1;
		return this.cards[this.size];
	}
	public void printCards(String text) {
		Card card;
		System.out.print("||" + text + "||  ");
		for (int i=0; i<this.size; i++) {
			card = this.cards[i];
			System.out.print("-- " + card.getSuit() + card.getRank() + " --");
		}
		System.out.println();
	}
	public int play(Scanner sc, Board board) {
		char suit = '0';
		char rank = '0';
		String input;
		Card card;
		int task_type = 0; // -1-->Endgame | 0-->Continue | 1-->Cut | 2-->Pisti
		boolean card_exists = false;
		boolean rank_confirmed = false;
		boolean suit_confirmmed = false;
		while (true) {
			// If there is no card, break
			if (this.size == 0) {
				task_type = -1;
				break;
			}
			// Ask user to choose a card
			System.out.print("Enter the card which you want to play: ");
			input = sc.next();
			// Check if the input length is 2
			if (input.length() != 2) {
				System.out.println("Try Again!");
				continue;
			}
			// Check if the given suit exists in the suits
			for (int i=0; i<suits.length; i++) {
				if (Character.toUpperCase(input.charAt(0)) == suits[i]) {
					suit = suits[i];
					suit_confirmmed = true;
				}
			}
			if (!suit_confirmmed) {
				System.out.println("Wrong Suit, try again!");
				continue;
			}
			// Check if the given rank exists in the ranks
			for (int i=0; i<ranks.length; i++) {
				if (Character.toUpperCase(input.charAt(1)) == ranks[i]) {
					rank = ranks[i];
					rank_confirmed = true;
				}
			}
			if (!rank_confirmed) {
				System.out.println("Wrong Rank, try again!");
				continue;
			}
			// Check if the given card exists in the cards
			for (int i=0; i<this.size; i++) {
				card = this.cards[i];
				if (suit == card.getSuit() && rank == card.getRank()) {
					card_exists = true;
					break;
				}
			}
			if (!card_exists) {
				System.out.println("You don't have this card, try again!");
				continue;

			}
			break;
		}
		if (task_type != -1) {
			if (board.getSize() > 0) {
				char last_card_rank = board.getLastCard().getRank();
				// Check if there is one card on the board
				if (board.getSize() == 1) {
					if (rank == last_card_rank) task_type = 2;                 // Pisti
					else if (rank == 'J') task_type = 1;                       // Cut
				}
				else {
					if (rank == last_card_rank || rank == 'J') task_type = 1;  // Cut
				}
			}
			board.addCard(this.removeCard(this.findCard(suit, rank)));
		}
		return task_type;
	}
	public int playAI(Random r, Board board) {
		int task_type = 0; // -1-->Endgame | 0-->Continue | 1-->Cut | 2-->Pisti
		int randnum = 0;
		int index = 0;
		// If there is no card, break 
		if (this.size == 0) task_type = -1;
		// Determine if a card will be selected according to the card on the board
		else if (board.getSize() > 0) {
			Card card;
			boolean matched = false;
			char last_card_rank = board.getLastCard().getRank();
			// Check if any card matches with the card on the board
			for (int i=0; i<this.size; i++) {
				index = i;
				card = this.cards[i];
				if (last_card_rank == card.getRank()) {
					matched = true;
					if (board.getSize() == 1) task_type = 2;
					else task_type = 1;
					break;
				}
			}
			// Check if there is any J in the cards
			if (!matched) {
				for (int i=0; i<this.size; i++) {
					index = i;
					card = this.cards[i];
					if (card.getRank() == 'J') {
						matched = true;
						task_type = 1;
						break;
					}
				}
			}
			// Randomly select a card
			if (!matched) {
				index = r.nextInt(this.size);
			}
			board.addCard(this.removeCard(index));
		}
		// Else, if there is no card on the board, it will be selected randomly except J
		else if (board.getSize() == 0) {
			while (true) {
				randnum = r.nextInt(this.size);
				// Choose any card except J
				// If there is only J, play it
				if ((this.cards[randnum].getRank() != 'J') || (this.cards[randnum].getRank() == 'J' && this.size == 1)) {
					index = randnum;
					break;
				}
			}                                                                                      
			board.addCard(this.removeCard(index));
		}
		return task_type;
	}
}
