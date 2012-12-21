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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class Logger {

	public static final int LEVEL_DEBUG = 3;

	public static final int LEVEL_INFO = 4;

	public static final int LEVEL_WARN = 5;

	public static final int LEVEL_ERROR = 6;

	static PrintStream logStream = null;
	static String logFileName = "log.txt";
	static int logLevel = LEVEL_DEBUG;
	static boolean enabled = true;

	public static void open(String logfilename, int level) {
		if (!enabled) return;
		logFileName = logfilename;
		if (logStream == null) {

			try {				
				if (getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					logStream = new PrintStream(new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + logFileName, false));
				} else {
					logStream = new PrintStream(new FileOutputStream("/" + logFileName, false));
				}
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
		if (!enabled) return;
		if (logStream == null) return;
		logStream.println("========== Logger End   ==========");
		logStream.close();
		logStream = null;
	}
	static void checkLogFile()
	{
		if (enabled && logStream == null)
			open("log.txt", LEVEL_DEBUG);
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
		if (!enabled && logLevel > LEVEL_DEBUG) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.d(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("DEBUG", msg));
		logStream.flush();
	}
	
	public static void info(String msg) {
		if (!enabled && logLevel > LEVEL_INFO) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.i(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("INFO", msg));
		logStream.flush();
	}

	public static void warn(String msg) {
		if (!enabled && logLevel > LEVEL_WARN) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.w(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("WARN", msg));
		logStream.flush();
	}

	public static void error(String msg) {
		if (!enabled && logLevel > LEVEL_ERROR) return;
		if (msg == null) msg = "";
		Throwable t = new Throwable(); 
		StackTraceElement trace = t.getStackTrace()[1];
		android.util.Log.e(getSourceInfo(trace), msg);
		checkLogFile();
		logStream.println(getLog("ERROR", msg));
		logStream.flush();
	}

	public static void check(boolean condition, String msg){
		if (!condition)	{
			if (!enabled && logLevel > LEVEL_ERROR) return;
			if (msg == null) msg = "";
			Throwable t = new Throwable(); 
			StackTraceElement trace = t.getStackTrace()[1];
			android.util.Log.e(getSourceInfo(trace), msg);
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
