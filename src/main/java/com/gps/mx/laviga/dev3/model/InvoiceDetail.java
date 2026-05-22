package com.gps.mx.laviga.dev3.model;

import java.math.BigDecimal;

public class InvoiceDetail {

    private int lineItemNumber;
    private int quantityReceived;
    private BigDecimal price;
    private BigDecimal extension;
    private BigDecimal taxAmount;
    private String vendorItemCode;
    private String itemName;

    public int getLineItemNumber() { return lineItemNumber; }
    public void setLineItemNumber(int lineItemNumber) { this.lineItemNumber = lineItemNumber; }

    public int getQuantityReceived() { return quantityReceived; }
    public void setQuantityReceived(int quantityReceived) { this.quantityReceived = quantityReceived; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getExtension() { return extension; }
    public void setExtension(BigDecimal extension) { this.extension = extension; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public String getVendorItemCode() { return vendorItemCode; }
    public void setVendorItemCode(String vendorItemCode) { this.vendorItemCode = vendorItemCode; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
}
