package com.gps.mx.laviga.dev3.model;

import java.math.BigDecimal;
import java.util.List;

public class CfdiComprobante {

    private String folio;
    private String uuid;            // UUID del TimbreFiscalDigital — usado como InvoiceID
    private String fecha;           // yyyy-MM-dd'T'HH:mm:ss
    private BigDecimal total;
    private BigDecimal totalImpuestosTrasladados;
    private List<CfdiConcepto> conceptos;
    
    private String emisorNombre;

    public String getEmisorNombre() { return emisorNombre; }
    public void setEmisorNombre(String emisorNombre) { this.emisorNombre = emisorNombre; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getTotalImpuestosTrasladados() { return totalImpuestosTrasladados; }
    public void setTotalImpuestosTrasladados(BigDecimal totalImpuestosTrasladados) {
        this.totalImpuestosTrasladados = totalImpuestosTrasladados;
    }

    public List<CfdiConcepto> getConceptos() { return conceptos; }
    public void setConceptos(List<CfdiConcepto> conceptos) { this.conceptos = conceptos; }
}