package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.reports.*;

import java.time.LocalDateTime;
import java.util.Map;

public interface ReportService {
    SalesReportDTO generateSalesReport(LocalDateTime startDate, LocalDateTime endDate);

    InventoryReportDTO generateInventoryReport();

    DashboardDTO getDashboard();

    LowStockReportDTO generateLowStockReport();

    AccountsReceivableReportDTO generateAccountsReceivableReport();

    TopProductsReportDTO generateTopProductsReport(LocalDateTime startDate, LocalDateTime endDate);
    
    Map<String, Object> getSalesChartData(int days);
    
    Map<String, Object> getSystemAlerts();
}
