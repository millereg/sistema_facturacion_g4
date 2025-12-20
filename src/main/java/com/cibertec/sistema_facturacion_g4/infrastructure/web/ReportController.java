package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.reports.*;
import com.cibertec.sistema_facturacion_g4.application.ports.ReportService;
import com.cibertec.sistema_facturacion_g4.application.usecases.ReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ReportServiceImpl reportServiceImpl;

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

    @GetMapping("/dashboard/sales-today")
    public ResponseEntity<BigDecimal> getSalesToday() {
        return ResponseEntity.ok(reportServiceImpl.getSalesToday());
    }

    @GetMapping("/dashboard/sales-month")
    public ResponseEntity<BigDecimal> getSalesMonth() {
        return ResponseEntity.ok(reportServiceImpl.getSalesMonth());
    }

    @GetMapping("/dashboard/customers-total")
    public ResponseEntity<Integer> getCustomersTotal() {
        return ResponseEntity.ok(reportServiceImpl.getCustomersTotal());
    }

    @GetMapping("/dashboard/products-stock")
    public ResponseEntity<Integer> getProductsStock() {
        return ResponseEntity.ok(reportServiceImpl.getProductsStock());
    }

    @GetMapping("/dashboard/invoices-month")
    public ResponseEntity<Integer> getInvoicesMonth() {
        return ResponseEntity.ok(reportServiceImpl.getInvoicesMonth());
    }

    @GetMapping("/dashboard/pending-payments")
    public ResponseEntity<BigDecimal> getPendingPayments() {
        return ResponseEntity.ok(reportServiceImpl.getPendingPayments());
    }

    @GetMapping("/dashboard/active-customers")
    public ResponseEntity<Integer> getActiveCustomers() {
        return ResponseEntity.ok(reportServiceImpl.getActiveCustomers());
    }

    @GetMapping("/dashboard/low-stock")
    public ResponseEntity<Integer> getLowStock() {
        return ResponseEntity.ok(reportServiceImpl.getLowStock());
    }

    @GetMapping("/dashboard/sales-chart-filtered")
    public ResponseEntity<Map<String, Object>> getSalesChartFiltered() {
        return ResponseEntity.ok(reportServiceImpl.getSalesChart());
    }

    @GetMapping("/dashboard/alerts-filtered")
    public ResponseEntity<Map<String, Object>> getDashboardAlerts() {
        return ResponseEntity.ok(reportServiceImpl.getDashboardAlerts());
    }
}
