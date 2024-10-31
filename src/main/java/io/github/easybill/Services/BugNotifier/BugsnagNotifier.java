package io.github.easybill.Services.BugNotifier;

import com.bugsnag.Bugsnag;
import io.github.easybill.Contracts.IExceptionNotifier;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class BugsnagNotifier implements IExceptionNotifier {

    @NonNull
    private final String bugsnagApiKey;

    public BugsnagNotifier(@NonNull String bugsnagApiKey) {
        this.bugsnagApiKey = bugsnagApiKey;
    }

    public void notify(Throwable throwable) {
        try (Bugsnag bugsnag = new Bugsnag(bugsnagApiKey)) {
            bugsnag.notify(throwable);
        }
    }
}
