import java.util.*;
import java.io.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;


public class SmackExample implements PacketListener{

    XMPPConnection connection;
    //Enter your apikey
    final String apikey = "";
    public void login() throws XMPPException
    {
        ConnectionConfiguration config = new ConnectionConfiguration("guest.collecta.com");
        connection = new XMPPConnection(config);

        connection.connect();
        connection.loginAnonymously();
        PacketFilter myFilter = new PacketFilter() {
                public boolean accept(Packet packet) {
                    return true;
                }
            };
        connection.addPacketListener(this, myFilter);
    }

    public void disconnect()
    {
        connection.disconnect();
    }

    public void processPacket(Packet p)
    {
        //look for event elements and print their xml out
        PacketExtension pe = p.getExtension("event", "http://jabber.org/protocol/pubsub#event");
        if(pe != null)
        {
            System.out.println("pe: "+pe.toXML());
        }

    }
    public void subscribeToSearch(String searchTerm)
    {
        //Build the subscribe request for the current user.
        final String term =searchTerm;
        final String jid = connection.getUser();

        IQ iqPacket = null;
        iqPacket = new IQ() {
                public String getChildElementXML() {
                    return "<pubsub xmlns='http://jabber.org/protocol/pubsub'>" +
                        "<subscribe jid='"+jid+"' node='search'/>" +
                        "<options>" + 
                        "<x xmlns='jabber:x:data' type='submit'>" +
                        "<field var='FORM_TYPE' type='hidden'>" +
                        "<value>http://jabber.org/protocol/pubsub#subscribe_options</value>" +
                        "</field>" +
                        "<field var='x-collecta#apikey'>"+
                        "<value>"+apikey+"</value>" +
                        "</field>"+
                        "<field var='x-collecta#query'>"+
                        "<value>"+term+"</value>"+
                        "</field>" +
                        "</x></options>"+
                        "</pubsub>";
                }
            };
        iqPacket.setType(IQ.Type.SET);
        iqPacket.setFrom(connection.getUser());
        iqPacket.setTo("search.collecta.com");
        connection.sendPacket(iqPacket);
    }

    public static void main(String args[]) throws XMPPException, IOException, InterruptedException
    {
        System.out.println("starting");
        // declare variables
        SmackExample c = new SmackExample();

        // turn on the enhanced debugger
        XMPPConnection.DEBUG_ENABLED = false;

        // We can login anonymously to the guest.collecta.com server to prefrom a search
        c.login();
        System.out.println("logging in");
        //Subscribe to a Search, multiple searches can be subscribed to.
        System.out.println("subscribing to search");
        c.subscribeToSearch("happy");


        //Dont return from main yet, we are waiting for results
        while(true){
            Thread.sleep(1000);
        }

    }

}