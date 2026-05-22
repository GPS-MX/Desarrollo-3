package com.gps.mx.laviga.dev3.service;

import com.gps.mx.laviga.dev3.model.CfdiComprobante;
import com.gps.mx.laviga.dev3.model.Invoice;

public interface InvoiceMapperService {
    Invoice map(CfdiComprobante cfdi);
}
