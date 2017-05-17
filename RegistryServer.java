import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RegistryServer extends Remote {
    public ChatEntity register(ChatEntity chatEntity) throws RemoteException;
    public boolean deregister(ChatEntity chatEntity) throws RemoteException;
    public ArrayList<ChatEntity> getListOfChatRooms() throws RemoteException;
    public ChatEntity getInfo(ChatEntity chatEntity) throws RemoteException;
}