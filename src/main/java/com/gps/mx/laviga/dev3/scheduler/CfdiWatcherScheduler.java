package com.gps.mx.laviga.dev3.scheduler;

import com.gps.mx.laviga.dev3.model.CfdiComprobante;
import com.gps.mx.laviga.dev3.model.Invoice;
import com.gps.mx.laviga.dev3.service.CfdiParserService;
import com.gps.mx.laviga.dev3.service.InvoiceMapperService;
import com.gps.mx.laviga.dev3.service.InvoiceWriterService;
import com.gps.mx.laviga.dev3.service.NboImportService;
import com.gps.mx.laviga.dev3.service.PriceUpdateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class CfdiWatcherScheduler {

    private final CfdiParserService cfdiParserService;
    private final InvoiceMapperService invoiceMapperService;
    private final InvoiceWriterService invoiceWriterService;
    private final PriceUpdateService priceUpdateService;
    private final NboImportService nboImportService;

    @Value("${cfdi.input.folder}")
    private String inputFolder;

    @Value("${cfdi.processed.folder}")
    private String processedFolder;

    @Value("${cfdi.error.folder}")
    private String errorFolder;

    public CfdiWatcherScheduler(CfdiParserService cfdiParserService,
                                 InvoiceMapperService invoiceMapperService,
                                 InvoiceWriterService invoiceWriterService,
                                 PriceUpdateService priceUpdateService,
                                 NboImportService nboImportService) {
        this.cfdiParserService = cfdiParserService;
        this.invoiceMapperService = invoiceMapperService;
        this.invoiceWriterService = invoiceWriterService;
        this.priceUpdateService = priceUpdateService;
        this.nboImportService = nboImportService;
    }

    @Scheduled(fixedDelayString = "${cfdi.watcher.interval}")
    public void procesarCfdis() {
        File inputDir = new File(inputFolder);
        if (!inputDir.exists()) inputDir.mkdirs();

        File[] archivos = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
        if (archivos == null || archivos.length == 0) return;

        for (File cfdiFile : archivos) {
            System.out.println("[CfdiWatcher] Procesando: " + cfdiFile.getName());
            try {
                // 1. Parsear CFDI
                CfdiComprobante cfdi = cfdiParserService.parse(cfdiFile);

                // 2. Mapear a Invoice
                Invoice invoice = invoiceMapperService.map(cfdi);

                // 3. Generar Invoice XML para el NBO
                File invoiceFile = invoiceWriterService.write(invoice);
                System.out.println("[CfdiWatcher] Invoice generado: " + invoiceFile.getAbsolutePath());

                // 4. Actualizar precios en BD
                priceUpdateService.updatePrices(invoice);

                // 5. Importar Invoice al NBO via BOANET
                nboImportService.importInvoice(invoiceFile.getAbsolutePath());

                // 6. Mover CFDI a procesados
                moverArchivo(cfdiFile, processedFolder);

            } catch (Exception e) {
                System.err.println("[CfdiWatcher] Error procesando " + cfdiFile.getName() + ": " + e.getMessage());
                moverArchivo(cfdiFile, errorFolder);
            }
        }
    }

    private void moverArchivo(File archivo, String destino) {
        try {
            File destDir = new File(destino);
            if (!destDir.exists()) destDir.mkdirs();
            File dest = new File(destDir, archivo.getName());
            Files.move(archivo.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("[CfdiWatcher] No se pudo mover " + archivo.getName() + ": " + e.getMessage());
        }
    }
}