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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileHelper {
	public static String readTextFile(File source, String encoding) throws IOException{
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(source);
		InputStreamReader isr = new InputStreamReader(fis, encoding == null ? "UTF-8" : encoding);
		char[] cs = new char[10240];
		int len;
		while((len = isr.read(cs)) > 0){
			sb.append(cs, 0, len);
		}
		isr.close();
		fis.close();
		return sb.toString();
	}
	public static void copyFile(File source, File target) throws IOException{
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(target);
		byte[] bs = new byte[10240];
		int len;
		while((len = fis.read(bs)) > 0){
			fos.write(bs, 0, len);
		}
		fis.close();
		fos.close();
	}
}
