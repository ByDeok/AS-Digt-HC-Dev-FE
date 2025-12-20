package vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception;

public class DeviceAlreadyLinkedException extends RuntimeException {
    public DeviceAlreadyLinkedException(String message) {
        super(message);
    }
}

