# Final Project - Codenames Server
#### CSCI 2020U: System Development and Integration

## Project Description
This program creates a server which can allows multiple users to join and play a modified version of the game Codenames by Vlaada Chvatil.

### Demo Video
[Demo Video](https://drive.google.com/file/d/12WVURCAUyMAXBi77ILPS826NDOCWZoil/view?usp=sharing)

### Modifications
Compared to the physical version of the game. These include:
- Fewer words to be guessed per team, leading to a shorter game.
- Custom theming with unique colours chosen for the UI.
- A specified "Guesser" role that will switch every turn, to prevent chaos of anyone being allowed to make a guess.

### How To Run
To use the program yourself, start by clicking the green "Code" button above on this page and copy the link shown. Next, open IntelliJ (https://www.jetbrains.com/idea/download/) or another IDE of your choice (exact instructions may vary on a different IDE). Navigate to the "Git" menu at the top of the window and choose "Clone...". Once the clone window pops up, paste the link in the provided box, select your directory (or leave the default) and click "Clone". Now navigate to src/main/java/server/ and run both GameServer.java and ChatServer.java. Once the servers are running, navigate to src/main/java/client and run GameClient. Select usernames and join with as many players as you would like (at least 4), and once everyone has selected "Ready," the game will begin. Follow the rules of the game until a team reaches their win condition of having all its words guessed.

### Game Rules
Players: 4+<br>
Split into 2 teams, automatically done as players join the server.<br>
When the game begins, 25 words will be shown to all players in a 5x5 grid in the window. Players will also have the option to chat with all other players at the bottom of the window.
The server will assign 1 player from each team to be a captain, who will recieve a random selection of 5 unique words out of the 25 shown. The captain must use the chatbox to give a **one-word** clue to their team that will connect as many of their words together as possible, helping their team to come up with guesses on what the words may be. The clue should also contain the number of words the captain is trying to help their team guess, to better guide them. Once a team makes a guess that is not in their captain's word list (this may be in the enemy captain's word list, getting them 1 word closer to winning), their turn is over, and the other team begins their turn. This repeats until either team's 5 words have all been guessed, ending the game with that team's victory.

### Libraries Used
javax
java

### Project Structure
<img width="827" height="636" alt="Screenshot 2026-04-17 at 5 04 25 PM" src="https://github.com/user-attachments/assets/ff45a2e3-4534-4fb3-9b46-0e9390524976" />


### References
https://www.iditect.com/program-example/how-to-set-an-image-as-a-background-for-frame-in-swing-gui-of-java.html
https://stackoverflow.com/questions/5895829/resizing-image-in-java
Drawings in the game were made using Pixilart: https://www.pixilart.com

chatServer and chatClient used the in-class example of the multi-threaded chat messenger as basis for implementing inter-user messaging 
