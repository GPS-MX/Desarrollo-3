package com.gps.mx.laviga.dev3.serviceImpl;

import com.gps.mx.laviga.dev3.model.Invoice;
import com.gps.mx.laviga.dev3.model.InvoiceDetail;
import com.gps.mx.laviga.dev3.model.InvoiceHeader;
import com.gps.mx.laviga.dev3.service.InvoiceLockService;
import com.gps.mx.laviga.dev3.service.InvoiceWriterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceWriterServiceImpl implements InvoiceWriterService {

    private static final String NS = "http://xxxx.BOI.Inbound.SchemaMap.ElectronicInvoices.ElectronicInvoices_MenuLinkImport_XML";
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final InvoiceLockService invoiceLockService;

    @Value("${invoice.output.folder}")
    private String outputFolder;

    public InvoiceWriterServiceImpl(InvoiceLockService invoiceLockService) {
        this.invoiceLockService = invoiceLockService;
    }

    @Override
    public File write(Invoice invoice) throws Exception {
        invoiceLockService.acquire();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element invoices = doc.createElementNS(NS, "ns0:Invoices");
            doc.appendChild(invoices);

            Element invoiceEl = doc.createElement("Invoice");
            invoices.appendChild(invoiceEl);

            InvoiceHeader h = invoice.getHeader();
            invoiceEl.appendChild(el(doc, "Header", null));
            Element header = (Element) invoiceEl.getLastChild();

            header.appendChild(el(doc, "VendorID",             h.getVendorId()));
            header.appendChild(el(doc, "Status",               h.getStatus()));
            header.appendChild(el(doc, "ExceptionHandling",    h.getExceptionHandling()));
            header.appendChild(el(doc, "CustomerNumber",       h.getCustomerNumber()));
            header.appendChild(el(doc, "SiteID",               h.getSiteId()));
            header.appendChild(el(doc, "SupplierID",           ""));
            header.appendChild(el(doc, "InvoiceAdjustment",    String.valueOf(h.isInvoiceAdjustment())));
            header.appendChild(el(doc, "InvoiceID",            h.getInvoiceId()));
            header.appendChild(el(doc, "OriginalInvoiceID", "0"));
            header.appendChild(el(doc, "InvoiceDate",          h.getInvoiceDate()));
            header.appendChild(el(doc, "DeliveryDate",         h.getDeliveryDate()));
            //header.appendChild(el(doc, "DeliveryTime",         h.getDeliveryTime()));
            header.appendChild(el(doc, "InvoiceTotal",         h.getInvoiceTotal().toPlainString()));
            header.appendChild(el(doc, "InvoiceTaxAmount",     h.getInvoiceTaxAmount().toPlainString()));
            header.appendChild(el(doc, "InvoiceFreightAmount", h.getInvoiceFreightAmount().toPlainString()));
            header.appendChild(el(doc, "PONumber",             h.getPoNumber()));
            header.appendChild(el(doc, "POID",                 ""));

            for (InvoiceDetail d : invoice.getDetails()) {
                Element detail = doc.createElement("Detail");
                detail.appendChild(el(doc, "LineItemNumber",   String.valueOf(d.getLineItemNumber())));
                detail.appendChild(el(doc, "QuantityReceived", String.valueOf(d.getQuantityReceived())));
                detail.appendChild(el(doc, "Price",            d.getPrice().toPlainString()));
                detail.appendChild(el(doc, "Extension",        d.getExtension().toPlainString()));
                detail.appendChild(el(doc, "TaxAmount",        d.getTaxAmount().toPlainString()));
                detail.appendChild(el(doc, "VendorItemCode",   d.getVendorItemCode()));
                detail.appendChild(el(doc, "ItemName",         d.getItemName()));
                invoiceEl.appendChild(detail);
            }

            String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP);
            String fileName = "xeinvcl02mnl." + timestamp + ".xml"; 

            File outDir = new File(outputFolder);

            if (!outDir.exists()) outDir.mkdirs();

            File outFile = new File(outDir, fileName);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(doc), new StreamResult(outFile));

            return outFile;
        } finally {
            invoiceLockService.release();
        }
    }

    private Element el(Document doc, String tag, String text) {
        Element el = doc.createElement(tag);
        if (text != null) el.setTextContent(text);
        return el;
    }
}