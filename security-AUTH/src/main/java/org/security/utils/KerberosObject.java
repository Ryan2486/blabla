package org.security.utils;

import java.util.Set;

import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;

public class KerberosObject {
    KerberosTicket ticket;
    String sessionKey;
    KerberosPrincipal client;
    KerberosPrincipal server;
    String role;
    Set<String> groups;

    public Set<String> getGroups() {
        return groups;
    }
    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public KerberosTicket getTicket() {
        return ticket;
    }
    public void setTicket(KerberosTicket ticket) {
        this.ticket = ticket;
    }
    public String getSessionKey() {
        return sessionKey;
    }
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    public KerberosPrincipal getClient() {
        return client;
    }
    public void setClient(KerberosPrincipal client) {
        this.client = client;
    }
    public KerberosPrincipal getServer() {
        return server;
    }
    public void setServer(KerberosPrincipal server) {
        this.server = server;
    }

}