package com.gps.mx.laviga.dev3.model;

import java.math.BigDecimal;

public class CfdiConcepto {

    private String claveProdServ;
    private String noIdentificacion; // ← nuevo
    private BigDecimal cantidad;
    private String descripcion;
    private BigDecimal valorUnitario;
    private BigDecimal importe;
    private BigDecimal impuestoImporte;

    public String getNoIdentificacion() { return noIdentificacion; }
    public void setNoIdentificacion(String noIdentificacion) { this.noIdentificacion = noIdentificacion; }

    public String getClaveProdServ() { return claveProdServ; }
    public void setClaveProdServ(String claveProdServ) { this.claveProdServ = claveProdServ; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public BigDecimal getImpuestoImporte() { return impuestoImporte; }
    public void setImpuestoImporte(BigDecimal impuestoImporte) { this.impuestoImporte = impuestoImporte; }
}
