package com.gps.mx.laviga.dev3.controller;

import com.gps.mx.laviga.dev3.model.CfdiComprobante;
import com.gps.mx.laviga.dev3.model.Invoice;
import com.gps.mx.laviga.dev3.service.CfdiParserService;
import com.gps.mx.laviga.dev3.service.InvoiceMapperService;
import com.gps.mx.laviga.dev3.service.InvoiceWriterService;
import com.gps.mx.laviga.dev3.service.PriceUpdateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cfdi")
public class CfdiController {

    private final CfdiParserService cfdiParserService;
    private final InvoiceMapperService invoiceMapperService;
    private final InvoiceWriterService invoiceWriterService;
    private final PriceUpdateService priceUpdateService;

    @Value("${cfdi.input.folder}")
    private String inputFolder;

    public CfdiController(CfdiParserService cfdiParserService,
                          InvoiceMapperService invoiceMapperService,
                          InvoiceWriterService invoiceWriterService,
                          PriceUpdateService priceUpdateService) {
        this.cfdiParserService = cfdiParserService;
        this.invoiceMapperService = invoiceMapperService;
        this.invoiceWriterService = invoiceWriterService;
        this.priceUpdateService = priceUpdateService;
    }

    // Sube el XML directo desde Postman como multipart
    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarCfdi(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("error", "No se recibió ningún archivo");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            File tempFile = File.createTempFile("cfdi_", ".xml");
            Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return ejecutarFlujo(tempFile, response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Procesa un XML que ya está en la carpeta de entrada por nombre
    @PostMapping("/procesar-local")
    public ResponseEntity<Map<String, Object>> procesarLocal(@RequestParam("archivo") String nombreArchivo) {
        Map<String, Object> response = new HashMap<>();
        File cfdiFile = new File(inputFolder, nombreArchivo);
        if (!cfdiFile.exists()) {
            response.put("error", "Archivo no encontrado: " + cfdiFile.getAbsolutePath());
            return ResponseEntity.badRequest().body(response);
        }
        return ejecutarFlujo(cfdiFile, response);
    }

    private ResponseEntity<Map<String, Object>> ejecutarFlujo(File cfdiFile, Map<String, Object> response) {
        try {
            CfdiComprobante cfdi = cfdiParserService.parse(cfdiFile);
            response.put("folio", cfdi.getFolio());
            response.put("fecha", cfdi.getFecha());
            response.put("total", cfdi.getTotal());
            response.put("totalImpuestos", cfdi.getTotalImpuestosTrasladados());
            response.put("conceptos", cfdi.getConceptos().size());

            Invoice invoice = invoiceMapperService.map(cfdi);
            File invoiceFile = invoiceWriterService.write(invoice);
            response.put("invoiceGenerado", invoiceFile.getAbsolutePath());

            priceUpdateService.updatePrices(invoice);
            response.put("status", "OK");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}