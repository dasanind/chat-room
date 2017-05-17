import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ChatRoomServerThread extends Thread implements ChatRoom {
	
	String chatRoomName;
	List<ChatEntity> chatClientNameList = new ArrayList<ChatEntity>();
	List<ChatClient> chatClientList;
	
/*  Constructor for the ChatRoomServerThread class */ 
	public ChatRoomServerThread() throws RemoteException {
		chatClientList = new ArrayList<ChatClient>();
    }
	
/*  Constructor for the ChatRoomServerThread class */ 
	public ChatRoomServerThread(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}
	
	synchronized public void run() {
		try {
			ChatRoomServerThread obj = new ChatRoomServerThread();
			ChatRoom chatroomstub = (ChatRoom) UnicastRemoteObject.exportObject(obj, 0);
			 
			Registry chatroomregistry = LocateRegistry.getRegistry();
			chatroomregistry.bind(chatRoomName, chatroomstub);
		} catch (RemoteException e) {
			 e.printStackTrace();
		 } catch (AlreadyBoundException e) {
			 e.printStackTrace();
		 }
	}
	 
/*  Allows a new chat client to join the chat room */ 
	@Override
	public synchronized boolean join(ChatEntity chatEntity, ChatClient c) throws RemoteException {
		boolean confirmJoin = false;
		if(chatClientList.isEmpty()) {
			chatClientNameList.add(chatEntity);
			chatClientList.add(c);
			confirmJoin = true;
		} else {
			Iterator<ChatEntity> chatClientListItr = chatClientNameList.iterator();
			boolean contains = false;
			while(chatClientListItr.hasNext()) {
				ChatEntity cEntity = (ChatEntity) chatClientListItr.next();  
				String name = cEntity.getName();
				if(name.equalsIgnoreCase(chatEntity.getName())){
					contains = true;
				}
			}
			
			if(!contains) {
				chatClientNameList.add(chatEntity);
				chatClientList.add(c);
				confirmJoin = true;
			} 
		}
		return confirmJoin;
	}

/*  Allows a joined chat client to send message to all of the joined chat clients */ 
	@Override
	public void talk(String roomName, String message, ChatEntity chatEntity, ChatClient c) 
			throws RemoteException {
		String clientName = chatEntity.getName();
		List<ChatClient> copyClientList = null; 
        synchronized(this) {
        	copyClientList = new ArrayList<ChatClient>(chatClientList);
        }
        for(ChatClient client : copyClientList) {
        	if(!(c.equals(client))) {
        		client.showMessage(message);
        	}
        }
	}

/*  Allows a joined chat client to leave the chat room */ 
	@Override
	public boolean leave(ChatEntity chatEntity, ChatClient c)
			throws RemoteException {
		boolean confirmLeave = false;
		boolean confirmEntityRemoval = false;
		boolean confirmClientRemoval = false;
		Iterator<ChatEntity> chatClientNameListItr = chatClientNameList.iterator();
		while(chatClientNameListItr.hasNext()) {
			ChatEntity cEntity = (ChatEntity) chatClientNameListItr.next();  
			String name = cEntity.getName();
			if(name.equalsIgnoreCase(chatEntity.getName())){
				chatClientNameListItr.remove();
				confirmEntityRemoval = true;
			}
		}
		Iterator<ChatClient> chatClientListItr = chatClientList.iterator();
		while(chatClientListItr.hasNext()) {
			ChatClient cClient = (ChatClient) chatClientListItr.next();  
			if(c.equals(cClient)){
				chatClientListItr.remove();
				confirmClientRemoval = true;
			}
		}
		confirmLeave = confirmEntityRemoval && confirmClientRemoval;
		return confirmLeave;
	}
}
