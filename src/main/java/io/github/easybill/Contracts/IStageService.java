package io.github.easybill.Contracts;

public interface IStageService {
    boolean isProd();
    boolean isDev();
    boolean isTest();
}
