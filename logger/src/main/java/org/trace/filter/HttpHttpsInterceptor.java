package org.trace.filter;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class HttpHttpsInterceptor {

    // The IP address and ports you want to intercept
    private static final String IP_ADDRESS = "127.0.0.1"; // Change to your desired IP address
    private static final int HTTP_PORT = 8080;
    private static final int HTTPS_PORT = 8443;

    public static void main(String[] args) {
        // Start HTTP Interceptor
        new Thread(() -> startHttpInterceptor(IP_ADDRESS, HTTP_PORT)).start();
        
        // Start HTTPS Interceptor (ensure you have valid SSL context)
        new Thread(() -> startHttpsInterceptor(IP_ADDRESS, HTTPS_PORT)).start();
    }

    private static void startHttpInterceptor(String ipAddress, int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(ipAddress, port));
            System.out.println("HTTP Interceptor started on port: " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleHttpRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleHttpRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine;
            while ((requestLine = in.readLine()) != null && !requestLine.isEmpty()) {
                System.out.println("HTTP Request: " + requestLine);
            }

            // Send a basic response
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println("Content-Length: 13");
            out.println();
            out.println("Hello, World!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startHttpsInterceptor(String ipAddress, int port) {
        try {
            // Load SSL context (this requires proper SSL certificate setup)
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, new java.security.SecureRandom());

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket();
            sslServerSocket.bind(new InetSocketAddress(ipAddress, port));

            System.out.println("HTTPS Interceptor started on port: " + port);

            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                handleHttpsRequest(sslSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleHttpsRequest(SSLSocket sslSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
             PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true)) {

            String requestLine;
            while ((requestLine = in.readLine()) != null && !requestLine.isEmpty()) {
                System.out.println("HTTPS Request: " + requestLine);
            }

            // Send a basic response
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println("Content-Length: 13");
            out.println();
            out.println("Hello, Secure World!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TrustManager to accept all certificates (for testing purposes only)
    private static class TrustAllCertificates implements X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
    }
}
