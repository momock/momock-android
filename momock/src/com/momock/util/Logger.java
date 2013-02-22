/*******************************************************************************
 * Copyright 2012 momock.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.momock.util;

import static android.os.Environment.getExternalStorageState;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.momock.event.Event;
import com.momock.event.EventArgs;
import com.momock.event.IEvent;
import com.momock.event.IEventHandler;

public class Logger {
	public static final int LEVEL_ALL = 0;
	
	public static final int LEVEL_DEBUG = 3;

	public static final int LEVEL_INFO = 4;

	public static final int LEVEL_WARN = 5;

	public static final int LEVEL_ERROR = 6;
	
	public static final int LEVEL_NONE = 7;

	static PrintStream logStream = null;
	static String logName = "app";
	static String logFileName = "log.txt";
	static int logLevel = LEVEL_DEBUG;
	static boolean enabled = true;

	public static class LogEventArgs extends EventArgs{
		String message;
		Throwable error;
		public LogEventArgs(String message, Throwable error){
			this.message = message;
			this.error = error;
		}
		public String getMessage() {
			return message;
		}
		public Throwable getError() {
			return error;
		}
	}
	static IEvent<LogEventArgs> event = new Event<LogEventArgs>();
	
	public static void addErrorLogHandler(IEventHandler<LogEventArgs> handler){
		event.addEventHandler(handler);
	}
	@TargetApi(Build.VERSION_CODES.FROYO)
	static File getExternalCacheDir(final Context context) {
		return context.getExternalCacheDir();
	}
	public static void open(Context context, final String name, int maxLogFiles, int level) {
		if (!enabled) return;
		logName = name;
		logFileName = logName + "[" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date()) + "].log";
		if (logStream == null) {
			logStream = System.out;		
			File logDir = null;
			try {								
				if (getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					logDir = Environment.getExternalStorageDirectory();
				} else if (context != null){
					logDir = context.getCacheDir();
				} 				
				if (logDir != null){
					android.util.Log.d("Logger", logDir.getAbsolutePath());
					String[] fs = logDir.list(new FilenameFilter(){

						@Override
						public boolean accept(File dir, String filename) {
							return filename.startsWith(logName + "[") && filename.endsWith("].log");
						}
						
					});
					List<String> allLogs = new ArrayList<String>();
					for(int i = 0; i < fs.length; i++)
						allLogs.add(fs[i]);
					Collections.sort(allLogs);
					for(int i = 0; i < allLogs.size() - maxLogFiles + 1; i++)
						new File(logDir, allLogs.get(i)).delete();
					logStream = new PrintStream(new FileOutputStream(new File(logDir, logFileName), false));
				}
			} catch (IOException e) {
				android.util.Log.e("Logger", "Fails to create log file!", e);
			}
		}
		logLevel = level;

		logStream.println("========== Logger Begin ==========");
		logStream.flush();
	}
	public static void open(Context context, String logfilename, int level) {
		if (!enabled) return;
		logFileName = logfilename;
		if (logStream == null) {
			logStream = System.out;		
			File logDir = null;
			try {								
				if (getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					logDir = Environment.getExternalStorageDirectory();
				} else if (context != null){
					logDir = context.getCacheDir();
				} 
				if (logDir != null){
					android.util.Log.d("Logger", logDir.getAbsolutePath());
					logStream = new PrintStream(new FileOutputStream(new File(logDir, logFileName), false));
				}
			} catch (IOException e) {
				android.util.Log.e("Logger", "Fails to create log file!", e);
			}
		}
		logLevel = level;

		logStream.println("========== Logger Begin ==========");
		logStream.flush();
	}
	public static void close()
	{
		if (!enabled) return;
		if (logStream == null) return;
		logStream.println("========== Logger End   ==========");
		logStream.close();
		logStream = null;
	}
	static void checkLogFile()
	{
		if (enabled && logStream == null)
			open(null, "log.txt", LEVEL_DEBUG);
	}
	static String getLog(String level, String msg)
	{
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[2];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		return "[" + level + "] " + sdf.format(new Date()) + " in " + trace.getFileName() + "(" + trace.getLineNumber() + ") >" + msg;
	}
	static String getSourceInfo(StackTraceElement trace)
	{
		return trace.getFileName() + "(" + trace.getLineNumber() + ")";
	}
	public static void debug(String msg) {
		if (!enabled || logLevel > LEVEL_DEBUG) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.d(logName, msg + " @ " + getSourceInfo(trace) );
		checkLogFile();
		logStream.println(getLog("DEBUG", msg));
		logStream.flush();
	}
	
	public static void info(String msg) {
		if (!enabled || logLevel > LEVEL_INFO) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.i(logName, msg + " @ " + getSourceInfo(trace) );
		checkLogFile();
		logStream.println(getLog("INFO", msg));
		logStream.flush();
	}

	public static void warn(String msg) {
		if (!enabled || logLevel > LEVEL_WARN) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.w(logName, msg + " @ " + getSourceInfo(trace) );
		checkLogFile();
		logStream.println(getLog("WARN", msg));
		logStream.flush();
	}

	public static void error(String msg) {
		if (!enabled || logLevel > LEVEL_ERROR) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.e(logName, msg + " @ " + getSourceInfo(trace) );
		checkLogFile();
		logStream.println(getLog("ERROR", msg));
		logStream.flush();
		event.fireEvent(null, new LogEventArgs(msg, null));
	}
	public static String getStackTrace(Throwable e){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		e.printStackTrace(ps);
		return new String(baos.toByteArray());
	}
	public static void error(Throwable e) {
		if (!enabled || logLevel > LEVEL_ERROR) return;
		String msg = e.getMessage() + " : " + getStackTrace(e);
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.e(logName, msg + " @ " + getSourceInfo(trace) );
		checkLogFile();
		logStream.println(getLog("ERROR", msg));
		logStream.flush();
		event.fireEvent(null, new LogEventArgs(null, e));
	}
	public static void check(boolean condition, String msg){
		if (!condition)	{
			if (!enabled || logLevel > LEVEL_ERROR) return;
			if (msg == null) msg = "";
			Throwable t = new Throwable(); 
			StackTraceElement trace = t.getStackTrace()[1];
			android.util.Log.e(logName, msg + " @ " + getSourceInfo(trace) );
			checkLogFile();
			logStream.println(getLog("ASSERT", msg));
			logStream.flush();
			
			throw new RuntimeException(msg);
		}
	}
	public static boolean isEnabled() {
		return enabled;
	}
	public static void setEnabled(boolean enabled) {
		Logger.enabled = enabled;
	}
}
