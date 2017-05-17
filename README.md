# chat-room
Distributed Chat Room

There are eight java files implementing the distributed chat rooms
service using Java RMI: (1) RegistryServer.java, (2)
RegistryServerImpl.java, (3) ChatRoomProvider.java, (4) ChatRoom.java,
(5) ChatRoomServerThread.java, (6) ChatClient.java, (7)
ChatClientImpl.java, and (8) ChatEntity.java.  There is also a
myPolicy file that specifies the security policy.


Place all the source files and the policy file in the same directory.

1. Compile the source files
-- javac *.java

2. Start the rmi registry
-- rmiregistry
(Note: rmiregistry has to be run in the same directory as the classes)

3. In a new terminal start the Registry Server
-- java -Djava.security.policy=myPolicy RegistryServerImpl

4. In a new terminal start the ChatRoomProvider 
-- java -Djava.security.policy=myPolicy ChatRoomProvider localhost
(Note: To start another ChatRoomProvider, open a new terminal and use the above command.)

5. In a new terminal start the ChatClient
-- java -Djava.security.policy=myPolicy ChatClientImpl localhost
(Note: To start another ChatClient, open a new terminal and use the above command.)


The options provided by the ChatRoomProvider are as follows:
Options : 
 1. Create & Register a New Chat Room.
 2. View Registered Chat Rooms.
 3. Deregister a Chat Room.

1. Create & Register a New Chat Room. -- Allows the provider to create
and register a new chat room by entering a name, location and
description. 

2. View Registered Chat Rooms. -- Allows the provider to view the chat
rooms that the provider registered with the Registry Server.

3. Deregister a Chat Room. -- Allows the provider to deregister a chat
room that the provider registered with the Registry Server.


The options provided by the ChatRoomClientImpl are as follows:

Options : 
 1. Sign in with Registry.
 2. Show Available Chat Rooms.
 3. View Info of a Particular Chat Room.
 4. Join a Chat Room.
 5. Chat.
 6. MyInfo.
 7. Leave a Chat Room.
 8. Sign out of Registry.

1. Sign in with Registry. -- Allows the client to register with the
registry server by entering a name and location. 

2. Show Available Chat Rooms. -- Allows the client to view the list of
available chatrooms. If the client is not registered then only the
names of the chat room are displayed; otherwise the client is able to
the see the names and description of the available chat rooms.

(Note: Chat room names are case sensitive.)

3. View Info of a Particular Chat Room. -- A registered client can
enter the name of a chat room and see detailed info of a particular
chat room.

4. Join a Chat Room. -- A registered client can join a chat room by
entering the name of an available chat room. After this, the system
prompts: "Do you want to join another chat room? Type Yes or No". On
typing "Yes", the client is given the option to enter another chat
room.  Taking the "No" option takes the client back to the main menu.

5. Chat. -- After joining a chat room/s, the registered client can
chat using this option. The client is shown the list of chat room/s
the client has joined and is prompted to enter the name of the chat
room the client wants to send message to.  The client has the option
to either type a specific room name, or use the default name provided
by hitting the "enter" key.  Following standard Unix convention from
pre-GUI days, a client can exit out of a specific chat communication
by entering a single "." character as the only character in a single
line.  When this is done, the system prompts: "Do you want to send
message to another room? Type Yes or No". If the client types "Yes"
then the option above to enter a chat room name is shown.

6. MyInfo. -- This option shows the client's own information, e.g.,
name, location, status, chat rooms joined, etc.

7. Leave a Chat Room. -- A client can leave the chat room by entering
the name of the room that the client has joined.

8. Sign out of Registry. -- A client deregisters from the
RegistryServer.


If you have further questions, email me at dasanuiit@gmail.com.
