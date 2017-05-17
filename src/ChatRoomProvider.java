import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ChatRoomProvider extends UnicastRemoteObject {
	
	List<ChatEntity> chatRoomRegisteredList = new ArrayList<ChatEntity>();
	
/*  Constructor for the ChatRoomProvider class */ 
	public ChatRoomProvider() throws RemoteException {
		
	}

/*  Register the chat room with the Registry Server */	
	public synchronized void register(RegistryServer stub, String name, String location, String description) throws RemoteException {
		ChatEntity chatRoom = new ChatEntity();
		chatRoom.setName(name);
		chatRoom.setStatus("New");
		chatRoom.setLocation(location);
		chatRoom.setDescription(description);
		chatRoom.setType("chatroom");
		ChatEntity chatRoomReceived = new ChatEntity();
		chatRoomReceived  = stub.register(chatRoom);
		String chatRoomStatus = chatRoomReceived.getStatus();
		if(chatRoomStatus.equalsIgnoreCase("Registered")) {
			String chatRoomName = chatRoomReceived.getName();
			if(chatRoomName.equalsIgnoreCase(name)) {
				System.out.println("\nSuccessfully registered chat room " + chatRoomName + ".");
			} else {
				System.out.println("\nThe name " + name + " is not available. So successfully " +
						"registered chat room with the name " + chatRoomName + ".");
			}
			updateChatRoomRegisterdList(chatRoomReceived);
			new ChatRoomServerThread(chatRoomName).start();
			
		}
		else
			System.out.println(chatRoom.getName() + " not registered.");
    }
	
/*  Update the chatRoomRegisteredList after successful registration of the chat room */    
    synchronized public void updateChatRoomRegisterdList(ChatEntity chatRoomReceived) {
    	chatRoomRegisteredList.add(chatRoomReceived);
	}
    
/*  View the chat rooms registered by the chat room provider */     
    synchronized public void viewRegisteredChatRooms() {
    	if(chatRoomRegisteredList.isEmpty()) {
			System.out.println("\nThe list of registered chat rooms is empty.");
		} else {
    	Iterator<ChatEntity> chatRoomRegisteredListItr = chatRoomRegisteredList.iterator();
    	System.out.println("\nThe regsitered chat rooms are:");
		while(chatRoomRegisteredListItr.hasNext()) {
			ChatEntity registeredChatRoom = (ChatEntity) chatRoomRegisteredListItr.next();    
			String registeredChatRoomName = registeredChatRoom.getName();
			System.out.println(registeredChatRoomName + ": " + registeredChatRoom.getDescription());
		}
    	}
    }
	
/*  Deregister the chat room from the registry server */ 
	public void deregister(RegistryServer stub, String name, Registry registry) throws RemoteException, NotBoundException {
		
		Iterator<ChatEntity> chatRoomRegisteredListItr = chatRoomRegisteredList.iterator();
		boolean confirmDeRegistration = false;
    	while(chatRoomRegisteredListItr.hasNext()) {
    		ChatEntity registeredChatRoom = (ChatEntity) chatRoomRegisteredListItr.next();  
			String registeredChatRoomName = registeredChatRoom.getName();
			if(registeredChatRoomName.equalsIgnoreCase(name)) {
				confirmDeRegistration = stub.deregister(registeredChatRoom);
				if(confirmDeRegistration) {
		            registry.unbind(registeredChatRoomName);
		            chatRoomRegisteredListItr.remove();
		            System.out.println("Successfully deregistered chat room " + registeredChatRoomName);
				}
			}  
		}
    	
		if(!confirmDeRegistration)
			System.out.println("You cannot deregister the chat room " + name);
    }
	
/*  Menu provided to the Chatroom Provider to create and register, deregister chatrooms */ 
    public void chatRoomProviderMenu() {
    	System.out.println("\nOptions : \n 1. Create & Register a New Chat Room." +
    								   "\n 2. View Registered Chat Rooms." +
				 					   "\n 3. Deregister a Chat Room.");
    	System.out.println("\nPlease enter your choice");
    }
    
    public static void main(String args[]) throws Exception {
        String host = args[0];
        try{
            Registry registry = LocateRegistry.getRegistry(host);
            RegistryServer stub = (RegistryServer) registry.lookup("RegistryServer");
            
            BufferedReader stdinp = new BufferedReader(new InputStreamReader(System.in));
            ChatRoomProvider chatRoomProvider = new ChatRoomProvider(); 
            while (true) {   
            	chatRoomProvider.chatRoomProviderMenu();
            	try {
            		String echoline = stdinp.readLine();
	        		StringTokenizer st = null;
	        		if(echoline.equals(null) || echoline.equals("")) {
	        			
	        		} else {
	        			st = new StringTokenizer(echoline);       		
	        			String tag = st.nextToken(); 
	        			if(tag.equalsIgnoreCase("1")) {
	        				String name = "";
		        			while(name.equals("")) {
		        				System.out.println("Enter Chat Room Name: ");
		        				 name = stdinp.readLine();
		        			} 
		        			
		        			String location = "";
		        			while(location.equals("")) {
		        				System.out.println("Enter Chat Room Location: ");
		        				location = stdinp.readLine();
		        			} 
		        			String description = "";
		        			while(description.equals("")) {
		        				System.out.println("Enter Chat Room Description: ");
		        				description = stdinp.readLine();
		        			} 
		        			
		        			chatRoomProvider.register(stub, name, location, description);
	        			} else if(tag.equalsIgnoreCase("2")) {
	        				chatRoomProvider.viewRegisteredChatRooms();
	        			} else if(tag.equalsIgnoreCase("3")) {
	        				String name = "";
		        			while(name.equals("")) {
		        				System.out.println("Enter Room Name: ");
		        				 name = stdinp.readLine();
		        			} 
		        			
		        			chatRoomProvider.deregister(stub, name, registry);
	        				
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
