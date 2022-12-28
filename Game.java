import java.util.Scanner;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.FileWriter;

public class Game {
    public static void main(String[] args) {	
		boolean loop = true;
		while (loop) {
			loop = playPisti();
		}
    }
	public static boolean playPisti() {
		System.out.print("\033[H\033[2J"); // Clear the console and move the cursor up
		// Create the instances and variables which are required
		Scanner sc = new Scanner(System.in);
		Random r = new Random(System.currentTimeMillis());
		Player player1 = new Player(); // Human
		Player player2 = new Player(); // AI
		Board board = new Board();
		int dealer = r.nextInt(2); // 0-->Human | 1-->AI
		int startwith = dealer; // 0-->Human | 1-->AI | -1-->Ignore
		int last_card_winner = -1; // 0-->Human | 1-->AI
		int task_type = 0; // -1-->Endgame | 0-->Continue | 1-->Cut | 2-->Pisti
        // Create, shuffle and cut the deck
		Card[] initial_cards = createDeck();
		int initial_cards_size = initial_cards.length;
		initial_cards = shuffleDeck(r, initial_cards);
		initial_cards = cutDeck(sc, r, dealer, initial_cards);
		// Place 4 cards for board
		initial_cards_size = placeCardsOnBoard(board, initial_cards, initial_cards_size);
        // Deal 4 cards for each player
		initial_cards_size = dealCards(player1, player2, dealer, initial_cards, initial_cards_size);
		// If one of the players has 4 J cards, restart the game
		boolean restart_game = checkCards(player1, player2);
		if (restart_game) {
			sc.close();
			return restart_game;
		}
        // Game Loop
        while (true) {
			// If player 1 and player 2 have no card and there are enough cards to distribute, deal 4 cards for each player
			if (player1.getSize() == 0 && player2.getSize() == 0 && initial_cards_size > 7) {
				initial_cards_size = dealCards(player1, player2, dealer, initial_cards, initial_cards_size);
				// If one of the players has 4 J cards, restart the game
				restart_game = checkCards(player1, player2);
				if (restart_game) break;
			}
			// Player-1
			if (startwith != 1 && (task_type != -1 || initial_cards_size != 0)) {
				startwith = -1;
				// Print information
				if (task_type == 2) System.out.println("Player-2 made a pisti!");
				else if (task_type == 1) System.out.println("Player-2 made a cut!");
				// Print the board
				board.printCard();
				// Player 1 Turn
				player1.printCards("Player-1");
				task_type = player1.play(sc, board);
				// Calculate Player 1 Score
				last_card_winner = calculateScore(player1, board, task_type, 0, last_card_winner);
			}
			System.out.print("\033[H\033[2J"); // Clear the console and move the cursor up
			// If player 1 and player 2 have no card and there are enough cards to distribute, deal 4 cards for each player
			if (player1.getSize() == 0 && player2.getSize() == 0 && initial_cards_size > 7) {
				initial_cards_size = dealCards(player1, player2, dealer, initial_cards, initial_cards_size);
				// If one of the players has 4 J cards, restart the game
				restart_game = checkCards(player1, player2);
				if (restart_game) break;
			}
			// Player-2
			if (startwith != 0 && (task_type != -1 || initial_cards_size != 0)) {
				startwith = -1;
				// Print information
				if (task_type == 2) System.out.println("Player-1 made a pisti!");
				else if (task_type == 1) System.out.println("Player-1 made a cut!");
				// Player 2 Turn
				task_type = player2.playAI(r, board);
				// Calculate Player 2 Score
				last_card_winner = calculateScore(player2, board, task_type, 1, last_card_winner);
			}
            // Check if the game is end
			if (task_type == -1 && initial_cards_size == 0) {
				// Move all the cards on the board to the player who made a cut or a pisti lastly
				Card card;
				Card[] board_cards = board.getCards();
				if (board.getSize() != 0) {
					if (last_card_winner == 1) {
						player1.setTakenCardsSize(player1.getTakenCardsSize()+board.getSize());
						for (int i=0; i<board.getSize(); i++) {
							card = board_cards[i];
							player1.addToTakenCards(card);
							player1.setScore(player1.getScore()+card.getPoint());
						}
						board.clearBoard();
					}
					else if (last_card_winner == -1) {
						player2.setTakenCardsSize(player2.getTakenCardsSize()+board.getSize());
						for (int i=0; i<board.getSize(); i++) {
							card = board_cards[i];
							player2.addToTakenCards(card);
							player2.setScore(player2.getScore()+card.getPoint());
						}
						board.clearBoard();
					}
				}
                // Add +3 additional points to the player taken more cards
				if (player1.getTakenCardsSize() > player2.getTakenCardsSize()) player1.setScore(player1.getScore()+3);
				else player2.setScore(player2.getScore()+3);
                // Print the scores
				System.out.println("Player-1 Score: " + player1.getScore());
				System.out.println("Player-2 Score: " + player2.getScore());
	            // Print the winner
				if (player1.getScore() > player2.getScore()) System.out.println("\n!!!Player-1 has won!!!\n");
				else if (player1.getScore() < player2.getScore()) System.out.println("\n!!!Player-2 has won!!!\n");
				else System.out.println("\n!!!Draw!!!\n");
				// Save the high score
				saveHighScore(sc, player1.getScore());
				break;
			}
        }
		sc.close();
		return restart_game;
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
    public static Card[] cutDeck(Scanner sc, Random r, int dealer, Card[] cards) {
		// Cut the deck
		int cardslength = cards.length;
		Card[] new_cards = new Card[cardslength];
		int randnum;
		if (dealer == 0) {
			int num;
			String input;
			while (true) {
				System.out.print("Enter a position to cut deck (0-52): ");
				input = sc.next();
				try {
					num = Integer.parseInt(input);
				}
				catch (NumberFormatException e) {
					System.out.println("Please enter a number!");
					continue;
				}
				if (num >= 0 && num <= 52) {
					break;
				}
				System.out.println("Try Again, enter a number between 0 and 52!");
			}
			randnum = r.nextInt(5);
			if (num > 45) num -= randnum;
			else if (num < 7) num += randnum;
			System.arraycopy(cards, num, new_cards, 0, cardslength-num);
			System.arraycopy(cards, 0, new_cards, cardslength-num, num);
		}
		else if (dealer == 1) {
			randnum = r.nextInt(cardslength*4/5)+cardslength*1/5; // Choose a random index
			System.arraycopy(cards, randnum, new_cards, 0, cardslength-randnum);
			System.arraycopy(cards, 0, new_cards, cardslength-randnum, randnum);
		}
		return new_cards;
	}
	public static int dealCards(Player player1, Player player2, int dealer, Card[] initial_cards, int initial_cards_size) {
		// Deal 4 cards for each player
		Card card;
		if (dealer == 0) {
			for (int i=0; i<8; i++) {
				card = initial_cards[initial_cards_size-1];
				initial_cards_size -= 1;
				if (i%2 == 0) {
					player2.addCard(card);
				}
				else {
					player1.addCard(card);
				}
			}
		}
		else {
			for (int i=0; i<8; i++) {
				card = initial_cards[initial_cards_size-1];
				initial_cards_size -= 1;
				if (i%2 == 0) {
					player1.addCard(card);
				}
				else {
					player2.addCard(card);
				}
			}
		}
		return initial_cards_size;
	}
	public static boolean checkCards(Player player1, Player player2) {
		// Check if one of the player has 4 J
		boolean confirmed = true;
		for (Card card: player1.getCards()) {
			if (card.getRank() != 'J') {
				confirmed = false;
				break;
			}
		}
		if (!confirmed) {
			for (Card card: player2.getCards()) {
				if (card.getRank() != 'J') {
					confirmed = false;
					break;
				}
			}
		}
		return confirmed;
	}
	public static int placeCardsOnBoard(Board board, Card[] initial_cards, int initial_cards_size) {
		// Place 4 cards on the board
		Card card;
		for (int i=0; i<4; i++) {
			card = initial_cards[initial_cards_size-1];
			initial_cards_size -= 1;
			board.addCard(card);
		}
		return initial_cards_size;
	}
	public static int calculateScore(Player player, Board board, int task_type, int turn, int last_card_winner) {
		// Update the score, set taken cards count and clear the board 
		Card card;
		Card[] board_cards = board.getCards();
		if (task_type == 2) {                        // If same ranks matched and the number of the cards on the board is 2 (pisti)
			player.setScore(player.getScore()+10);
			for (int i=0; i<board.getSize(); i++) {
				card = board_cards[i];
				player.addToTakenCards(card);
			}
			board.clearBoard();
			if (turn == 0) last_card_winner = 0;
			else last_card_winner = 1;
		}
		else if (task_type == 1) {                   // If J or the same rank took all the cards (cut)
			for (int i=0; i<board.getSize(); i++) {
				card = board_cards[i];
				player.addToTakenCards(card);
				player.setScore(player.getScore()+card.getPoint());
			}
			board.clearBoard();
			if (turn == 0) last_card_winner = 0;
			else last_card_winner = 1;
		}
		return last_card_winner;
	}
	public static void saveHighScore(Scanner sc, int new_score) {
		String[] data;
		String file_name = "scores.txt";
		int index = 0;
		int capacity = 10; // Store top 10 scores
		boolean confirmed = true;
		Scanner reader = null;
		FileWriter writer = null;
		String[] names = new String[capacity];
		int[] scores = new int[capacity];
		// Create a reader object
		try {
			reader = new Scanner(Paths.get(file_name));
			// Get all the players in scores.txt
			while (reader.hasNextLine()) {
				data = reader.nextLine().split(" ");
				names[index] = "";
				for (int i=0; i<data.length-1; i++) {
					names[index] += data[i] + " ";
				}
				names[index] = names[index].trim();
				scores[index] = Integer.parseInt(data[data.length-1]);
				index += 1;
			}
			if (index == capacity) index = capacity-1;
		}
		catch (IOException r) {
			// System.out.println("An error occured");
		}
		finally {
			if (reader != null) reader.close();
		}
		// Check if the new score is the highest
		if (scores[0] >= new_score) {
			confirmed = false;
		}
		// Add the score to the file
		if (confirmed) {
			System.out.println("New High Score!");
			// Ask player to enter a name
			System.out.print("Enter your name: ");
			String player_name = sc.next();
			// Shift all elements and add new score
			for (int i=capacity-1; i>0; i--) {
				names[i] = names[i-1];
				scores[i] = scores[i-1];
			}
			names[0] = player_name;
			scores[0] = new_score;
			// Create a writer object
			try {
				writer = new FileWriter(file_name);
				// Write new high score to the file
				for (int i=0; i<=index; i++) {
					if (i > 0) {
						writer.write("\n" + names[i] + " " + Integer.toString(scores[i]));
					}
					else {
						writer.write(names[i] + " " + Integer.toString(scores[i]));
					}
				}
				writer.close();
			}
			catch (IOException e) {
				// System.out.println("An error occured");
			}
			finally {
				// if (writer != null) writer.close();
			}
		}
	}
}