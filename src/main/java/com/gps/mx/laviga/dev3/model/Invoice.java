package com.gps.mx.laviga.dev3.model;

import java.util.List;

public class Invoice {

    private InvoiceHeader header;
    private List<InvoiceDetail> details;

    public InvoiceHeader getHeader() { return header; }
    public void setHeader(InvoiceHeader header) { this.header = header; }

    public List<InvoiceDetail> getDetails() { return details; }
    public void setDetails(List<InvoiceDetail> details) { this.details = details; }
}
