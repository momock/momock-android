/*******************************************************************************
 * Copyright 2013 momock.com
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
package com.momock.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.momock.util.Logger;

public class AsyncTaskService implements IAsyncTaskService {
	public AsyncTaskService() {
	}

	BlockingQueue<Runnable> worksQueue = new ArrayBlockingQueue<Runnable>(2);
	RejectedExecutionHandler executionHandler = new RejectedExecutionHandler() {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			Logger.warn(r.toString() + " has been rejected!");
		}

	};

	ThreadPoolExecutor executor = null;

	@Override
	public Class<?>[] getDependencyServices() {
		return null;
	}

	@Override
	public void start() {
		executor = new ThreadPoolExecutor(3, 100, 30, TimeUnit.SECONDS, worksQueue, executionHandler);
		executor.allowCoreThreadTimeOut(true);
	}

	@Override
	public void stop() {
		executor.shutdown();
		try {
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
			}
		} catch (InterruptedException e) {
			Logger.error(e);
			executor.shutdownNow();
		}
		worksQueue.clear();
		executor = null;
	}

	@Override
	public void run(Runnable task) {
		if (executor == null){
			Logger.warn("AsyncTaskService is not ready to accept task : " + task.toString());
			return;
		}
		executor.execute(task);
	}

}
