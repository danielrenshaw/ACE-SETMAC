/*
 * Copyright 2012 Daniel Renshaw &lt;d.renshaw@sms.ed.ac.uk&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ed.inf.ace.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class ThreadPool extends ThreadPoolExecutor {

  public ThreadPool(int maximumPoolSize) {
    super(maximumPoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(), new LowPriorityThreadFactory());
  }

  public void stop() throws Exception {
    this.setCorePoolSize(0);
    this.shutdown();
    this.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
  }
}