package com.gps.mx.laviga.dev3.serviceImpl;

import com.gps.mx.laviga.dev3.model.CfdiComprobante;
import com.gps.mx.laviga.dev3.model.CfdiConcepto;
import com.gps.mx.laviga.dev3.service.CfdiParserService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CfdiParserServiceImpl implements CfdiParserService {

    private static final String NS_CFDI = "http://www.sat.gob.mx/cfd/4";
    private static final String NS_TFD  = "http://www.sat.gob.mx/TimbreFiscalDigital";

    @Override
    public CfdiComprobante parse(File cfdiFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(cfdiFile);

        Element comprobante = doc.getDocumentElement();

        CfdiComprobante cfdi = new CfdiComprobante();
        cfdi.setFolio(comprobante.getAttribute("Folio"));

        // UUID del TimbreFiscalDigital
        NodeList tfdNodes = doc.getElementsByTagNameNS(NS_TFD, "TimbreFiscalDigital");
        if (tfdNodes.getLength() > 0) {
            cfdi.setUuid(((Element) tfdNodes.item(0)).getAttribute("UUID"));
        }

        cfdi.setFecha(comprobante.getAttribute("Fecha"));
        
        // Validación de nulidad o vacío en Total
        String totalStr = comprobante.getAttribute("Total");
        cfdi.setTotal(totalStr != null && !totalStr.isEmpty() ? new BigDecimal(totalStr) : BigDecimal.ZERO);

        // Emisor
        NodeList emisorNodes = doc.getElementsByTagNameNS(NS_CFDI, "Emisor");
        if (emisorNodes.getLength() > 0) {
            Element emisor = (Element) emisorNodes.item(0);
            cfdi.setEmisorNombre(emisor.getAttribute("Nombre"));
        }

        // TotalImpuestosTrasladados seguro
        BigDecimal totalImpuestos = BigDecimal.ZERO;
        NodeList hijosComprobante = comprobante.getChildNodes();
        for (int i = 0; i < hijosComprobante.getLength(); i++) {
            org.w3c.dom.Node nodo = hijosComprobante.item(i);
            if (nodo.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                // Modificado para soportar tanto localName como nodeName por tolerancia de parsers
                String localName = nodo.getLocalName() != null ? nodo.getLocalName() : nodo.getNodeName();
                if (localName.contains("Impuestos")) {
                    String totalTrasladados = ((Element) nodo).getAttribute("TotalImpuestosTrasladados");
                    if (totalTrasladados != null && !totalTrasladados.isEmpty()) {
                        totalImpuestos = new BigDecimal(totalTrasladados);
                    }
                    break;
                }
            }
        }
        cfdi.setTotalImpuestosTrasladados(totalImpuestos);

        // Conceptos
        List<CfdiConcepto> conceptos = new ArrayList<>();
        NodeList conceptoNodes = doc.getElementsByTagNameNS(NS_CFDI, "Concepto");
        for (int i = 0; i < conceptoNodes.getLength(); i++) {
            Element el = (Element) conceptoNodes.item(i);
            CfdiConcepto concepto = new CfdiConcepto();
            concepto.setClaveProdServ(el.getAttribute("ClaveProdServ"));
            concepto.setNoIdentificacion(el.getAttribute("NoIdentificacion"));
            
            String cantStr = el.getAttribute("Cantidad");
            concepto.setCantidad(cantStr != null && !cantStr.isEmpty() ? new BigDecimal(cantStr) : BigDecimal.ZERO);
            
            concepto.setDescripcion(el.getAttribute("Descripcion"));
            
            String valUnitStr = el.getAttribute("ValorUnitario");
            concepto.setValorUnitario(valUnitStr != null && !valUnitStr.isEmpty() ? new BigDecimal(valUnitStr) : BigDecimal.ZERO);
            
            String importeStr = el.getAttribute("Importe");
            concepto.setImporte(importeStr != null && !importeStr.isEmpty() ? new BigDecimal(importeStr) : BigDecimal.ZERO);

            // Suma de traslados del concepto controlando cadenas vacías
            BigDecimal impuestoConcepto = BigDecimal.ZERO;
            NodeList traslados = el.getElementsByTagNameNS(NS_CFDI, "Traslado");
            for (int j = 0; j < traslados.getLength(); j++) {
                Element traslado = (Element) traslados.item(j);
                String imp = traslado.getAttribute("Importe");
                // MEJORA: Evita lanzar NumberFormatException en conceptos exentos/tasa cero
                if (imp != null && !imp.trim().isEmpty()) { 
                    impuestoConcepto = impuestoConcepto.add(new BigDecimal(imp.trim()));
                }
            }
            concepto.setImpuestoImporte(impuestoConcepto);

            conceptos.add(concepto);
        }
        cfdi.setConceptos(conceptos);

        return cfdi;
    }
}