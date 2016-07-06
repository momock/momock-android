package com.momock.http.wget.info.ex;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ProxyAuth extends RuntimeException {
    private static final long serialVersionUID = 1L;

    URLConnection c;

    public ProxyAuth(URLConnection c) {
        this.c = c;
    }

    public ProxyAuth(Throwable e) {
        super(e);
    }

    public ProxyAuth(String msg) {
        super(msg);
    }

    public URL getMoved() {
        try {
            return new URL(c.getHeaderField("Location"));
        } catch (MalformedURLException e) {
            throw new DownloadError(e);
        }
    }

}
