package dev.simplix.cirrus.common.menu;

import dev.simplix.cirrus.common.handler.ActionHandler;
import dev.simplix.cirrus.common.util.SafeRunnable;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface ErrorProne {

    default void handleException(@Nullable ActionHandler actionHandler, Throwable throwable) {
    }

    /**
     * Executes error prone code that may throw a exception. When an exception is thrown, it will be
     * delegated to the exception handler.
     *
     * @param runnable The runnable to run
     */
    default void errorProne(@NonNull SafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            handleException(null, throwable);
        }
    }

    /**
     * Executes error prone code that may throw a exception. When an exception is thrown, it will be
     * delegated to the exception handler.
     *
     * @param runnable        The runnable to run
     * @param finallyRunnable Code that may run finally
     */
    default void errorProne(@NonNull SafeRunnable runnable, @NonNull Runnable finallyRunnable) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            handleException(null, throwable);
        } finally {
            finallyRunnable.run();
        }
    }

}
