package com.liuiie.demo.utils.storage.exception;

/**
 * StorageRuntimeException
 *
 * @author Liuiie
 * @since 2025/1/9 14:41
 */
public class StorageRuntimeException extends RuntimeException {
    public StorageRuntimeException() {
        super();
    }

    public StorageRuntimeException(String message) {
        super(message);
    }

    public StorageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageRuntimeException(Throwable cause) {
        super(cause);
    }

    public static StorageRuntimeException newInstance(String message) {
        return new StorageRuntimeException(message);
    }

    public static StorageRuntimeException newInstance(Throwable cause) {
        return new StorageRuntimeException(cause);
    }

    public static StorageRuntimeException newInstance(String message, Throwable cause) {
        return new StorageRuntimeException(message, cause);
    }
}
