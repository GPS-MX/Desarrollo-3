package com.gps.mx.laviga.dev3.serviceImpl;

import com.gps.mx.laviga.dev3.model.Invoice;
import com.gps.mx.laviga.dev3.model.InvoiceDetail;
import com.gps.mx.laviga.dev3.service.PriceUpdateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceUpdateServiceImpl implements PriceUpdateService {

    @Value("${dbf.crud.url}")
    private String dbfCrudUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void updatePrices(Invoice invoice) {
        if (invoice.getDetails() == null || invoice.getDetails().isEmpty()) {
            System.out.println("[PriceUpdateService] Sin detalles, no se actualiza nada.");
            return;
        }

        List<Map<String, Object>> records = new ArrayList<>();

        for (InvoiceDetail detail : invoice.getDetails()) {
            String vendorItemCode = detail.getVendorItemCode();

            if (vendorItemCode == null || vendorItemCode.isEmpty() || detail.getPrice() == null) {
                System.out.println("[PriceUpdateService] Detalle sin VendorItemCode o precio, omitiendo.");
                continue;
            }

            Map<String, Object> record = new HashMap<>();
            record.put("id", vendorItemCode);       // chitname2 del DBF
            record.put("fieldName", "cost");        // campo de costo en ITM.DBF
            record.put("newValue", detail.getPrice().setScale(2, RoundingMode.HALF_UP).toPlainString());
            records.add(record);
        }

        if (records.isEmpty()) {
            System.out.println("[PriceUpdateService] No hay registros válidos para actualizar.");
            return;
        }

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("records", records);

            String json = objectMapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            String url = dbfCrudUrl + "/itm/updateCostByChitNameAlternate";
            String response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class).getBody();

            System.out.println("[PriceUpdateService] Respuesta: " + response);

        } catch (Exception e) {
            System.err.println("[PriceUpdateService] Error al actualizar costos: " + e.getMessage());
        }
    }
}