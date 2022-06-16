/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * Helper class to run jobs in lock step in several threads.
 * <p>
 * You should override the {@link #job} method and make it execute code where blocks are wrapped in:
 *
 * <pre>
 * if (thread(1)) {
 *     // code to execute only in thread 1
 * }
 * </pre>
 *
 * The parameter to {@link #thread} should be 1, 2, 3... depending on the thread you want this block to be executed in.
 * <p>
 * After you created the job instances, run the whole process by calling:
 *
 * <pre>
 * LockStepJob.run(job1, job2, job3...);
 * </pre>
 *
 * @since 5.7
 */
public abstract class LockStepJob implements Runnable {

    protected int n;

    protected CyclicBarrier barrier;

    protected Throwable throwable;

    /** Run the thread n (1, 2...). */
    public void initialize(int n, CyclicBarrier barrier) {
        this.n = n;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            job();
        } catch (Throwable t) {
            // System.err.println("Exception in thread " +
            // Thread.currentThread());
            // t.printStackTrace();
            throwable = t;
        }
    }

    /**
     * Method to call around each part to be executed by a single thread.
     *
     * @param which which thread is concerned
     * @return {@code true} if the code should be executed
     */
    public boolean thread(int which) throws Exception {
        // sync all threads
        barrier.await(30, TimeUnit.SECONDS); // throws on timeout
        // execute this step if in the right thread
        return which == n;
    }

    /**
     * Override this to define the actual job to execute in multiple threads.
     */
    public abstract void job() throws Exception;

    public static void run(LockStepJob... jobs) throws Exception {
        int n = jobs.length;
        CyclicBarrier barrier = new CyclicBarrier(n);
        for (int i = 0; i < n; i++) {
            jobs[i].initialize(i + 1, barrier);
        }
        Thread[] threads = new Thread[n];
        try {
            for (int i = 0; i < n; i++) {
                threads[i] = new Thread(jobs[i], "test-" + (i + 1));
                threads[i].start();
            }
            for (int i = 0; i < n; i++) {
                threads[i].join();
                threads[i] = null;
            }
            Exception exception = new RuntimeException("failed");
            for (int i = 0; i < n; i++) {
                Throwable t = jobs[i].throwable;
                if (t != null) {
                    exception.addSuppressed(t);
                }
            }
            if (exception.getSuppressed().length > 0) {
                throw exception;
            }
        } finally {
            // error condition recovery
            for (int i = 0; i < n; i++) {
                if (threads[i] != null) {
                    threads[i].interrupt();
                }
            }
        }
    }

}
