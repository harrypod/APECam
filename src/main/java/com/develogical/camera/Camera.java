package com.develogical.camera;

public class Camera {
    private Sensor sensor;
    private MemoryCard memoryCard;
    private boolean isPowerOn;
    private boolean isWriting;
    private boolean isPowerOffRequested;

    public Camera(Sensor sensor, MemoryCard memoryCard) {
        this.sensor = sensor;
        this.memoryCard = memoryCard;
    }

    public void pressShutter() {
        if(isPowerOn) {
            isWriting=true;
            memoryCard.write(sensor.readData(), () -> {
                isWriting=false;
                if(isPowerOffRequested) {
                    powerOff();
                }
            });
        }
    }

    public void powerOn() {
        sensor.powerUp();
        isPowerOn=true;
    }

    public void powerOff() {
        if(!isWriting) {
            sensor.powerDown();
            isPowerOn = isPowerOffRequested = false;
        }
        else {
            isPowerOffRequested=true;
        }
    }
}

