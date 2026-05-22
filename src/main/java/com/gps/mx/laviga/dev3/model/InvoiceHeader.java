package com.gps.mx.laviga.dev3.model;

import java.math.BigDecimal;

public class InvoiceHeader {

    private String vendorId;
    private String status;
    private String exceptionHandling;
    private String customerNumber;
    private String siteId;
    private boolean invoiceAdjustment;
    private String invoiceId;
    private String originalInvoiceId;
    private String invoiceDate;
    private String deliveryDate;
    private String deliveryTime;
    private BigDecimal invoiceTotal;
    private BigDecimal invoiceTaxAmount;
    private BigDecimal invoiceFreightAmount;
    private String poNumber;

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExceptionHandling() { return exceptionHandling; }
    public void setExceptionHandling(String exceptionHandling) { this.exceptionHandling = exceptionHandling; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }

    public boolean isInvoiceAdjustment() { return invoiceAdjustment; }
    public void setInvoiceAdjustment(boolean invoiceAdjustment) { this.invoiceAdjustment = invoiceAdjustment; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getOriginalInvoiceId() { return originalInvoiceId; }
    public void setOriginalInvoiceId(String originalInvoiceId) { this.originalInvoiceId = originalInvoiceId; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }

    public BigDecimal getInvoiceTotal() { return invoiceTotal; }
    public void setInvoiceTotal(BigDecimal invoiceTotal) { this.invoiceTotal = invoiceTotal; }

    public BigDecimal getInvoiceTaxAmount() { return invoiceTaxAmount; }
    public void setInvoiceTaxAmount(BigDecimal invoiceTaxAmount) { this.invoiceTaxAmount = invoiceTaxAmount; }

    public BigDecimal getInvoiceFreightAmount() { return invoiceFreightAmount; }
    public void setInvoiceFreightAmount(BigDecimal invoiceFreightAmount) { this.invoiceFreightAmount = invoiceFreightAmount; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
}
