package com.iot.shared.exception;

public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String deviceId) {
        super("Device introuvable : " + deviceId);

    }

}
