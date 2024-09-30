package org.security.service;

import org.security.handler.KerberosCallbackHandler;
import org.security.utils.ADGroupRetrieval;
import org.security.utils.KerberosObject;

import org.springframework.stereotype.Service;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.kerberos.KerberosTicket;

import java.util.Set;

@Service
public class ADAuth {

    public KerberosTicket authenticate(String username, String password) throws LoginException {
        // Set up the JAAS LoginContext
        LoginContext loginContext = new LoginContext("com.sun.security.jgss.krb5.initiate", new KerberosCallbackHandler(username, password));
        loginContext.login();

        // Retrieve the authenticated Subject
        Subject subject = loginContext.getSubject();

        // Extract the Kerberos ticket from the Subject
        Set<KerberosTicket> kerberosTickets = subject.getPrivateCredentials(KerberosTicket.class);
        return kerberosTickets.stream().findFirst().orElse(null);
    }

    
    public KerberosObject authenticateDetails(String username, String password) throws Exception {
        // Set up the JAAS LoginContext
        LoginContext loginContext = new LoginContext("com.sun.security.jgss.krb5.initiate", new KerberosCallbackHandler(username, password));
        loginContext.login();

        // Retrieve the authenticated Subject
        Subject subject = loginContext.getSubject();

        // Extract the Kerberos ticket from the Subject
        Set<KerberosTicket> kerberosTickets = subject.getPrivateCredentials(KerberosTicket.class);
        
        // Attempt to retrieve and print session keys
        KerberosObject kerberosObjectReturned = null;
        if (kerberosTickets != null && !kerberosTickets.isEmpty()) {
            kerberosObjectReturned = new KerberosObject();
            for (KerberosTicket ticket : kerberosTickets) {
                // Extract and print the session key
                byte[] sessionKey = ticket.getSessionKey().getEncoded();
                kerberosObjectReturned.setSessionKey(bytesToHex(sessionKey));
                kerberosObjectReturned.setTicket(kerberosTickets.stream().findFirst().orElse(null));
                // Additional information
                kerberosObjectReturned.setClient(ticket.getClient());
                kerberosObjectReturned.setServer(ticket.getServer());
            }
            // Retrieve and set groups
            ADGroupRetrieval groupRetrieval = new ADGroupRetrieval();
            Set<String> groups = groupRetrieval.getGroupsFromAD(username, password);
            kerberosObjectReturned.setGroups(groups);
        } else {
            throw new LoginException("No Kerberos tickets found.");
        }
        
        return kerberosObjectReturned;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}

