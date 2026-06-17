package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Random;

public class GameServer {
    // Player list
    private static final Set<PlayerHandler> playerHandlers = new HashSet<>();
    private static final Map<String, PlayerHandler> players = new HashMap<>();

    private static ArrayList<String> TEAM1 = new ArrayList<>();
    private static ArrayList<String> TEAM2 = new ArrayList<>();
    private static String gameState = "IN LOBBY";
    private static int numReady = 0;
    private static ArrayList<String> CURRENT_WORD_GRID = new ArrayList<>();
    private static int turnTeam = 1;
    private static final Set<String> guessedWordsTEAM1 = new HashSet<>();
    private static final Set<String> guessedWordsTEAM2 = new HashSet<>();
    private static String currentGuesserUsername = null;
    private static int TEAM1_SCORE;
    private static int TEAM2_SCORE;
    private static int TEAM1_RANK;
    private static int TEAM2_RANK;

    private static final ArrayList<String> TEAM1_WORDLIST = new ArrayList<>();
    private static final ArrayList<String> TEAM2_WORDLIST = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        final int PORT = 5001;
        System.out.println("Game server started at port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);

        Thread addPlayer = new Thread(() -> {
            try {
                while (true) {
                    new PlayerHandler(serverSocket.accept()).start();   //Create new PlayerHandler thread for each client
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        addPlayer.start();


    }

    private static class PlayerHandler extends Thread { //PlayerHandler thread
        private final Socket socket;
        private PrintWriter out;
        private String username;
        private Boolean isCaptain =  false;
        private int playerTeam;

        public PlayerHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                //Create input and output streams
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                //Handle join team and print the team and team member to the client
                synchronized (players){
                    username = in.readLine();
                    players.put(username, this);
                    playerHandlers.add(this);
                    if (TEAM1.size() <= TEAM2.size()){
                        TEAM1.add(username);
                        playerTeam = 1;
                        out.println("TEAM1");   //Output player's team to GameClient (for chatBox)
                    } else{
                        TEAM2.add(username);
                        playerTeam = 2;
                        out.println("TEAM2");   //Output player's team to GameClient (for chatBox)
                    }
                    handleSendTeam(playerTeam);
                }
                System.out.println(username + " connected");
                System.out.println("TEAM1: " + TEAM1.toString() + "\nTEAM2: " + TEAM2.toString());

                // Main run
                String input;
                while((input = in.readLine()) != null) {
                    //Lobby state
                    if(gameState.equals("IN LOBBY")){
                        if(input.equals("READY")){

                            System.out.println("Player "+ username+ " is Ready.");
                            synchronized (this){
                                numReady++;
                            }
                            if (numReady >= 4){
                                System.out.println("Game start in 4 sec....");
                                try{
                                    sleep(4000);
                                } catch (Exception ignored){

                                }
                                gameState = "IN GAME";
                                broadcast("START GAME");
                                startGameSetup();
                                System.out.println("Game start");
                            }
                        }
                    }
                    //In game state
                    if (gameState.equals("IN GAME")) {
                        if (input.startsWith("GUESS:::")) {
                            String guessedWord = input.substring("GUESS:::".length()).trim();
                            handleGuess(this, guessedWord);
                        }
                    }
                    //Leaderboard state
                    if(gameState.equals("LEADERBOARD")){
                        giveLeaderboard();
                        numReady = 0;
                        gameState = "IN LOBBY";
                    }

                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally { //When client leaves, cleanup and broadcast exit to other players
                System.out.println(username + " left the game");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(TEAM1.contains(username)) {
                    TEAM1.remove(username);
                } else{
                    TEAM2.remove(username);
                }
                synchronized (playerHandlers) {
                    playerHandlers.remove(this);
                    players.remove(username);
                }
            }
        } //end of run()

        public String getUsername() {
            return username;
        }
    }  //end of playerHandler

    private static void startGameSetup() {
        CURRENT_WORD_GRID.clear();
        TEAM1_WORDLIST.clear();
        TEAM2_WORDLIST.clear();
        guessedWordsTEAM1.clear();
        guessedWordsTEAM2.clear();
        //Keeping track of each team's score
        TEAM1_SCORE = 0;
        TEAM2_SCORE = 0;
        TEAM1_RANK = 0;
        TEAM2_RANK = 0;

        CURRENT_WORD_GRID = createWordList();   //call createWordList() and store in global variable
        System.out.println("TEAM1 Words: " + TEAM1_WORDLIST + "\nTEAM2 Words: " + TEAM2_WORDLIST);  //Debug
        turnTeam = 1;   //Set default as TEAM1 guesses first

        broadcast("WORD GRID");     //Broadcast word grid to each client
        for (String word : CURRENT_WORD_GRID) {
            broadcast(word);
        }

        //Randomly choose player from each team as captain
        //first team1
        Random r1 = new Random();
        String captain1String = TEAM1.get(r1.nextInt(TEAM1.size()));
        PlayerHandler captain1 = players.get(captain1String);
        if (captain1 != null) {
            captain1.isCaptain = true;
        }
        broadcast("TEAM 1 CAPTAIN: " + captain1String);
        StringBuilder wordsToDisplay = new StringBuilder("Your team's words are: ");
        for(int i=0; i<TEAM1_WORDLIST.size(); i++) {
            if(i == 0) {
                wordsToDisplay.append(TEAM1_WORDLIST.get(i));
            } else {
                wordsToDisplay.append(", ").append(TEAM1_WORDLIST.get(i));
            }
        }
        broadcast(wordsToDisplay.toString());   //Send team's word list to team captain

        //Next team2
        Random r2 = new Random();
        String captain2String = TEAM2.get(r2.nextInt(TEAM2.size()));
        PlayerHandler captain2 = players.get(captain2String);
        if (captain2 != null) {
            captain2.isCaptain = true;
        }
        broadcast("TEAM 2 CAPTAIN: " + captain2String);
        wordsToDisplay = new StringBuilder("Your team's words are: ");
        for(int i=0; i<TEAM2_WORDLIST.size(); i++) {
            if(i == 0) {
                wordsToDisplay.append(TEAM2_WORDLIST.get(i));
            } else {
                wordsToDisplay.append(", ").append(TEAM2_WORDLIST.get(i));
            }
        }
        broadcast(wordsToDisplay.toString());

        broadcast("CURRENT TURN:::TEAM" + turnTeam);    //Broadcast which team's tuen it is (default team1)
        chooseNewGuesser();
        broadcast("END INFO");
    }

    private static void chooseNewGuesser() {    //Randomly choose non-captain from each team to be a guesser each turn
        ArrayList<String> currentTeamList = (turnTeam == 1) ? TEAM1 : TEAM2;
        ArrayList<String> eligibleGuessers = new ArrayList<>();

        for (String username : currentTeamList) {
            PlayerHandler player = players.get(username);

            if (player != null && !player.isCaptain) {
                eligibleGuessers.add(username);     //Add each non-captain to eligibleGuessers pool
            }
        }

        if (eligibleGuessers.isEmpty()) {
            currentGuesserUsername = null;
            broadcast("GAME MSG:::No eligible guesser available for Team " + turnTeam);
            return;
        }

        Random random = new Random();
        currentGuesserUsername = eligibleGuessers.get(random.nextInt(eligibleGuessers.size()));

        broadcast("CURRENT GUESSER:::" + currentGuesserUsername);
        broadcast("GAME MSG:::Team " + turnTeam + "'s guesser is " + currentGuesserUsername);   //Broadcast team guesser(s)
    }

    private static ArrayList<String> createWordList(){
        ArrayList<String> wordGrid = new ArrayList<>();
        try {
            ArrayList<String> words = new ArrayList<>();
            FileReader fileReader = new FileReader("src/main/java/words.csv");  //Import CSV file with words for wordgrid
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String word = "";
            while((word=bufferedReader.readLine()) != null){
                words.add(word);
            }
            Random rand = new Random();
            Set<String> usedWords = new HashSet<>();

            //Add 25 words to shared wordGrid
            while(wordGrid.size() < 25) {
                int randNum = rand.nextInt(words.size());
                word = words.get(randNum);
                if(wordGrid.isEmpty() || !wordGrid.contains(word)) {
                    wordGrid.add(word);
                }
            }

            //Choose 5 words from grid to be each team's word list
            while (TEAM1_WORDLIST.size() < 5){
                int randNum = rand.nextInt(wordGrid.size());
                word = wordGrid.get(randNum);
                if(usedWords.isEmpty() || !usedWords.contains(word)){
                    TEAM1_WORDLIST.add(word);
                    usedWords.add(word);
                }
            }
            while (TEAM2_WORDLIST.size() < 5){
                int randNum = rand.nextInt(wordGrid.size());
                word = wordGrid.get(randNum);
                if(usedWords.isEmpty() || !usedWords.contains(word)){
                    TEAM2_WORDLIST.add(word);
                    usedWords.add(word);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println("WordGrid: " + wordGrid.toString() + "\nTeam1 Words: " + TEAM1_WORDLIST + "\nTeam2 Words: " + TEAM2_WORDLIST);     //debug
        return wordGrid;
    }



    private static void handleSendTeam(int userTeam){   //Broadcast each team's members for lobby
        for(PlayerHandler ph : playerHandlers){
            ph.out.println("lobby/team-info");
            ph.out.println("TEAM 1");
            for(String member : TEAM1){
                ph.out.println(member);
            }
            ph.out.println("END INFO");

            ph.out.println("TEAM 2");
            for(String member : TEAM2){
                ph.out.println(member);
            }
            ph.out.println("END INFO");
            ph.out.println("USER TEAM");
            ph.out.println(userTeam);
        }
    }

    private  static void broadcast(String message){ //Send a string to each client thread
        for (PlayerHandler ph : playerHandlers){
            ph.out.println(message);
        }
    }

    private static void handleGuess(PlayerHandler player, String guessedWord) {

        if (player.playerTeam != turnTeam) {
            player.out.println("GAME MSG:::It is not your team's turn.");
            return;
        }

        if (player.isCaptain) {
            player.out.println("GAME MSG:::Captains cannot guess.");
            return;
        }

        if (!player.username.equals(currentGuesserUsername)) {
            player.out.println("GAME MSG:::Only the current guesser can click a word.");
            return;
        }

        String actualWord = findMatchingWord(guessedWord);

        if (guessedWordsTEAM1.contains(actualWord) || guessedWordsTEAM2.contains(actualWord)) {
            player.out.println("GAME MSG:::That word was already guessed.");
            return;
        }

        if (TEAM1.contains(player.getUsername())) {     //If guesser makes a guess, add it to respective team's guessedWords
            guessedWordsTEAM1.add(actualWord);
        } else if (TEAM2.contains(player.getUsername())) {
            guessedWordsTEAM2.add(actualWord);
        }

        if (TEAM1_WORDLIST.contains(actualWord)) {
            broadcast("REVEAL:::TEAM1:::" + actualWord);
            broadcast("GAME MSG:::" + player.username + " guessed " + actualWord + ", it is Team 1's word.");
            TEAM1_SCORE++;  //If Team1 word is guessed, they get a point
            checkWin();

            if (player.playerTeam != 1) {
                endTurn();
            }

        } else if (TEAM2_WORDLIST.contains(actualWord)) {
            broadcast("REVEAL:::TEAM2:::" + actualWord);
            broadcast("GAME MSG:::" + player.username + " guessed " + actualWord + ", it is Team 2's word.");
            TEAM2_SCORE++;  //If Team2 word is guessed, they get a point
            checkWin();

            if (player.playerTeam != 2) {
                endTurn();
            }

        } else {
            broadcast("REVEAL:::NEUTRAL:::" + actualWord);
            broadcast("GAME MSG:::" + player.username + " guessed " + actualWord + ", it is neutral.");
            endTurn();
        }
    }

    private static String findMatchingWord(String guess) {
        for (String word : CURRENT_WORD_GRID) {
            if (word.equalsIgnoreCase(guess.trim())) {
                return word;
            }
        }
        return null;
    }

    private static void endTurn() {
        if(!gameState.equals("LEADERBOARD")) {
            turnTeam = (turnTeam == 1) ? 2 : 1;
            broadcast("GAME MSG:::Turn over.");
            broadcast("CURRENT TURN:::TEAM" + turnTeam);
            chooseNewGuesser();
        }
    }

    private synchronized static void checkWin() {
        if (TEAM1_SCORE == TEAM1_WORDLIST.size()) {     //If all of team1's words are guessed, they win
            broadcast("GAME MSG:::TEAM 1 WINS!");
            TEAM1_RANK = 1;
            TEAM2_RANK = 2;
            gameState = "LEADERBOARD";
        } else if (TEAM2_SCORE == TEAM2_WORDLIST.size()) {     //If all of team2's words are guessed, they win
            broadcast("GAME MSG:::TEAM 2 WINS!");
            TEAM1_RANK = 2;
            TEAM2_RANK = 1;
            gameState = "LEADERBOARD";
        }
//        System.out.println("checkWin() Called: \nTeam1 Score=" + TEAM1_SCORE + "\nTeam2 Score=" + TEAM2_SCORE + "\n"); //debug
    }

    private synchronized static void giveLeaderboard() {
        //broadcast LEADERBOARD to clients
        broadcast("LEADERBOARD");
        if(TEAM1_RANK == 1){
            broadcast("1");
            int choose = 5 - TEAM2_SCORE;
            String stringChoose = String.valueOf(choose);
            broadcast(stringChoose);
        } else if(TEAM2_RANK == 1){
            broadcast("2");
            int choose = 5 - TEAM1_SCORE;
            String stringChoose = String.valueOf(choose);
            broadcast(stringChoose);
        }

    }


}

