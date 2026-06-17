# CSCI 2020U Project Charter & Work Contract
## 1. Project Overview
* **Project members:** An Le, Colin Hewlett, Lucas Yantsis, Matthew MacDonal, Sajan Selvasangar
* **Product Name:** Mums the word.
* **Project Description:** We are creating a guessing game inspired by the board game Codename. It will be a team-based guessing game where when given a shared array of words, each team is given a clue and must guess as many correctly related words based on their clue as they can. Once the timer for the round runs out, the team with highest score gets a point (teams earn points guessing a correct word on the shared board, and lose points when they guess an incorrect word).
* **Tech Stack:** Java/JavaFX.
## 2. Team Work Contract
* **Communication Channel:** Discord
* **Meeting Schedule:** Tuesdays & Thursdays from 1:40pm to 2:40pm
* (time & date is subject to change to accommodate exceptional circumstances)
* **Conflict Resolution:** If team members does not contribute to assigned tasks, we
will first attempt to contact the team member for a valid excuse. If none is given in
a timely manner, preferably before 1 week after the update request, and/or the excuse is
not valid, is the problem escalated to the instructor. All team members are also resposible
to enable other members to execute their contributions (i.e., push the code needed, etc)
and give updates to work done in a timely manner.
## 3. Work Division & Contribution Report
*Note: The "Actual Contribution" column should be updated at the time of final
submission. You may edit the "Task" column to reflect your own tasks.*
| Task / Module | Assigned Member (Plan) | Actual Contribution (Final) |
| :--- | :--- | :--- |
| **Multi-threaded Server** | [Lucas] | Lucas has implemented the chat features of the game, allowing players to talk with each other and has helped Colin and Sajan in making the game mechanics work with the server|
| **Socket Networking** | [Lucas] | Lucas has done a good job making sure the client and server can talk with each other uniformly, so interruptions don't interfere with the game rounds|
| **GUI Implementation** | [Matt] | Everyone helped in creating the GUI, with An making the lobby and main GUI for our game, Colin making the buttons for the games, Lucas creating the ui interface for the chat, and Sajan creating the UI for the leaderboard, with Matt and An further refining each GUI to be visually appealing|
| **Persistence (File I/O)** | [An Le] | There was not a lot of file usage in our game, with only the words used in our game being stored in a CSV, which was created and populated by Sajan, then implemented into our game by Lucas and Colin|
| **UX/Sound Effects** | [Colin Hewlett] | Due to time constraints, we were not able to add sound effects |
| **Leaderboard** | [Colin Hewlett] | Sajan created the design of the leaderboard, then implemented the ui for the leaderboard |
| **Lobby/Waiting Room** | [An Le] | An implemented the back-end for the lobby and inital ui desgin of the lobby |
| **Game/GUI Design** | [Matt] | Matt and An refined the ui created by other team members to be something more presentable and visually appealing |
| **Game Mechanics** | [Sajan Selvasangar] | Sajan, with Colin's help, created the server-side of the game mechanics that were further refined by Colin and Lucas, who both added the client side of the game with the aid of An in certain parts|
| **Documentation/README** | [Sajan Selvasangar] | Colin created the readme, and Sajan added more details to it, An made the video|
## 4. Final Contribution Status (Tag one at Final Submission)
At the end of the project, the team must agree on one of the following tags:
* **[#] (1) EVEN CONTRIBUTION:** All members met expectations from the original
  charter.
* **[ ] (2) UNEVEN CONTRIBUTION:** One or more members did not meet expectations.
