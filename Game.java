import java.util.Scanner;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.FileWriter;

public class Game {
    public static void main(String[] args) {	
		// Create the instances and variables which are required
		Scanner sc = new Scanner(System.in);
		Random r = new Random(System.currentTimeMillis());
		Player player1 = new Player(); // Human
		Player player2 = new Player(); // AI
		Board board = new Board();
		int dealer = r.nextInt(2); // 0-->Human | 1-->AI
		int task_type; // -1-->Endgame | 0-->Continue | 1-->Cut | 2-->Pisti
		int last_card_winner = 0; // 1-->Human | -1-->AI
        // Create a deck, shuffle and cut the deck
		Card[] initial_cards = createDeck();
		int initial_cards_size = initial_cards.length;
		initial_cards = shuffleDeck(r, initial_cards);
		initial_cards = cutDeck(r, initial_cards);
        // Distribute 4 cards for each player and the board
		initial_cards_size = distributeCards(player1, player2, dealer, initial_cards, initial_cards_size);
		initial_cards_size = placeCardsOnBoard(board, initial_cards, initial_cards_size);
        // Game Loop
        while (true) {
            // If player 1 and player 2 have no card and there are enough cards to distribute, distribute 4 cards for each player
			if (player1.getSize() == 0 && player2.getSize() == 0 && initial_cards_size > 7) {
				initial_cards_size = distributeCards(player1, player2, dealer, initial_cards, initial_cards_size);
			}
            // Print the board
			board.printCard();
            // Player 1 Turn
			player1.printCards("Player-1");
			task_type = player1.play(sc, board);
            // Calculate Player 1 Score
			last_card_winner = calculateScore(player1, board, task_type, 1, last_card_winner);
			System.out.print("\033[H\033[2J"); // Clear the console and move the cursor up
			// Print information
			if (task_type == 2) System.out.println("Player-1 made a pisti!");
			else if (task_type == 1) System.out.println("Player-1 made a cut!");
			if (task_type != -1) { // If the first player has no card and could not play, do not ask the second player to play, end the game
				// Player 2 Turn
				task_type = player2.playAI(r, board);
				// Print information
				if (task_type == 2) System.out.println("Player-2 made a pisti!");
				else if (task_type == 1) System.out.println("Player-2 made a cut!");
				// Calculate Player 2 Score
				last_card_winner = calculateScore(player2, board, task_type, -1, last_card_winner);
			}
            // Check if the game is end
			if ((player1.getSize() == 0 && player2.getSize() == 0 && initial_cards_size == 0) || (task_type == -1)) {
				// Move all the cards on the board to the player who cut or made a pisti lastly
				Card card;
				Card[] board_cards = board.getCards();
				if (board.getSize() != 0) {
					if (last_card_winner == 1) {
						player1.setTakenCardsCount(player1.getTakenCardsCount()+board.getSize());
						for (int i=0; i<board.getSize(); i++) {
							card = board_cards[i];
							player1.setScore(player1.getScore()+card.getPoint());
						}
						board.clearBoard();
					}
					else if (last_card_winner == -1) {
						player2.setTakenCardsCount(player2.getTakenCardsCount()+board.getSize());
						for (int i=0; i<board.getSize(); i++) {
							card = board_cards[i];
							player2.setScore(player2.getScore()+card.getPoint());
						}
						board.clearBoard();
					}
				}
                // Add +3 additional points to the player taken more cards
				if (player1.getTakenCardsCount() > player2.getTakenCardsCount()) player1.setScore(player1.getScore()+3);
				else player2.setScore(player2.getScore()+3);
                // Print the scores
				System.out.println("Player-1 Score: " + player1.getScore());
				System.out.println("Player-2 Score: " + player2.getScore());
	            // Print the winner
				if (player1.getScore() > player2.getScore()) {
					System.out.println("\n!!!Player-1 has won!!!\n");
				}
				else if (player1.getScore() < player2.getScore()) {
					System.out.println("\n!!!Player-2 has won!!!\n");
				}
				else {
					System.out.println("\n!!!Draw!!!\n");
				}
				// Save the high score
				saveHighScore(sc, player1.getScore());
				break;
			}
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
	public static int distributeCards(Player player1, Player player2, int dealer, Card[] initial_cards, int initial_cards_size) {
		// Distribute 4 cards for each player
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
			player.setTakenCardsCount(player.getTakenCardsCount()+board.getSize());
			board.clearBoard();
			if (turn == 1) last_card_winner = 1;
			else last_card_winner = -1;
		}
		else if (task_type == 1) {                   // If J or the same rank took all the cards (cut)
			player.setTakenCardsCount(player.getTakenCardsCount()+board.getSize());
			for (int i=0; i<board.getSize(); i++) {
				card = board_cards[i];
				player.setScore(player.getScore()+card.getPoint());
			}
			board.clearBoard();
			if (turn == 1) last_card_winner = 1;
			else last_card_winner = -1;
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
				if (data[0].equals(" ")) continue;
				names[index] = data[0];
				scores[index] = Integer.parseInt(data[1]);
				index += 1;
			}
			if (index == capacity) index = capacity-1;
		}
		catch (IOException r) {
			System.out.println("An error occured");
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
			// Check if the player entered a valid name
			String player_name;
			while (true) {
				System.out.print("Enter your name: ");
				player_name = sc.nextLine();
				if (player_name.split(" ").length == 1) break;
				System.out.println("Please do not use space while writing the name!");
			}
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
				System.out.println("An error occured");
			}
			finally {
				//if (writer != null) writer.close();
			}
		}
	}
}