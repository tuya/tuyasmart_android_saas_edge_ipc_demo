package com.tuya.ai.ipcsdkdemo.video;


import android.util.Log;

public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;

    private String name;

    public FPSCounter() {
        name = "";
    }

    public FPSCounter(String name) {
        this.name = " " + name;
    }

    public void logFrame() {
        frames++;
        if (System.nanoTime() - startTime >= 1000000000) {
            Log.d("FPSCounter", name + "fps: " + frames);
            startTime = System.nanoTime();
            frames = 0;
        }
    }
}
