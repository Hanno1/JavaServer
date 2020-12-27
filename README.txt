A simple Java Server

1. Milestone: simple chat System which works via console.
2. Milestone: GUI implementation for the first Milestone and chatroom realisation
3. Milestone: private chats and Rooms with password

1. Every new client is saved and handled as a thread (since we have to handle multiple clients). If the client writes a line it is send to the server and the server acts 
according to the input: if the first character is a '!' it works with the line as a command to the server. Else it displays the line for every client online at the 
moment. The difficult part was to succesfully connect and close the client without getting errors in the client or server console. This was done using try and catch 
phrases. The Realisation of the communication is rather simple, since we dont have many commands yet.

2. Now i needed to implement a grafic interface for the server. At first i run into a problem with the JTextArea, cause it cant display too many lines at once. 
So the client crushed and at first i hadnt any clue why. After some Experiments i threaded some functions and made breaks (thread.sleep) while writing output.
For the Implementation of the chatrrom system i created a new class called Chatroom with attributes like name, list with clients and so on. Finally i saved a list with 
all chatrooms in the server so it could be accessed and displayed easely. 
For the gui implementation i added a serverframe and a client frame  programm. 
Finally i added a readAndWrite to save the userlist, the log and all banned users in a csv file and display them in the server at any time (as well as reset them).

3. At first i didnt know how i wanted to implement private chats. I decided to make a popup menu then you click on a user name and there you can start a private chat. The 
popup menu opens a new textfield with input, textarea and a button. Therefor i created a new class called ClientPrivateFrame. The conversations are stored and can be 
accesed in the server. For chatrooms with passwords i changed the room creation button in the client, so you can enter a password as well. Additionally i expanded the 
chatroom class in the server and added another constructor which initialises a given password as well. This was a bit tricky since i reoved the client from a room and 
after this i checked for a password and if the password was wrong the client didnt get access. But this resulted in a null pointer exception since the client had no 
chatroom. After some experimentation i solved this too.

This is my first real project so sorry for bad english and commenting in the files.