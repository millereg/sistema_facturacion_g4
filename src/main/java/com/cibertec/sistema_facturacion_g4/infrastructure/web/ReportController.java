package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.reports.*;
import com.cibertec.sistema_facturacion_g4.application.ports.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<SalesReportDTO> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        SalesReportDTO report = reportService.generateSalesReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/inventory")
    public ResponseEntity<InventoryReportDTO> getInventoryReport() {
        InventoryReportDTO report = reportService.generateInventoryReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        DashboardDTO dashboard = reportService.getDashboard();
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<LowStockReportDTO> getLowStockReport() {
        LowStockReportDTO report = reportService.generateLowStockReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/accounts-receivable")
    public ResponseEntity<AccountsReceivableReportDTO> getAccountsReceivableReport() {
        AccountsReceivableReportDTO report = reportService.generateAccountsReceivableReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/top-products")
    public ResponseEntity<TopProductsReportDTO> getTopProductsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        TopProductsReportDTO report = reportService.generateTopProductsReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/dashboard/sales-chart")
    public ResponseEntity<Map<String, Object>> getSalesChart(@RequestParam(defaultValue = "7") int days) {
        Map<String, Object> chartData = reportService.getSalesChartData(days);
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/dashboard/alerts")
    public ResponseEntity<Map<String, Object>> getSystemAlerts() {
        Map<String, Object> alerts = reportService.getSystemAlerts();
        return ResponseEntity.ok(alerts);
    }
}
