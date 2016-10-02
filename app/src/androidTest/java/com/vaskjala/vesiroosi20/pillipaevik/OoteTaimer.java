package com.vaskjala.vesiroosi20.pillipaevik;

import android.support.test.espresso.IdlingResource;

public class OoteTaimer implements IdlingResource {
    private final long startTime;
    private final long waitingTime;
    private ResourceCallback resourceCallback;

    public OoteTaimer(long waitingTime) {
        this.startTime = System.currentTimeMillis();
        this.waitingTime = waitingTime;
    }

    @Override
    public String getName() {
        return OoteTaimer.class.getName() + ":" + waitingTime;
    }

    @Override
    public boolean isIdleNow() {
        long elapsed = System.currentTimeMillis() - startTime;
        boolean idle = (elapsed >= waitingTime);
        if (idle) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(
            ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}