import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClient extends Remote {
	public void showMessage(String message) throws RemoteException;
}
