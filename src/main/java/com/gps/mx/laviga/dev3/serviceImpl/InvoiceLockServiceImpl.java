package com.gps.mx.laviga.dev3.serviceImpl;

import com.gps.mx.laviga.dev3.service.InvoiceLockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class InvoiceLockServiceImpl implements InvoiceLockService {

    private static final String LOCK_FILE = "invoice.lock";
    private static final int MAX_WAIT_MS = 30000; // 30 segundos máximo de espera
    private static final int POLL_INTERVAL_MS = 500;

    @Value("${invoice.output.folder}")
    private String outputFolder;

    @Override
    public void acquire() throws Exception {
        File lockFile = new File(outputFolder, LOCK_FILE);
        File outDir = new File(outputFolder);
        if (!outDir.exists()) outDir.mkdirs();

        long waited = 0;
        while (!lockFile.createNewFile()) {
            if (waited >= MAX_WAIT_MS) {
                throw new Exception("[InvoiceLock] Timeout esperando el lock después de " + MAX_WAIT_MS + "ms");
            }
            System.out.println("[InvoiceLock] Carpeta ocupada, esperando...");
            Thread.sleep(POLL_INTERVAL_MS);
            waited += POLL_INTERVAL_MS;
        }
        System.out.println("[InvoiceLock] Lock adquirido");
    }

    @Override
    public void release() {
        File lockFile = new File(outputFolder, LOCK_FILE);
        if (lockFile.exists()) {
            lockFile.delete();
            System.out.println("[InvoiceLock] Lock liberado");
        }
    }
}