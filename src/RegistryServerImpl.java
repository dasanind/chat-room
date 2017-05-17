import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.registry.*;

public class RegistryServerImpl implements RegistryServer{

	List<ChatEntity> chatEntityList;
	int count = 0;
	
/*  Constructor for the RegistryServerImpl class */ 
	public RegistryServerImpl() throws RemoteException {
		chatEntityList = new ArrayList<ChatEntity>();
    }
	
/*  Registers a chat entity i.e. a chat room or a chat client */	
	@Override
	public synchronized ChatEntity register(ChatEntity chatEntity) throws RemoteException {
		if(chatEntityList.isEmpty()) { 
			chatEntity.setStatus("Registered");
			chatEntityList.add(chatEntity);
		} else {
			Iterator<ChatEntity> chatEntityListItr = chatEntityList.iterator();
			boolean contains = false;
			while(chatEntityListItr.hasNext()) {
				ChatEntity cEntity = (ChatEntity) chatEntityListItr.next();  
				String name = cEntity.getName();
				if(name.equalsIgnoreCase(chatEntity.getName())){
					contains = true;
				}
			}
			
			if(!contains) {
				chatEntity.setStatus("Registered");
				chatEntityList.add(chatEntity);
			} else {
				String nameReceived = chatEntity.getName();
				count = count + 1;
				chatEntity.setName(count+"_"+nameReceived);
				chatEntity.setStatus("Registered");
				chatEntityList.add(chatEntity);
			}
		}
		return chatEntity;
	}
	
/*  Deregisters a specified chat entity i.e. a chat room or a chat client */	
	@Override
	public synchronized boolean deregister(ChatEntity chatEntity) throws RemoteException {
		boolean confirmDeRegistration = false;
		Iterator<ChatEntity> chatEntityListItr = chatEntityList.iterator();
		while(chatEntityListItr.hasNext()) {
			ChatEntity cEntity = (ChatEntity) chatEntityListItr.next();  
			String name = cEntity.getName();
			if(name.equalsIgnoreCase(chatEntity.getName())){
				chatEntityListItr.remove();
				confirmDeRegistration = true;
			}
		}
		return confirmDeRegistration;
	}
	
/*  Provides a list of registered chat rooms */	
	@Override
	public synchronized ArrayList<ChatEntity> getListOfChatRooms() throws RemoteException {
		List<ChatEntity> chatRoomList = new ArrayList<ChatEntity>();
		Iterator<ChatEntity> chatEntityListItr = chatEntityList.iterator();
		while(chatEntityListItr.hasNext()) {
			ChatEntity cEntity = (ChatEntity) chatEntityListItr.next();  
			String type = cEntity.getType();
			if(type.equalsIgnoreCase("chatroom")){
				chatRoomList.add(cEntity);
			}
		}
		return (ArrayList<ChatEntity>) chatRoomList;
	}
	
/*  Provides the information of the specified entity */	
	@Override
	public synchronized ChatEntity getInfo(ChatEntity chatEntity) throws RemoteException {
		Iterator<ChatEntity> chatEntityListItr = chatEntityList.iterator();
		while(chatEntityListItr.hasNext()) {
			ChatEntity cEntity = (ChatEntity) chatEntityListItr.next();  
			String name = cEntity.getName();
			if(name.equalsIgnoreCase(chatEntity.getName())){
				return cEntity;
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
        try{
        	RegistryServerImpl obj = new RegistryServerImpl();
        	RegistryServer stub = (RegistryServer) UnicastRemoteObject.exportObject(obj, 0);
            
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("RegistryServer", stub);
        }catch(Exception e){
        	
        }
    }
}
