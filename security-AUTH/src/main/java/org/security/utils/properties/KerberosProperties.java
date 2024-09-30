package org.security.utils.properties;

import java.io.IOException;

import org.security.utils.FileProcessor;

import java.io.File;

public class KerberosProperties {
    String server;
    int clockskew;
    int udpPreferenceLimit;
    String DnsLookUpKdc;
    String DnsLookUpRealm;
    String serverDomain;

    public String getServer() {
        return server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public int getClockskew() {
        return clockskew;
    }
    public void setClockskew(int cockskew) {
        this.clockskew = cockskew;
    }
    public int getUdpPreferenceLimit() {
        return udpPreferenceLimit;
    }
    public void setUdpPreferenceLimit(int udpPreferenceLimit) {
        this.udpPreferenceLimit = udpPreferenceLimit;
    }
    public String getDnsLookUpKdc() {
        return DnsLookUpKdc;
    }
    public void setDnsLookUpKdc(String dnsLookUpKdc) {
        DnsLookUpKdc = dnsLookUpKdc;
    }
    public String getDnsLookUpRealm() {
        return DnsLookUpRealm;
    }
    public void setDnsLookUpRealm(String dnsLookUpRealm) {
        DnsLookUpRealm = dnsLookUpRealm;
    }
    public String getServerDomain() {
        return serverDomain;
    }
    public void setServerDomain(String serverDomain) {
        this.serverDomain = serverDomain;
    }

    public String replaceString() throws IOException{
        String inputFilePath = "templates" + File.separator + "krb5.template";
        String stringReplaced = FileProcessor.readFile(inputFilePath);
        stringReplaced = stringReplaced
                .replaceAll("#SERVER#", this.getServer().toUpperCase())
                .replaceAll("#server#", this.getServer().toLowerCase())
                .replaceAll("#SERVER_DOMAIN#", this.getServerDomain())
                .replaceAll("#CLOCKSKEW#", ""+this.getClockskew())
                .replaceAll("#UDP_PREFERENCE_LIMIT#", ""+this.getUdpPreferenceLimit())
                .replaceAll("#DNS_LOOKUP_KDC#", ""+this.getDnsLookUpKdc())
                .replaceAll("#DNS_LOOKUP_REALM#", ""+this.getDnsLookUpRealm());
        return stringReplaced;
    }
}
