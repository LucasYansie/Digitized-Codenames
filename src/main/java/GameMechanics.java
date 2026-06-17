import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.Random;

public class GameMechanics {
    private Timer timer = new Timer();
    private Map<String, Integer> vote;
    private final ArrayList<String> words = new ArrayList<>();
    private final ArrayList<String> randomWords = new ArrayList<>();
    private final ArrayList<String> captainWords = new ArrayList<>();

    public void run(){
        createWordList();
        wordsToGive();
    }



    private void createWordList(){
        try {
            FileReader fileReader = new FileReader("words.csv");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String word = "";
            while((word=bufferedReader.readLine()) != null){
                words.add(word);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> getWords(){
        return this.words;
    }

    public void addPlayersToMap(String username){
        if(vote.containsKey(username)){
            vote.put(username, 0);
        }
    }

    public void voteForPlayer(String username){
        if(vote.containsKey(username)){
            int voteCount = vote.get(username);
            voteCount++;
            vote.replace(username, voteCount);
        }
    }

    public String countVote(){
        String captain = "";
        for(Map.Entry<String, Integer> player : vote.entrySet()){
            if(captain.isEmpty()){
                captain = player.getKey();
            }
            else if(player.getValue() > vote.get(captain)){
                captain = player.getKey();
            }
        }

        return captain;
    }

    private void wordsToGive(){
        Random r = new Random();
        int maxWords = words.size()-1;
        int randomIndex = 0;
        String randomWord = "";

        for(int i=0; i<6; i++){
            randomIndex = r.nextInt(maxWords);
            randomWord = words.get(randomIndex);
            randomWords.add(randomWord);
        }
    }

    public ArrayList<String> giveWordsToTeam(){
        return this.randomWords;
    }

    public void getCaptainsLinkedWords(String word) {
        captainWords.add(word);
    }
}
