package com.gps.mx.laviga.dev3.service;

import com.gps.mx.laviga.dev3.model.Invoice;

import java.io.File;

public interface InvoiceWriterService {
    File write(Invoice invoice) throws Exception;
}
