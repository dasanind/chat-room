import java.rmi.*;

public interface ChatRoom extends Remote {
	  public boolean join(ChatEntity chatEntity, ChatClient c) throws RemoteException;
	  public void talk(String roomName, String message, ChatEntity chatEntity, ChatClient c) throws RemoteException;
	  public boolean leave(ChatEntity chatEntity, ChatClient c) throws RemoteException;
}
