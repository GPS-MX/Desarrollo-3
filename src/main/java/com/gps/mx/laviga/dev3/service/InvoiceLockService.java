package com.gps.mx.laviga.dev3.service;

public interface InvoiceLockService {
    void acquire() throws Exception;
    void release();
}