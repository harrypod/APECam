package com.develogical.camera;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class CameraTest {
    @Test
    public void switchingTheCameraOnPowersUpTheSensor() {
        Sensor sensor = mock(Sensor.class);
        Camera camera = new Camera(sensor, null);

        camera.powerOn();
        verify(sensor).powerUp();
    }

    @Test
    public void switchingTheCameraOffPowersDownTheSensor() {
        Sensor sensor = mock(Sensor.class);
        Camera camera = new Camera(sensor, null);

        camera.powerOff();
        verify(sensor).powerDown();
    }

    @Test
    public void pressingTheShutterWithThePowerOnCopiesDataFromTheSensorToTheMemoryCard() {
        Sensor sensor = mock(Sensor.class);

        byte[] bytesToTest = new byte[] { 1,2,3 };
        when(sensor.readData()).thenReturn(bytesToTest);

        MemoryCard memoryCard = mock(MemoryCard.class);
        Camera camera = new Camera(sensor, memoryCard);

        camera.powerOn();
        camera.pressShutter();

        verify(memoryCard).write(eq(bytesToTest), any());
    }

    @Test
    public void pressingTheShutterWithThePowerOffShouldNotCopyDataFromTheSensorToTheMemoryCard() {
        MemoryCard memoryCard = mock(MemoryCard.class);
        Camera camera = new Camera(null, memoryCard);

        camera.pressShutter();

        verifyZeroInteractions(memoryCard);
    }

    @Test
    public void pressingTheShutterWithThePowerOffAfterItHasBeenTurnedOnAndOffShouldNotCopyDataFromTheSensorToTheMemoryCard() {
        Sensor sensor = mock(Sensor.class);
        MemoryCard memoryCard = mock(MemoryCard.class);
        Camera camera = new Camera(sensor, memoryCard);

        camera.powerOn();
        camera.powerOff();
        camera.pressShutter();

        verifyZeroInteractions(memoryCard);
    }

    @Test
    public void whenDataIsCurrentlyBeingWrittenSwitchingCameraOffDoesNotPowerDownSensor() {
        Sensor sensor = mock(Sensor.class);
        MemoryCard memoryCard = mock(MemoryCard.class);

        Camera camera = new Camera(sensor, memoryCard);

        camera.powerOn();
        camera.pressShutter();
        camera.powerOff();

        ArgumentCaptor<WriteCompleteListener> argument = ArgumentCaptor.forClass(WriteCompleteListener.class);
        verify(memoryCard).write(any(), argument.capture());
        verify(sensor, never()).powerDown();
    }

    @Test
    public void whenPowerOffHasBeenRequestWhenDataIsBeingWrittenShouldPowerDownOnceDataComplete() {
        Sensor sensor = mock(Sensor.class);
        MemoryCard memoryCard = mock(MemoryCard.class);

        Camera camera = new Camera(sensor, memoryCard);

        camera.powerOn();
        camera.pressShutter();
        camera.powerOff();

        ArgumentCaptor<WriteCompleteListener> argument = ArgumentCaptor.forClass(WriteCompleteListener.class);
        verify(memoryCard).write(any(), argument.capture());

        argument.getValue().writeComplete();
        verify(sensor).powerDown();
    }
}
