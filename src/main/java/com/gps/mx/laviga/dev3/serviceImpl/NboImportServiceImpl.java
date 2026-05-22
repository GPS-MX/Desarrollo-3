package com.gps.mx.laviga.dev3.serviceImpl;

import com.gps.mx.laviga.dev3.service.NboImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NboImportServiceImpl implements NboImportService {

    @Value("${nbo.bat.path}")
    private String batPath;

    @Override
    public void importInvoice(String invoiceFilePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", batPath);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println("[NboImportService] Exit code: " + exitCode);
            System.out.println("[NboImportService] Output: " + output.toString());

            if (exitCode != 0) {
                System.err.println("[NboImportService] Error al importar: " + invoiceFilePath);
            } else {
                System.out.println("[NboImportService] Invoice importado: " + invoiceFilePath);
            }

        } catch (Exception e) {
            System.err.println("[NboImportService] Excepción: " + e.getMessage());
        }
    }
}