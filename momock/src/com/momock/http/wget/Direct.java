package com.momock.http.wget;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import com.momock.http.wget.info.DownloadInfo;

public abstract class Direct {

    File target = null;

    DownloadInfo info;

    /**
     * size of read buffer
     */
    static public final int BUF_SIZE = 4 * 1024;

    /**
     * 
     * @param info
     *            download file information
     * @param target
     *            target file
     */
    public Direct(DownloadInfo info, File target) {
        this.target = target;
        this.info = info;
    }

    /**
     * 
     * @param stop
     *            multithread stop command
     * @param notify
     *            progress notify call
     */
    abstract public void download(AtomicBoolean stop, Runnable notify);

}
