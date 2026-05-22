@echo off
echo [%date% %time%] Iniciando importacion >> C:\GPS\LaViga\import_log.txt
cd /d "C:\Program Files (x86)\NCRBackOffice\BackOfficeSwitchboard"
echo [%date% %time%] Ejecutando BOANET... >> C:\GPS\LaViga\import_log.txt
BOANET.Windows.exe /User "Facturas GPS" /Password "facturas.2026" /SiteID Pruebas01 /ImportID inv001 >> C:\GPS\LaViga\import_log.txt 2>&1
echo [%date% %time%] Exit code: %errorlevel% >> C:\GPS\LaViga\import_log.txt

REM Mover XMLs procesados
for %%f in (C:\NBO\InvoiceImport\xeinvcl02mnl.*.xml) do (
    move "%%f" "C:\NBO\InvoiceImport\procesados\" >> C:\GPS\LaViga\import_log.txt 2>&1
)