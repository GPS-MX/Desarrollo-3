package com.gps.mx.laviga.dev3.service;

import com.gps.mx.laviga.dev3.model.CfdiComprobante;

import java.io.File;

public interface CfdiParserService {
    CfdiComprobante parse(File cfdiFile) throws Exception;
}
