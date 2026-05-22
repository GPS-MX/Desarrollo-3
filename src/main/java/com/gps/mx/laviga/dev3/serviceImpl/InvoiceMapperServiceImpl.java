package com.gps.mx.laviga.dev3.serviceImpl;

import com.gps.mx.laviga.dev3.model.*;
import com.gps.mx.laviga.dev3.repository.ProveedorRepository;
import com.gps.mx.laviga.dev3.service.InvoiceMapperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceMapperServiceImpl implements InvoiceMapperService {

    @Value("${invoice.vendor-id}")
    private String vendorId;

    @Value("${invoice.status}")
    private String status;

    @Value("${invoice.exception-handling}")
    private String exceptionHandling;

    @Value("${invoice.delivery-time}")
    private String deliveryTime;

    @Value("${nbo.site-id}")
    private String nboSiteId;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    public Invoice map(CfdiComprobante cfdi) {
        Invoice invoice = new Invoice();
        invoice.setHeader(buildHeader(cfdi));
        invoice.setDetails(buildDetails(cfdi));
        return invoice;
    }

    private InvoiceHeader buildHeader(CfdiComprobante cfdi) {
        InvoiceHeader header = new InvoiceHeader();

        String resolvedVendorId = vendorId;
        if (cfdi.getEmisorNombre() != null && !cfdi.getEmisorNombre().isEmpty()) {
            String nombreLimpio = cfdi.getEmisorNombre()
                .replaceAll("(?i)\\s*S\\.A\\.\\s+DE\\s+C\\.V\\.*", "")
                .replaceAll("(?i)\\s*SA\\s+DE\\s+CV\\.*", "")
                .trim();

            resolvedVendorId = proveedorRepository
                    .findProveedorByNombreFlexible(nombreLimpio)
                    .map(Proveedor::getVendorId)
                    .orElseGet(() -> {
                        System.out.println("[InvoiceMapper] Proveedor no encontrado: "
                            + cfdi.getEmisorNombre() + " (buscado como: " + nombreLimpio + "), usando fallback");
                        return vendorId;
                    });
        }

        header.setVendorId(resolvedVendorId);
        header.setStatus(status);
        header.setExceptionHandling(exceptionHandling);
        header.setCustomerNumber("");
        header.setSiteId(nboSiteId);
        header.setInvoiceAdjustment(false);

        String invoiceId = (cfdi.getUuid() != null && !cfdi.getUuid().isEmpty())
            ? cfdi.getUuid()
            : cfdi.getFolio();
        header.setInvoiceId(invoiceId);

        header.setOriginalInvoiceId("0");
        String fecha = cfdi.getFecha();
        String soloFecha = fecha != null && fecha.contains("T") ? fecha.substring(0, fecha.indexOf('T')) : fecha;
        header.setInvoiceDate(soloFecha);
        header.setDeliveryDate(soloFecha);
        header.setDeliveryTime(deliveryTime);
        header.setInvoiceTotal(cfdi.getTotal().setScale(2, java.math.RoundingMode.HALF_UP));
        header.setInvoiceTaxAmount(
            cfdi.getTotalImpuestosTrasladados() != null
                ? cfdi.getTotalImpuestosTrasladados().setScale(2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP)
        );
        header.setInvoiceFreightAmount(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP));
        header.setPoNumber("0");
        return header;
    }

    private List<InvoiceDetail> buildDetails(CfdiComprobante cfdi) {
        List<InvoiceDetail> details = new ArrayList<>();
        int lineNumber = 1;

        for (CfdiConcepto concepto : cfdi.getConceptos()) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setLineItemNumber(lineNumber++);
            detail.setQuantityReceived(concepto.getCantidad().intValue());
            detail.setPrice(concepto.getValorUnitario().setScale(2, java.math.RoundingMode.HALF_UP));
            detail.setExtension(concepto.getImporte().setScale(2, java.math.RoundingMode.HALF_UP));
            detail.setTaxAmount(
                concepto.getImpuestoImporte() != null
                    ? concepto.getImpuestoImporte().setScale(2, java.math.RoundingMode.HALF_UP)
                    : BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP)
            );

            String vendorCode = concepto.getNoIdentificacion() != null && !concepto.getNoIdentificacion().isEmpty()
                ? concepto.getNoIdentificacion()
                : concepto.getClaveProdServ();

            if (vendorCode != null) {
                vendorCode = vendorCode.replaceAll("^0+(?!$)", "").trim();
            }

            detail.setVendorItemCode(vendorCode);
            detail.setItemName(concepto.getDescripcion());
            details.add(detail);
        }
        return details;
    }
}