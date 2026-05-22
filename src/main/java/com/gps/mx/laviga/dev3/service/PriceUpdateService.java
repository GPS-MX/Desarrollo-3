package com.gps.mx.laviga.dev3.service;

import com.gps.mx.laviga.dev3.model.Invoice;

public interface PriceUpdateService {
    /**
     * Actualiza el defaultPrice de cada Item en la BD
     * usando el precio del CFDI (ValorUnitario) via dbf_crud_module.
     */
    void updatePrices(Invoice invoice);
}
