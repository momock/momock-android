package com.momock.http.wget.info;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import com.momock.http.wget.info.ex.DownloadError;

public class ProxyInfo {
    public Proxy proxy;
    // null if not set, Authenticator.setDefault only be called if you didn't so
    // in main app.
    public Authenticator auth;

    public ProxyInfo(Proxy p) {
        this.proxy = p;
    }

    public ProxyInfo(Proxy p, Authenticator a) {
        this.proxy = p;
        this.auth = a;
    }

    public ProxyInfo(Proxy p, final String login, final String password) {
        this.proxy = p;

        Authenticator a = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication(login, password.toCharArray()));
            }
        };

        this.auth = a;
    }

    // addr = "10.0.0.1"
    // port = 8080
    public ProxyInfo(String addr, int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(addr, port));
    }

    public ProxyInfo(String addr, int port, final String login, final String password) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(addr, port));

        Authenticator a = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication(login, password.toCharArray()));
            }
        };

        this.proxy = proxy;
        this.auth = a;
    }

    public void set() {
        if (auth == null) {
            throw new DownloadError("proxy requires auth");
        }
        Authenticator.setDefault(auth);
    }
}
