package com.huawei.mfg.runnable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base Runnable which allows to stop the runnable
 */
abstract public class MfgRunnable implements Runnable {
    protected AtomicBoolean running;

    public MfgRunnable() {
        this.running = new AtomicBoolean(true);
    }

    public void stop() {
        this.running.set(false);
        this.notifyAll();
    }

}
