package com.momock.http.wget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.TargetApi;
import android.os.Build;

import com.momock.http.wget.info.DownloadInfo;
import com.momock.http.wget.info.DownloadInfo.Part;
import com.momock.http.wget.info.DownloadInfo.Part.States;
import com.momock.http.wget.info.URLInfo;
import com.momock.http.wget.info.ex.DownloadInterruptedError;
import com.momock.http.wget.info.ex.DownloadMultipartError;
import com.momock.http.wget.info.ex.DownloadRetry;
import com.momock.util.Logger;

public class DirectMultipart extends Direct {

    static public final int THREAD_COUNT = 3;
    static public final int RETRY_DELAY = 10;

    //LimitThreadPool worker = new LimitThreadPool(THREAD_COUNT);

	BlockingQueue<Runnable> worksQueue = new ArrayBlockingQueue<Runnable>(1000);
	RejectedExecutionHandler executionHandler = new RejectedExecutionHandler() {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			Logger.warn(r.toString() + " has been rejected!");
		}

	};

	ThreadPoolExecutor executor = null;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	void config(){
		executor.allowCoreThreadTimeOut(true);
	}
	
    boolean fatal = false;

    Object lock = new Object();

    /**
     * 
     * @param info
     *            download file information
     * @param target
     *            target file
     */
    public DirectMultipart(DownloadInfo info, File target) {
        super(info, target);
        executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT * 2, 30, TimeUnit.SECONDS, worksQueue, executionHandler);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
			config();
    }

    /**
     * download part.
     * 
     * if returns normally - part is fully donwloaded. other wise - it throws
     * RuntimeException or DownloadRetry or DownloadError
     * 
     * @param part
     *            downloading part
     * @param stop
     *            multithread stop command
     * @param notify
     *            progress notify call
     * 
     */
    void downloadPart(Part part, AtomicBoolean stop, Runnable notify) throws IOException {
        RandomAccessFile fos = null;
        BufferedInputStream binaryreader = null;

        try {
            long start = part.getStart() + part.getCount();
            long end = part.getEnd();

            // fully downloaded already?
            if (end - start + 1 == 0)
                return;

            HttpURLConnection conn = info.openConnection();

            File f = target;

            fos = new RandomAccessFile(f, "rw");

            conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
            fos.seek(start);

            byte[] bytes = new byte[BUF_SIZE];
            int read = 0;

            RetryWrap.check(conn);

            binaryreader = new BufferedInputStream(conn.getInputStream());

            boolean localStop = false;

            while ((read = binaryreader.read(bytes)) > 0) {
                // ensure we do not download more then part size.
                // if so cut bytes and stop download
                long partEnd = part.getLength() - part.getCount();
                if (read > partEnd) {
                    read = (int) partEnd;
                    localStop = true;
                }

                fos.write(bytes, 0, read);
                part.setCount(part.getCount() + read);
                info.calculate();
                notify.run();

                if (stop.get())
                    throw new DownloadInterruptedError("stop");
                if (Thread.interrupted())
                    throw new DownloadInterruptedError("interrupted");
                if (fatal())
                    throw new DownloadInterruptedError("fatal");

                // do not throw exception here. we normally done downloading.
                // just took a littlbe bit more
                if (localStop)
                    return;
            }

            if (part.getCount() != part.getLength())
                throw new DownloadRetry("EOF before end of part");
        } finally {
            if (binaryreader != null)
                binaryreader.close();
            if (fos != null)
                fos.close();
        }

    }

    boolean fatal() {
        synchronized (lock) {
            return fatal;
        }
    }

    void fatal(boolean b) {
        synchronized (lock) {
            fatal = b;
        }
    }

    String trimLen(String str, int len) {
        if (str.length() > len)
            return str.substring(0, len / 2) + "..." + str.substring(str.length() - len / 2, str.length());
        else
            return str;
    }

    void downloadWorker(final Part p, final AtomicBoolean stop, final Runnable notify) throws InterruptedException {
    	executor.execute(new Runnable() {
            @Override
            public void run() {
                {
                    String f = "%s - Part: %d";
                    Thread t = Thread.currentThread();
                    t.setName(String.format(f, trimLen(info.getSource().toString(), 64), p.getNumber()));
                }

                try {
                    RetryWrap.wrap(stop, new RetryWrap.Wrap() {

                        @Override
                        public void proxy() {
                            info.getProxy().set();
                        }

                        @Override
                        public void download() throws IOException {
                            p.setState(States.DOWNLOADING);
                            notify.run();

                            downloadPart(p, stop, notify);
                        }

                        @Override
                        public void retry(int delay, Throwable e) {
                            p.setDelay(delay, e);
                            notify.run();
                        }

                        @Override
                        public void moved(URL url) {
                            p.setState(States.RETRYING);
                            notify.run();
                        }

                    });
                    p.setState(States.DONE);
                    notify.run();
                } catch (DownloadInterruptedError e) {
                    p.setState(States.STOP, e);
                    notify.run();

                    fatal(true);
                } catch (RuntimeException e) {
                    p.setState(States.ERROR, e);
                    notify.run();

                    fatal(true);
                }
            }
        });

        p.setState(States.DOWNLOADING);
    }

    /**
     * return next part to download. ensure this part is not done() and not
     * currently downloading
     * 
     * @return
     */
    Part getPart() {
        for (Part p : info.getParts()) {
            if (!p.getState().equals(States.QUEUED))
                continue;
            return p;
        }

        return null;
    }

    /**
     * return true, when thread pool empty, and here is no unfinished parts to
     * download
     * 
     * @return true - done. false - not done yet
     * @throws InterruptedException
     */
    boolean done(AtomicBoolean stop) {
        if (stop.get())
            throw new DownloadInterruptedError("stop");
        if (Thread.interrupted())
            throw new DownloadInterruptedError("interupted");
        if (executor.getActiveCount() > 0)
            return false;
        if (getPart() != null)
            return false;

        return true;
    }

    @Override
    public void download(AtomicBoolean stop, Runnable notify) {
        for (Part p : info.getParts()) {
            if (p.getState().equals(States.DONE))
                continue;
            p.setState(States.QUEUED);
        }
        info.setState(URLInfo.States.DOWNLOADING);
        notify.run();

        try {
            while (!done(stop)) {
                Part p = getPart();
                if (p != null) {
                    downloadWorker(p, stop, notify);
                } else {
                    // we have no parts left.
                    //
                    // wait until task ends and check again if we have to retry.
                    // we have to check if last part back to queue in case of
                    // RETRY state
                    //worker.waitUntilNextTaskEnds();
                	Thread.sleep(1000);
                }

                // if we start to receive errors. stop add new tasks and wait
                // until all active tasks be emptied
                if (fatal()) {
                    //worker.waitUntilTermination();
                	executor.awaitTermination(60, TimeUnit.SECONDS);
                	
                    // check if all parts finished with interrupted, throw one
                    // interrupted
                    {
                        boolean interrupted = true;
                        for (Part pp : info.getParts()) {
                            Throwable e = pp.getException();
                            if (e == null)
                                continue;
                            if (e instanceof DownloadInterruptedError)
                                continue;
                            interrupted = false;
                        }
                        if (interrupted)
                            throw new DownloadInterruptedError("multipart all interrupted");
                    }

                    // ok all thread stopped. now throw the exception and let
                    // app deal with the errors
                    throw new DownloadMultipartError(info);
                }
            }

            info.setState(URLInfo.States.DONE);
            notify.run();
        } catch (InterruptedException e) {
            info.setState(URLInfo.States.STOP);
            notify.run();

            throw new DownloadInterruptedError(e);
        } catch (DownloadInterruptedError e) {
            info.setState(URLInfo.States.STOP);
            notify.run();

            throw e;
        } catch (RuntimeException e) {
            info.setState(URLInfo.States.ERROR);
            notify.run();

            throw e;
        } finally {
            //worker.shutdown();
        	executor.shutdown();
        }
    }

    /**
     * check existing file for download resume. for multipart download it may
     * check all parts CRC
     * 
     * @param info
     *            download information
     * @param targetFile
     *            target file
     * @return return true - if all ok, false - if download can not be restored.
     */
    public static boolean canResume(DownloadInfo info, File targetFile) {
        if (!targetFile.exists())
            return false;

        if (targetFile.length() < info.getCount())
            return false;

        return true;
    }
}
