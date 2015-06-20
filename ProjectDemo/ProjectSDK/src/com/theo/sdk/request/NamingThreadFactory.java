/*
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.theo.sdk.request;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread工厂
 * @author Theo
 */
public class NamingThreadFactory implements ThreadFactory {


    /** 真正构造线程的Factory */
    private final ThreadFactory mBackingFactory;
    /** 线程名前缀 */
    private final String mPrefix;
    /** 线程计数器 */
    private final AtomicInteger mCount = new AtomicInteger(0);

    /**
     * Creates a new factory that delegates to the default thread factory for
     * thread creation, then uses {@code format} to construct a name for the new
     * thread.
     * 
     * @param prefix
     *            线程名前缀
     */
    public NamingThreadFactory(String prefix) {
        this(prefix, Executors.defaultThreadFactory());
    }

    /**
     * Creates a new factory that delegates to {@code backingFactory} for thread
     * creation, then uses {@code format} to construct a name for the new
     * thread.
     * 
     * @param prefix
     *            线程名前缀
     * @param backingFactory
     *            the factory that will actually create the threads
     */
    public NamingThreadFactory(String prefix, ThreadFactory backingFactory) {
        mPrefix = prefix;
        mBackingFactory = backingFactory;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        Thread t = mBackingFactory.newThread(r);
        t.setName(makeName(mCount.getAndIncrement()));
        return t;
    }

    /**
     * 
     * 构造线程名称方法
     * @param ordinal 序号
     * @return 线程名
     */
    private String makeName(int ordinal) {
        return String.format("%s-%d", mPrefix, ordinal);
    }
}
