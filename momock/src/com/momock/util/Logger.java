package com.momock.util;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class Logger {

	public static final int LEVEL_DEBUG = 3;

	public static final int LEVEL_INFO = 4;

	public static final int LEVEL_WARN = 5;

	public static final int LEVEL_ERROR = 6;

	static PrintStream logStream = null;
	static String logFileName = "log.txt";
	static int logLevel = LEVEL_DEBUG;

	public static void open(String logfilename, int level) {
		logFileName = logfilename;
		if (logStream == null) {

			try {
				logStream = new PrintStream(new FileOutputStream(
						Environment.getExternalStorageDirectory() + "/"
								+ logFileName, false));
			} catch (IOException e) {
				logStream = System.out;
				android.util.Log.e("Logger", "Fails to create log file!", e);
			}
		}
		logLevel = level;

		logStream.println("========== Logger Begin ==========");
		logStream.flush();
	}
	public static void close()
	{
		if (logStream == null) return;
		logStream.println("========== Logger End   ==========");
		logStream.close();
		logStream = null;
	}
	static void checkLogFile()
	{
		if (logStream == null)
			open("log.txt", LEVEL_DEBUG);
	}
	static String getLog(String level, String msg)
	{
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[2];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return "[" + level + "] " + sdf.format(new Date()) + " in " + trace.getFileName() + "(" + trace.getLineNumber() + ") >" + msg;
	}
	static String getSourceInfo(StackTraceElement trace)
	{
		return trace.getFileName() + "(" + trace.getLineNumber() + ")";
	}
	public static void debug(String msg) {
		if (logLevel > LEVEL_DEBUG) return;

		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.d(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("DEBUG", msg));
		logStream.flush();
	}
	
	public static void info(String msg) {
		if (logLevel > LEVEL_INFO) return;

		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.i(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("INFO", msg));
		logStream.flush();
	}

	public static void warn(String msg) {
		if (logLevel > LEVEL_WARN) return;

		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.w(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("WARN", msg));
		logStream.flush();
	}

	public static void error(String msg) {
		if (logLevel > LEVEL_ERROR) return;

		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.e(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("ERROR", msg));
		logStream.flush();
	}

}
