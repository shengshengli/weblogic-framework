import weblogic.jndi.internal.NamingNode;

import javax.naming.*;
import javax.naming.event.NamingListener;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;

/**
 * Title: PocServerNamingNode
 * Desc: TODO
 * Date:2020/4/3 21:22
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 *
 * @author R4v3zn
 * @version 1.0.0
 */
public class PocServerNamingNode implements NamingNode {
    @Override
    public void bind(String s, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public Context createSubcontext(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public void destroySubcontext(String s, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public NameParser getNameParser(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public String getNameInNamespace() throws RemoteException {
        return null;
    }

    @Override
    public String getNameInNamespace(String s) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public NamingEnumeration list(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public NamingEnumeration listBindings(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public Object lookup(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public Object lookupLink(String s, Hashtable hashtable) throws NamingException, RemoteException {
        return null;
    }

    @Override
    public void rebind(String s, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void rebind(Name name, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void rebind(String s, Object o, Object o1, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void rename(String s, String s1, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public void unbind(String s, Object o, Hashtable hashtable) throws NamingException, RemoteException {

    }

    @Override
    public NamingNode getParent() {
        return null;
    }

    @Override
    public Context getContext(Hashtable hashtable) {
        return null;
    }

    @Override
    public void addNamingListener(String s, int i, NamingListener namingListener, Hashtable hashtable) throws NamingException {

    }

    @Override
    public void removeNamingListener(NamingListener namingListener, Hashtable hashtable) throws NamingException {

    }

    @Override
    public List getOneLevelScopeNamingListeners() {
        return null;
    }

    @Override
    public void addOneLevelScopeNamingListener(NamingListener namingListener) {

    }
}
