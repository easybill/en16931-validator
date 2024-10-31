package io.github.easybill.Services.BugNotifier;

import io.github.easybill.Contracts.IExceptionNotifier;

public final class NoopNotifier implements IExceptionNotifier {

    @Override
    public void notify(Throwable throwable) {
        return;
    }
}
