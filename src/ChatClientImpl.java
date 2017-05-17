import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ChatClientImpl extends UnicastRemoteObject implements ChatClient {
	
	boolean amRegistered = false;
	List<String> chatRoomList = new ArrayList<String>();
	String myName;
	ChatEntity myInfo;
	
/*  Constructor for the ChatClientImpl class */ 
	public ChatClientImpl() throws RemoteException {
		
	}

/*  Display the messages send by other clients that joined the same chat room */	
	@Override
	public void showMessage(String message) throws RemoteException {
		System.out.println(message);
	}
	
/* Register the chat client with the Registry Server */	
	public void register(RegistryServer stub, String name, String location) throws RemoteException, AlreadyBoundException {
		ChatEntity chatClient = new ChatEntity();
		chatClient.setName(name);
		chatClient.setStatus("New");
		chatClient.setLocation(location);
		chatClient.setType("chatclient");
		ChatEntity chatClientReceived = new ChatEntity();
		chatClientReceived  = stub.register(chatClient);
		String chatClientStatus = chatClientReceived.getStatus();
		if(chatClientStatus.equalsIgnoreCase("Registered")) {
			amRegistered = true;
			String chatClientName = chatClientReceived.getName();
			myName = chatClientName;
			myInfo = chatClientReceived;
			if(chatClientName.equalsIgnoreCase(name)) {
				System.out.println("\nSuccessfully registered chat client " + chatClientName + ".");
			} else {
				System.out.println("\nThe name " + name + " is not available. So successfully " +
						"registered with the name " + chatClientName + ".");
			}
		}
		else
			System.out.println(chatClient.getName() + " not registered.");
    }

/*  Get the list of available chat rooms. Only the names are displayed if the client is registered.
 *  The chat room names and the chat room description is displayed when the client is registered. */	
	public void getlistOfChatRooms(RegistryServer stub) throws RemoteException {
		List<ChatEntity> chatEntityList = new ArrayList<ChatEntity>();
		chatEntityList = stub.getListOfChatRooms();
		if(chatEntityList.isEmpty()) {
			System.out.println("The list of chat rooms is empty");
		} else {
			if(!amRegistered) {
				System.out.println("The List of chat rooms:");
				Iterator<ChatEntity> chatEntityListItr = chatEntityList.iterator();
				while(chatEntityListItr.hasNext()) {
					ChatEntity cEntity = (ChatEntity) chatEntityListItr.next();  
					String name = cEntity.getName();
					System.out.println(name);
				}
			
				System.out.println("To see the information about the chat rooms, you need to register.");
			}
			else if(amRegistered) {
				System.out.println("The List and information of chat rooms:");
				Iterator<ChatEntity> chatEntityListItr = chatEntityList.iterator();
				while(chatEntityListItr.hasNext()) {
					ChatEntity cEntity = (ChatEntity) chatEntityListItr.next();  
					String name = cEntity.getName();
					String description = cEntity.getDescription();
					System.out.println(name + ": " + description);
				}
			}
		}
	}
	
/*  Join the chat room from the list of available list of chat rooms */	
	public void join(ChatRoom stub, String name) throws RemoteException {
		ChatEntity chatClient = new ChatEntity();
		boolean contains = false;
		Iterator<String> chatRoomListItr = chatRoomList.iterator();
		while(chatRoomListItr.hasNext()) {
			String cRoomName = (String) chatRoomListItr.next();  
			if(cRoomName.equalsIgnoreCase(name)){
				contains = true;
			}
		}
		if(!contains) {
			chatClient.setName(myName);
			chatClient.setType("chatclient");
			boolean confirmJoin = stub.join(chatClient, this);
			if(confirmJoin) {
				myInfo.setStatus("Active");
				updateChatRoomList(name);
				System.out.println("Successfully joined chat room " + name + ".");
			}
		}
		else
			System.out.println("You have already joined " + name + ".");
    }
	
/*  Update the chatRoomList after successfully joining the chat room */    
    synchronized public void updateChatRoomList(String name) {
    	chatRoomList.add(name);
	}
    
/*  Get information about a particular chat room */  
    public void getInfo(RegistryServer stub, String name) throws RemoteException {
		ChatEntity chatRoom = new ChatEntity();
		chatRoom.setName(name);
		ChatEntity chatRoomInfo = new ChatEntity();
		chatRoomInfo = stub.getInfo(chatRoom);
		if(chatRoomInfo != null) {
			System.out.println("The info of the chat room " + chatRoomInfo.getName() + " are:");
			System.out.println("Status: " + chatRoomInfo.getStatus());
			System.out.println("Location: " + chatRoomInfo.getLocation());
			System.out.println("Description: " + chatRoomInfo.getDescription());
		} else {
			System.out.println("The info of the specified chat room is not found.");
		}
	}
 
/*  Send message in chat rooms the client have joined */  
    public synchronized void sendMessage(Registry registry) throws Exception {
    	if(amRegistered) {
    		int numRoomJoined = chatRoomList.size();
    		if(numRoomJoined > 0) {
    			
				boolean done = false;
				while(!done) {
			    	System.out.println("Following is the list of rooms you joined:");
			    	Iterator<String> chatRoomListItr = chatRoomList.iterator();
					while(chatRoomListItr.hasNext()) {
						String cRoomName = (String) chatRoomListItr.next();  
						System.out.println(cRoomName);
					}
					
					BufferedReader stdinp = new BufferedReader(new InputStreamReader(System.in));
					String name = chatRoomList.get(0);
					String nameRead = "";
					
					boolean flag = false;
					while(!flag) {
						System.out.println("\nEnter room name [default room name is " + name + "]:");
						nameRead = stdinp.readLine();
						if(nameRead.equalsIgnoreCase("") || (nameRead == null)) {
							nameRead = name;
							flag = true;
						} 
						if(!flag) {
							chatRoomListItr = chatRoomList.iterator();
							while((chatRoomListItr.hasNext())) {
								String cRoomName = (String) chatRoomListItr.next();  
								if(nameRead.equalsIgnoreCase(cRoomName))
									flag = true;
							}
						}
						
						if(flag) {
							sendMessageToRoom(nameRead, registry);
						}
					}
					
					boolean flagChangeRoom = false;
	    			while(!flagChangeRoom) {
	    				System.out.println("Do you want to send message to another room? Type Yes or No ");
	    				String doneSending = stdinp.readLine();
	    				if(doneSending.equalsIgnoreCase("No") || doneSending.equalsIgnoreCase("Yes"))
	    					flagChangeRoom = true;
	        			if(doneSending.equalsIgnoreCase("No"))
	        				done = true;
	    			}
				
				}
				
    		} else {
    			System.out.println("To chat you need to join one of the available rooms.");
    		}
    	} else {
			System.out.println("To chat you need to register.");
		}
    }
 
/*  Send message to selected chat room */    
    public void sendMessageToRoom(String roomName, Registry registry)  {
    	try {
	    	ChatEntity chatClient = new ChatEntity();
	    	BufferedReader stdinp = new BufferedReader(new InputStreamReader(System.in));
	    	String message = "";
			System.out.println("Enter your message (A single line containing '.' switches to a different chat room) :");
			while(!(message.equals(".")) || (message.equalsIgnoreCase(""))) {
				chatClient.setName(myName);
				chatClient.setType("chatclient");
				message = stdinp.readLine();
				ChatRoom stub = (ChatRoom) registry.lookup(roomName);
				if (!(message.equals("."))) {				
					stub.talk(roomName, roomName + "::" + message, chatClient, this);
				}
			}
    	} catch (AccessException e) {
			
		} catch (RemoteException e) {
			
		} catch (NotBoundException e) {
			System.out.println("\nThe chatroom " + roomName + " is no longer available. " +
					"\nNote your list of joined chat rooms will not be updated yet. To see updated " +
					"list of joined chat rooms you have to use the option 6. MyInfo");
		} catch (IOException e) {
			
		}
    }
    
/*  Leave a selected chat room */ 
    public void leave(ChatRoom stub, String name) throws RemoteException {
		ChatEntity chatClient = new ChatEntity();
		boolean contains = false;
		if(amRegistered) {
			Iterator<String> chatRoomListItr = chatRoomList.iterator();
			while(chatRoomListItr.hasNext()) {
				String cRoomName = (String) chatRoomListItr.next();  
				if(cRoomName.equalsIgnoreCase(name)){
					contains = true;
				}
			}
			if(contains) {
				chatClient.setName(myName);
				chatClient.setType("chatclient");
				boolean confirmLeave = stub.leave(chatClient, this);
				if(confirmLeave) {
					removeChatRoomList(name);
					System.out.println("Successfully left chat room " + name + ".");
				}
			}
			else
				System.out.println("You cannot leave a the chat room " + name + " you didn't join.");
		} else {
			System.out.println("To leave a chat room you need to register.");
		}
    }
    
/*  Update the list of chat rooms the client joined */ 
    public synchronized void removeChatRoomList(String name)  {
		Iterator<String> chatRoomListItr = chatRoomList.iterator();
		while(chatRoomListItr.hasNext()) {
			String cRoomName = (String) chatRoomListItr.next();   
			if(cRoomName.equalsIgnoreCase(name)){
				chatRoomListItr.remove();
			}
		}
	}
    
/*  Deregister the client from the registry server */ 
    public void deregister(RegistryServer stub) throws RemoteException, NotBoundException {
    	boolean confirmDeRegistration = false;
		if(amRegistered) {
			ChatEntity chatClient = new ChatEntity();
			chatClient.setName(myName);
			chatClient.setType("chatclient");
			confirmDeRegistration = stub.deregister(chatClient);
			if(confirmDeRegistration) {
				System.out.println("Successfully deregistered chat client " + myName + ".");
				amRegistered = false;
			} else {
				System.out.println("You cannot deregister the chat client " + myName + ".");
			}
		} else {
			System.out.println("You cannot deregister the chat client unless you have registered.");
		}
    }
    
/*  Get information about the client i.e name, location, status, rooms joined */ 
    public void getmyInfo(Registry registry) {
    	System.out.println("My Name: " + myInfo.getName());
    	System.out.println("My Location: "+ myInfo.getLocation());
    	System.out.println("My Status: "+ myInfo.getStatus());
    	if(chatRoomList.isEmpty()) {
    		System.out.println("You have not joined any chat rooms yet.");
    	} else {
	    	System.out.println("Chat rooms I joined:");
	    	Iterator<String> chatRoomListItr = chatRoomList.iterator();
//	    	boolean notBound = false;
	    	while(chatRoomListItr.hasNext()) {
	    		boolean notBound = false;
				String cRoomName = (String) chatRoomListItr.next();  
				try {
					ChatRoom stub = (ChatRoom) registry.lookup(cRoomName);
				} catch (AccessException e) {
					
				} catch (RemoteException e) {
					
				} catch (NotBoundException e) {
					notBound = true;
					System.out.println("The chat room " + cRoomName + " you joined is no longer available. " +
							"So it will be removed from your list of joined chat rooms.");
					chatRoomListItr.remove();
				}
				if(!notBound) {
					System.out.println(cRoomName);
				}
			}
    	}
    }
	
/*  Menu provided to the chat client to register themselves */ 
    public void chatClientMenu() {
    	System.out.println("\nOptions : \n 1. Sign in with Registry." +
				 					   "\n 2. Show Available Chat Rooms." +
				 					   "\n 3. View Info of a Particular Chat Room." +
				 				       "\n 4. Join a Chat Room." +
				 					   "\n 5. Chat." +
				 				       "\n 6. MyInfo." +
				 					   "\n 7. Leave a Chat Room." +
				 					   "\n 8. Sign out of Registry.");
    	System.out.println("\nPlease enter your choice");
    }
    
    public static void main(String args[]) throws Exception {
        String host = args[0];
        try{
            Registry registry = LocateRegistry.getRegistry(host);
            RegistryServer stub = (RegistryServer) registry.lookup("RegistryServer");
            
            BufferedReader stdinp = new BufferedReader(new InputStreamReader(System.in));
            ChatClientImpl myChatClient = new ChatClientImpl(); 
            while (true) {  
            	boolean done = false;
            	myChatClient.chatClientMenu();
            	try {
            		String echoline = stdinp.readLine();
	        		StringTokenizer st = null;
	        		if(echoline.equals(null) || echoline.equals("")) {
	        			
	        		} else {
	        			st = new StringTokenizer(echoline);       		
	        			String tag = st.nextToken(); 
	        			if(tag.equalsIgnoreCase("1")) {
	        				if(!(myChatClient.amRegistered)) {
		        				String name = "";
			        			while(name.equals("")) {
			        				System.out.println("Enter Client Name: ");
			        				 name = stdinp.readLine();
			        			} 
			        			
			        			String location = "";
			        			while(location.equals("")) {
			        				System.out.println("Enter location: ");
			        				location = stdinp.readLine();
			        			} 
			        			
			        			myChatClient.register(stub, name, location);
	        				} else {
	        					System.out.println("You have already registered.");
	        				}
	        			} else if(tag.equalsIgnoreCase("2")) {
	        				myChatClient.getlistOfChatRooms(stub);
	        			} else if(tag.equalsIgnoreCase("3")) {
	        				if(myChatClient.amRegistered) {
		        				String name = "";
			        			while(name.equals("")) {
			        				System.out.println("Enter Room Name: ");
			        				 name = stdinp.readLine();
			        			} 
			        			
			        			myChatClient.getInfo(stub, name);
	        				} else {
	        					System.out.println("To see the information about the chat rooms, you need to register.");
	        				}
	        				
	        			} else if(tag.equalsIgnoreCase("4")) {
	        				if(myChatClient.amRegistered) {
	        					while(!done) {
			        				String name = "";
				        			while(name.equals("")) {
				        				System.out.println("Enter Room Name: ");
				        				 name = stdinp.readLine();
				        			} 
				        			try {
					        			ChatRoom chatroomStub = (ChatRoom) registry.lookup(name);
					        			myChatClient.join(chatroomStub, name);
				        			} catch(NotBoundException e) {
				        				System.out.println("Use the names from the available rooms.");
				        			}
				        			
				        			boolean flag = false;
				        			while(!flag) {
				        				System.out.println("Do you want to join another chat room? Type Yes or No ");
				        				String doneJoining = stdinp.readLine();
				        				if(doneJoining.equalsIgnoreCase("No") || doneJoining.equalsIgnoreCase("Yes"))
					        				flag = true;
					        			if(doneJoining.equalsIgnoreCase("No"))
					        				done = true;
				        			}
	        					}
			        			
	        				} else {
	        					System.out.println("To join chat rooms you need to register.");
	        				}
	        			} else if(tag.equalsIgnoreCase("5")) {
		        			myChatClient.sendMessage(registry);
	        			} else if(tag.equalsIgnoreCase("6")) {
		        			myChatClient.getmyInfo(registry);
	        			} else if(tag.equalsIgnoreCase("7")) {
	        				if(myChatClient.amRegistered) {
		        				String name = "";
			        			while(name.equals("")) {
			        				System.out.println("Enter Room Name: ");
			        				 name = stdinp.readLine();
			        			} 
			        			try {
				        			ChatRoom chatroomStub = (ChatRoom) registry.lookup(name);
				        			myChatClient.leave(chatroomStub, name);
			        			} catch(NotBoundException e) {
			        				System.out.println("Use the names from the available rooms.");
			        			}
	        				} else {
	        					System.out.println("To leave a chat room you need to register and join.");
	        				}
	        			} else if(tag.equalsIgnoreCase("8")) {
	        				
		        			myChatClient.deregister(stub);
	        			} else {
		        			System.out.println("Malformed Query");
		        		}
	        		} 
            	} catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
