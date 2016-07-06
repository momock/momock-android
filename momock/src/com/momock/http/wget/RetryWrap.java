package com.momock.http.wget;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.momock.http.wget.info.ex.DownloadError;
import com.momock.http.wget.info.ex.DownloadIOCodeError;
import com.momock.http.wget.info.ex.DownloadIOError;
import com.momock.http.wget.info.ex.DownloadInterruptedError;
import com.momock.http.wget.info.ex.DownloadMoved;
import com.momock.http.wget.info.ex.DownloadRetry;
import com.momock.http.wget.info.ex.ProxyAuth;

public class RetryWrap {

    public static int RETRY_DELAY = 10;
    public static int RETRY_SLEEP = 1000;

    public interface WrapReturn<T> {
        public void proxy();

        public void retry(int delay, Throwable e);

        public void moved(URL url);

        public T download() throws IOException;
    }

    public interface Wrap {
        public void proxy();

        public void retry(int delay, Throwable e);

        public void moved(URL url);

        public void download() throws IOException;
    }

    static <T> void moved(AtomicBoolean stop, WrapReturn<T> r, DownloadMoved e) {
        if (stop.get())
            throw new DownloadInterruptedError("stop");

        if (Thread.currentThread().isInterrupted())
            throw new DownloadInterruptedError("interrrupted");

        r.moved(e.getMoved());
    }

    static <T> void retry(AtomicBoolean stop, WrapReturn<T> r, RuntimeException e) {
        for (int i = RETRY_DELAY; i >= 0; i--) {
            r.retry(i, e);

            if (stop.get())
                throw new DownloadInterruptedError("stop");

            if (Thread.currentThread().isInterrupted())
                throw new DownloadInterruptedError("interrrupted");

            try {
                Thread.sleep(RETRY_SLEEP);
            } catch (InterruptedException e1) {
                throw new DownloadInterruptedError(e1);
            }
        }
    }

    public static <T> T run(AtomicBoolean stop, WrapReturn<T> r) {
        while (true) {
            if (stop.get())
                throw new DownloadInterruptedError("stop");
            if (Thread.currentThread().isInterrupted())
                throw new DownloadInterruptedError("interrupted");

            try {
                try {
                    try {
                        T t = r.download();
                        return t;
                    } catch (ProxyAuth e) {
                        // retry with proxy set
                        r.proxy();
                        // if we will get another proxy exception. do not retry
                        // but stop download
                        T t = r.download();
                        return t;
                    }
                } catch (ProxyAuth e) {
                    throw new DownloadError(e);
                } catch (SocketException e) {
                    // enumerate all retry exceptions
                    throw new DownloadRetry(e);
                } catch (ProtocolException e) {
                    // enumerate all retry exceptions
                    throw new DownloadRetry(e);
                } catch (HttpRetryException e) {
                    // enumerate all retry exceptions
                    throw new DownloadRetry(e);
                } catch (InterruptedIOException e) {
                    // enumerate all retry exceptions
                    throw new DownloadRetry(e);
                } catch (UnknownHostException e) {
                    // enumerate all retry exceptions
                    throw new DownloadRetry(e);
                } catch (FileNotFoundException e) {
                    throw new DownloadError(e);
                } catch (RuntimeException e) {
                    throw e;
                } catch (IOException e) {
                    throw new DownloadIOError(e);
                }
            } catch (DownloadMoved e) {
                moved(stop, r, e);
            } catch (DownloadRetry e) {
                retry(stop, r, e);
            }
        }
    }

    public static <T> T wrap(AtomicBoolean stop, WrapReturn<T> r) {
        return RetryWrap.run(stop, r);
    }

    public static void wrap(AtomicBoolean stop, final Wrap r) {
        WrapReturn<Object> rr = new WrapReturn<Object>() {

            @Override
            public Object download() throws IOException {
                r.download();
                return null;
            }

            @Override
            public void retry(int delay, Throwable e) {
                r.retry(delay, e);
            }

            @Override
            public void moved(URL url) {
                r.moved(url);
            }

            @Override
            public void proxy() {
                r.proxy();
            }
        };

        RetryWrap.run(stop, rr);
    }

    public static void check(HttpURLConnection c) throws IOException {
        int code = c.getResponseCode();
        switch (code) {
        case HttpURLConnection.HTTP_OK:
        case HttpURLConnection.HTTP_PARTIAL:
            return;
        case HttpURLConnection.HTTP_MOVED_TEMP:
        case HttpURLConnection.HTTP_MOVED_PERM:
            // rfc2616: the user agent MUST NOT automatically redirect the
            // request unless it can be confirmed by the user
            throw new DownloadMoved(c);
        case HttpURLConnection.HTTP_PROXY_AUTH:
            throw new ProxyAuth(c);
        case HttpURLConnection.HTTP_FORBIDDEN:
            throw new DownloadIOCodeError(HttpURLConnection.HTTP_FORBIDDEN);
        case 416:
            // HTTP Error 416 - Requested Range Not Satisfiable
            throw new DownloadIOCodeError(416);
        }
    }
}
