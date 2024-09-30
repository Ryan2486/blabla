package org.security.utils.properties;

import java.io.File;
import java.io.IOException;

import org.security.utils.FileProcessor;

public class LdapProperties {
    String ldapUrl;
    int port;
    String domainName;
    String authType;
    String suffix;
    int min;
    int max;
    int timeoutPool;
    int timeoutConnect;
    int timeoutRead;
    String useSSL;
    String keystorePath;
    String keyStorePwd;

    public String getLdapUrl() {
        return this.ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getAuthType() {
        return this.authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getSuffix() {
        if (this.suffix.contains("@"))
            return this.suffix;
        else
            return "@" + this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getTimeoutPool() {
        return this.timeoutPool;
    }

    public void setTimeoutPool(int timeoutPool) {
        this.timeoutPool = timeoutPool;
    }

    public int getTimeoutConnect() {
        return this.timeoutConnect;
    }

    public void setTimeoutConnect(int timeoutConnect) {
        this.timeoutConnect = timeoutConnect;
    }

    public int getTimeoutRead() {
        return this.timeoutRead;
    }

    public void setTimeoutRead(int timeoutRead) {
        this.timeoutRead = timeoutRead;
    }

    public String getUseSSL() {
        return this.useSSL;
    }

    public void setUseSSL(String useSSL) {
        this.useSSL = useSSL;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setKeyStorePwd(String keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }

    public String processDomainName() {
        String processedDomainName = "";
        // Correctly split the domain name by periods
        String[] splitName = this.getDomainName().split("\\.");
        // Build the processed domain name with "dc=" for each part
        for (String string : splitName) {
            processedDomainName += "dc=" + string + ",";
        }
        // Remove the trailing comma (since the last "dc=" is followed by a comma)
        return processedDomainName.substring(0, processedDomainName.length() - 1);
    }

    public String replaceString() throws IOException {
        String inputFilePath = "templates" + File.separator + "ldap.template";
        String stringReplaced;

        try {
            // Read the template file
            stringReplaced = FileProcessor.readFile(inputFilePath);
            // Perform the string replacements
            stringReplaced = stringReplaced
                    .replace("#LDAPURL#", this.getLdapUrl())
                    .replace("#PORT#", String.valueOf(this.getPort()))
                    .replace("#DOMAIN_NAME#", this.processDomainName())
                    .replace("#AUTH_TYPE#", this.getAuthType())
                    .replace("#SUFFIX#", this.getSuffix())
                    .replace("#MIN#", String.valueOf(this.getMin()))
                    .replace("#MAX#", String.valueOf(this.getMax()))
                    .replace("#TIMEOUT_POOL#", String.valueOf(this.getTimeoutPool()))
                    .replace("#TIMEOUT_CONNECT#", String.valueOf(this.getTimeoutConnect()))
                    .replace("#TIMEOUT_READ#", String.valueOf(this.getTimeoutRead()))
                    .replace("#SSL#", String.valueOf(this.getUseSSL()))
                    .replace("#KEYSTORE_PATH#", this.getKeystorePath())
                    .replace("#KEYSTORE_PWD#", this.getKeyStorePwd());
        } catch (Exception e) {
            // Handle and log any exception that occurs
            System.err.println("Error during string replacement: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to process the template due to an exception.", e);
        }
        return stringReplaced;
    }
}
