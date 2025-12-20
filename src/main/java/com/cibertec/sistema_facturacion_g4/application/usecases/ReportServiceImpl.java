package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.reports.*;
import com.cibertec.sistema_facturacion_g4.application.ports.ReportService;
import com.cibertec.sistema_facturacion_g4.application.dto.reports.TopProductStats;
import com.cibertec.sistema_facturacion_g4.domain.entities.Company;
import com.cibertec.sistema_facturacion_g4.domain.entities.Customer;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.entities.Supplier;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CompanyRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CustomerRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceDetailRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.PaymentRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.SupplierRepository;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
        private final InvoiceRepository invoiceRepository;
        private final InvoiceDetailRepository invoiceDetailRepository;
        private final ProductRepository productRepository;
        private final CustomerRepository customerRepository;
        private final CompanyRepository companyRepository;
        private final SupplierRepository supplierRepository;
        private final PaymentRepository paymentRepository;

        @Override
        public SalesReportDTO generateSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
                Long companyId = TenantUtils.getCurrentCompanyId();

                Company company = companyRepository.findById(companyId)
                                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                List<Invoice> invoices = invoiceRepository.findByCompanyIdAndDateRange(
                                companyId, startDate, endDate);
                BigDecimal totalSales = BigDecimal.ZERO;
                BigDecimal totalTax = BigDecimal.ZERO;

                for (Invoice invoice : invoices) {
                        if (invoice.getTotalAmount() != null) {
                                totalSales = totalSales.add(invoice.getTotalAmount());
                        }
                        if (invoice.getTaxAmount() != null) {
                                totalTax = totalTax.add(invoice.getTaxAmount());
                        }
                }
                BigDecimal averageTicket = BigDecimal.ZERO;
                if (!invoices.isEmpty()) {
                        averageTicket = totalSales.divide(
                                        BigDecimal.valueOf(invoices.size()),
                                        2,
                                        RoundingMode.HALF_UP);
                }

                return SalesReportDTO.builder()
                                .companyId(companyId)
                                .companyName(company.getBusinessName())
                                .startDate(startDate)
                                .endDate(endDate)
                                .totalInvoices(invoices.size())
                                .totalSales(totalSales)
                                .totalTax(totalTax)
                                .averageTicket(averageTicket)
                                .currency("PEN")
                                .build();
        }

        @Override
        public InventoryReportDTO generateInventoryReport() {
                Long companyId = TenantUtils.getCurrentCompanyId();

                Company company = companyRepository.findById(companyId)
                                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

                List<Product> products = productRepository.findByCompanyId(companyId);
                List<Product> lowStockProducts = productRepository.findLowStockProductsByCompanyId(companyId);

                BigDecimal totalValue = BigDecimal.ZERO;
                for (Product product : products) {
                        if (product.getPrice() != null && product.getStock() != null) {
                                BigDecimal price = product.getPrice();
                                BigDecimal productValue = price.multiply(BigDecimal.valueOf(product.getStock()));
                                totalValue = totalValue.add(productValue);
                        }
                }

                return InventoryReportDTO.builder()
                                .companyId(companyId)
                                .companyName(company.getBusinessName())
                                .totalProducts(products.size())
                                .lowStockProducts(lowStockProducts.size())
                                .totalInventoryValue(totalValue)
                                .currency("PEN")
                                .build();
        }

        @Override
        public DashboardDTO getDashboard() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                Long userId = SecurityUtils.getCurrentUserId();
                String userRole = SecurityUtils.getCurrentUserRole();
                boolean isAdminOrManager = "ADMIN".equals(userRole) || "GERENTE".equals(userRole);

                Company company = companyRepository.findById(companyId)
                                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                
                LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
                
                List<Invoice> invoicesToday;
                if (isAdminOrManager) {
                        invoicesToday = invoiceRepository.findByCompanyIdAndDateRange(companyId, todayStart, todayEnd);
                } else {
                        invoicesToday = invoiceRepository.findByCompanyIdAndUserIdAndDateRange(companyId, userId, todayStart, todayEnd);
                }

                BigDecimal salesToday = BigDecimal.ZERO;
                for (Invoice invoice : invoicesToday) {
                        if (invoice.getTotalAmount() != null) {
                                salesToday = salesToday.add(invoice.getTotalAmount());
                        }
                }
                
                LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime monthEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
                
                List<Invoice> invoicesMonth;
                if (isAdminOrManager) {
                        invoicesMonth = invoiceRepository.findByCompanyIdAndDateRange(companyId, monthStart, monthEnd);
                } else {
                        invoicesMonth = invoiceRepository.findByCompanyIdAndUserIdAndDateRange(companyId, userId, monthStart, monthEnd);
                }

                BigDecimal salesThisMonth = BigDecimal.ZERO;
                for (Invoice invoice : invoicesMonth) {
                        if (invoice.getTotalAmount() != null) {
                                salesThisMonth = salesThisMonth.add(invoice.getTotalAmount());
                        }
                }
                
                List<Product> products = productRepository.findByCompanyId(companyId);
                List<Product> lowStockProducts = productRepository.findLowStockProductsByCompanyId(companyId);
                List<Customer> customers = customerRepository.findByCompanyId(companyId);
                List<Customer> activeCustomers = customerRepository.findByCompanyIdAndActiveTrue(companyId);
                
                BigDecimal totalPending = BigDecimal.ZERO;
                List<Invoice> pendingInvoices;
                if (isAdminOrManager) {
                        List<Invoice> issued = invoiceRepository.findByCompanyIdAndStatus(companyId, Invoice.InvoiceStatus.ISSUED);
                        List<Invoice> partial = invoiceRepository.findByCompanyIdAndStatus(companyId, Invoice.InvoiceStatus.PARTIALLY_PAID);
                        pendingInvoices = new java.util.ArrayList<>();
                        pendingInvoices.addAll(issued);
                        pendingInvoices.addAll(partial);
                } else {
                        pendingInvoices = invoiceRepository.findByCompanyIdAndUserId(companyId, userId)
                                .stream()
                                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.ISSUED || 
                                           i.getStatus() == Invoice.InvoiceStatus.PARTIALLY_PAID)
                                .toList();
                }

                for (Invoice invoice : pendingInvoices) {
                        List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(invoice.getId(), companyId);
                        BigDecimal totalPaid = payments.stream()
                                .map(Payment::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                        BigDecimal pending = invoice.getTotalAmount().subtract(totalPaid);
                        if (pending.compareTo(BigDecimal.ZERO) > 0) {
                                totalPending = totalPending.add(pending);
                        }
                }

                return DashboardDTO.builder()
                                .companyId(companyId)
                                .companyName(company.getBusinessName())
                                .salesToday(salesToday)
                                .salesThisMonth(salesThisMonth)
                                .invoicesThisMonth(invoicesMonth.size())
                                .totalProducts(products.size())
                                .lowStockProducts(lowStockProducts.size())
                                .totalCustomers(customers.size())
                                .activeCustomers(activeCustomers.size())
                                .pendingPayments(totalPending)
                                .currency("PEN")
                                .build();
        }

        @Override
        public LowStockReportDTO generateLowStockReport() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                Company company = companyRepository.findById(companyId)
                                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

                List<Product> products = productRepository.findByCompanyId(companyId);
                Map<Long, String> suppliersById = supplierRepository.findByCompanyId(companyId)
                                .stream()
                                .collect(Collectors.toMap(Supplier::getId, Supplier::getName));

                List<LowStockReportDTO.ProductStockInfo> lowStockProducts = products.stream()
                                .filter(p -> p.getStock() < 10)
                                .map(p -> LowStockReportDTO.ProductStockInfo.builder()
                                                .productId(p.getId())
                                                .supplierId(p.getSupplierId())
                                                .supplierName(p.getSupplierId() != null
                                                                ? suppliersById.getOrDefault(p.getSupplierId(), "Sin proveedor")
                                                                : "Sin proveedor")
                                                .productCode(p.getCode())
                                                .productName(p.getName())
                                                .category("General")
                                                .currentStock(p.getStock())
                                                .minimumStock(10)
                                                .status(p.getStock() < 5 ? "CRITICAL" : "LOW")
                                                .unitPrice(p.getPrice())
                                                .totalValue(p.getPrice().multiply(new BigDecimal(p.getStock())))
                                                .build())
                                .toList();

                return LowStockReportDTO.builder()
                                .reportTitle("Reporte de Stock Bajo")
                                .generatedDate(LocalDateTime.now().toString())
                                .companyName(company.getBusinessName())
                                .totalProducts(products.size())
                                .lowStockProducts(lowStockProducts.size())
                                .products(lowStockProducts)
                                .build();
        }

        @Override
        public AccountsReceivableReportDTO generateAccountsReceivableReport() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                Company company = companyRepository.findById(companyId)
                                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

                List<Invoice> issuedInvoices = invoiceRepository.findByCompanyIdAndStatus(
                                companyId, Invoice.InvoiceStatus.ISSUED);
                List<Invoice> partiallyPaidInvoices = invoiceRepository.findByCompanyIdAndStatus(
                                companyId, Invoice.InvoiceStatus.PARTIALLY_PAID);

                List<Invoice> pendingInvoices = new java.util.ArrayList<>();
                pendingInvoices.addAll(issuedInvoices);
                pendingInvoices.addAll(partiallyPaidInvoices);

                List<AccountsReceivableReportDTO.PendingInvoiceInfo> pendingList = pendingInvoices.stream()
                                .map(inv -> {
                                        List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(inv.getId(), companyId);
                                        BigDecimal totalPaid = payments.stream()
                                                        .map(Payment::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        
                                        BigDecimal pendingAmount = inv.getTotalAmount().subtract(totalPaid);
                                        
                                        int daysOverdue = 0;
                                        if (inv.getDueDate() != null) {
                                                daysOverdue = (int) java.time.temporal.ChronoUnit.DAYS.between(
                                                        inv.getDueDate().toLocalDate(),
                                                        LocalDateTime.now().toLocalDate()
                                                );
                                                daysOverdue = Math.max(0, daysOverdue); 
                                        }
                                        
                                        String status;
                                        if (daysOverdue > 30) {
                                                status = "CRITICAL";
                                        } else if (daysOverdue > 0) {
                                                status = "OVERDUE";
                                        } else {
                                                status = "CURRENT";
                                        }
                                        
                                        return AccountsReceivableReportDTO.PendingInvoiceInfo.builder()
                                                        .invoiceId(inv.getId())
                                                        .invoiceNumber(inv.getSeries() + "-" + inv.getNumber())
                                                        .customerName(inv.getCustomerName())
                                                        .customerDocument(inv.getCustomerDocument())
                                                        .customerEmail("")
                                                        .issueDate(inv.getIssueDate().toString())
                                                        .dueDate(inv.getDueDate() != null ? inv.getDueDate().toString() : "Sin fecha")
                                                        .amount(pendingAmount)
                                                        .daysOverdue(daysOverdue)
                                                        .status(status)
                                                        .build();
                                })
                                .filter(info -> info.getAmount().compareTo(BigDecimal.ZERO) > 0)
                                .toList();

                BigDecimal totalReceivable = pendingList.stream()
                                .map(AccountsReceivableReportDTO.PendingInvoiceInfo::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                BigDecimal overdueAmount = pendingList.stream()
                                .filter(info -> info.getDaysOverdue() > 0)
                                .map(AccountsReceivableReportDTO.PendingInvoiceInfo::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return AccountsReceivableReportDTO.builder()
                                .reportTitle("Cuentas por Cobrar")
                                .generatedDate(LocalDateTime.now().toString())
                                .companyName(company.getBusinessName())
                                .totalReceivable(totalReceivable)
                                .overdueAmount(overdueAmount)
                                .customerCount(pendingList.size())
                                .customers(pendingList)
                                .build();
        }

        @Override
        public TopProductsReportDTO generateTopProductsReport(LocalDateTime startDate, LocalDateTime endDate) {
                Long companyId = TenantUtils.getCurrentCompanyId();
                Company company = companyRepository.findById(companyId)
                                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

                List<Invoice> invoices = invoiceRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate);

                java.util.Map<Long, TopProductStats> productStatsMap = new java.util.HashMap<>();
                BigDecimal totalRevenue = BigDecimal.ZERO;

                for (Invoice invoice : invoices) {
                        List<com.cibertec.sistema_facturacion_g4.domain.entities.InvoiceDetail> details = invoiceDetailRepository
                                        .findByInvoiceId(invoice.getId());

                        for (com.cibertec.sistema_facturacion_g4.domain.entities.InvoiceDetail detail : details) {
                                Long productId = detail.getProductId();
                                if (productId == null)
                                        continue;

                                TopProductStats stats = productStatsMap.getOrDefault(productId, new TopProductStats());
                                stats.setProductId(productId);
                                stats.setProductCode(detail.getProductCode());
                                stats.setProductName(detail.getProductName());

                                Integer qty = detail.getQuantity() != null ? detail.getQuantity() : 0;
                                BigDecimal price = detail.getUnitPrice() != null ? detail.getUnitPrice()
                                                : BigDecimal.ZERO;
                                BigDecimal detailTotal = detail.getTotalAmount() != null ? detail.getTotalAmount()
                                                : BigDecimal.ZERO;

                                stats.setQuantitySold(stats.getQuantitySold() + qty);
                                stats.setRevenue(stats.getRevenue().add(detailTotal));
                                stats.setUnitPrice(price);

                                productStatsMap.put(productId, stats);
                                totalRevenue = totalRevenue.add(detailTotal);
                        }
                }

                List<TopProductStats> sortedStats = productStatsMap.values().stream()
                                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
                                .limit(5)
                                .toList();

                int ranking = 1;
                List<TopProductsReportDTO.TopProductInfo> topProducts = new java.util.ArrayList<>();
                for (TopProductStats stats : sortedStats) {
                        double percentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                                        ? stats.getRevenue().divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                                        .multiply(new BigDecimal("100")).doubleValue()
                                        : 0.0;

                        topProducts.add(TopProductsReportDTO.TopProductInfo.builder()
                                        .productId(stats.getProductId())
                                        .productCode(stats.getProductCode())
                                        .productName(stats.getProductName())
                                        .category("General")
                                        .quantitySold(stats.getQuantitySold())
                                        .unitPrice(stats.getUnitPrice())
                                        .totalRevenue(stats.getRevenue())
                                        .ranking(ranking++)
                                        .percentageOfTotal(percentage)
                                        .build());
                }

                int totalProductsSold = productStatsMap.values().stream()
                                .mapToInt(s -> s.getQuantitySold())
                                .sum();

                return TopProductsReportDTO.builder()
                                .reportTitle("Productos Más Vendidos")
                                .generatedDate(LocalDateTime.now().toString())
                                .companyName(company.getBusinessName())
                                .period(startDate.toLocalDate() + " a " + endDate.toLocalDate())
                                .totalProductsSold(totalProductsSold)
                                .totalRevenue(totalRevenue)
                                .topProducts(topProducts)
                                .build();
        }

        @Override
        public java.util.Map<String, Object> getSalesChartData(int days) {
                Long companyId = TenantUtils.getCurrentCompanyId();
                LocalDateTime endDate = LocalDateTime.now();
                LocalDateTime startDate = endDate.minusDays(days - 1);

                List<Invoice> invoices;
                if (isAdminOrManager()) {
                        invoices = invoiceRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate);
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        invoices = invoiceRepository.findByCompanyIdAndUserIdAndDateRange(companyId, userId, startDate, endDate);
                }

                java.util.Map<String, BigDecimal> dailySales = new java.util.LinkedHashMap<>();
                for (int i = 0; i < days; i++) {
                        LocalDateTime date = startDate.plusDays(i);
                        String dateKey = date.toLocalDate().toString();
                        dailySales.put(dateKey, BigDecimal.ZERO);
                }

                for (Invoice invoice : invoices) {
                        if (invoice.getIssueDate() != null && invoice.getTotalAmount() != null) {
                                String dateKey = invoice.getIssueDate().toLocalDate().toString();
                                dailySales.merge(dateKey, invoice.getTotalAmount(), BigDecimal::add);
                        }
                }

                java.util.Map<String, Object> chartData = new java.util.HashMap<>();
                chartData.put("labels", dailySales.keySet().toArray(new String[0]));
                chartData.put("data", dailySales.values().stream()
                                .map(v -> v.doubleValue())
                                .toArray(Double[]::new));
                chartData.put("currency", "PEN");
                chartData.put("title", "Ventas de los últimos " + days + " días");

                return chartData;
        }

        @Override
        public java.util.Map<String, Object> getSystemAlerts() {
                Long companyId = TenantUtils.getCurrentCompanyId();

                List<Product> lowStockProducts = productRepository.findLowStockProductsByCompanyId(companyId);

                List<Invoice> overdueInvoices;
                if (isAdminOrManager()) {
                        overdueInvoices = invoiceRepository.findByCompanyId(companyId)
                                        .stream()
                                        .filter(i -> {
                                                if (i.getDueDate() == null || !i.getDueDate().isBefore(LocalDateTime.now())) {
                                                        return false;
                                                }
                                                if (i.getStatus() == Invoice.InvoiceStatus.PAID || 
                                                    i.getStatus() == Invoice.InvoiceStatus.CANCELLED ||
                                                    i.getStatus() == Invoice.InvoiceStatus.DRAFT) {
                                                        return false;
                                                }
                                                
                                                List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(i.getId(), companyId);
                                                BigDecimal totalPaid = payments.stream()
                                                        .map(Payment::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                                
                                                BigDecimal pending = i.getTotalAmount().subtract(totalPaid);
                                                return pending.compareTo(BigDecimal.ZERO) > 0;
                                        })
                                        .toList();
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        overdueInvoices = invoiceRepository.findByCompanyIdAndUserId(companyId, userId)
                                        .stream()
                                        .filter(i -> {
                                                if (i.getDueDate() == null || !i.getDueDate().isBefore(LocalDateTime.now())) {
                                                        return false;
                                                }
                                                if (i.getStatus() == Invoice.InvoiceStatus.PAID || 
                                                    i.getStatus() == Invoice.InvoiceStatus.CANCELLED ||
                                                    i.getStatus() == Invoice.InvoiceStatus.DRAFT) {
                                                        return false;
                                                }
                                                
                                                List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(i.getId(), companyId);
                                                BigDecimal totalPaid = payments.stream()
                                                        .map(Payment::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                                
                                                BigDecimal pending = i.getTotalAmount().subtract(totalPaid);
                                                return pending.compareTo(BigDecimal.ZERO) > 0;
                                        })
                                        .toList();
                }

                long newCustomersToday = customerRepository.findByCompanyId(companyId)
                                .stream()
                                .filter(c -> c.getCreatedAt() != null &&
                                                c.getCreatedAt().toLocalDate()
                                                                .equals(LocalDateTime.now().toLocalDate()) &&
                                                !c.getIsGeneric())
                                .count();

                java.util.Map<String, Object> alerts = new java.util.HashMap<>();
                alerts.put("lowStockCount", lowStockProducts.size());
                alerts.put("overdueInvoices", overdueInvoices.size());
                alerts.put("newCustomersToday", (int) newCustomersToday);
                alerts.put("pendingTasks", 0);

                return alerts;
        }

        private boolean isAdminOrManager() {
                String role = SecurityUtils.getCurrentUserRole();
                return "ADMIN".equals(role) || "GERENTE".equals(role);
        }

        public BigDecimal getSalesToday() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

                List<Invoice> invoices;
                if (isAdminOrManager()) {
                        invoices = invoiceRepository.findByCompanyIdAndDateRange(companyId, todayStart, todayEnd);
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        invoices = invoiceRepository.findByCompanyIdAndUserIdAndDateRange(companyId, userId, todayStart, todayEnd);
                }

                BigDecimal total = BigDecimal.ZERO;
                for (Invoice invoice : invoices) {
                        if (invoice.getTotalAmount() != null) {
                                total = total.add(invoice.getTotalAmount());
                        }
                }
                return total;
        }

        public BigDecimal getSalesMonth() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime monthEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

                List<Invoice> invoices;
                if (isAdminOrManager()) {
                        invoices = invoiceRepository.findByCompanyIdAndDateRange(companyId, monthStart, monthEnd);
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        invoices = invoiceRepository.findByCompanyIdAndUserIdAndDateRange(companyId, userId, monthStart, monthEnd);
                }

                BigDecimal total = BigDecimal.ZERO;
                for (Invoice invoice : invoices) {
                        if (invoice.getTotalAmount() != null) {
                                total = total.add(invoice.getTotalAmount());
                        }
                }
                return total;
        }

        public int getCustomersTotal() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                
                if (isAdminOrManager()) {
                        return customerRepository.findByCompanyId(companyId).size();
                } else {
                        return customerRepository.findByCompanyId(companyId).size();
                }
        }

        public int getProductsStock() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                return productRepository.findByCompanyId(companyId).size();
        }

        public int getInvoicesMonth() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime monthEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

                List<Invoice> invoices;
                if (isAdminOrManager()) {
                        invoices = invoiceRepository.findByCompanyIdAndDateRange(companyId, monthStart, monthEnd);
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        invoices = invoiceRepository.findByCompanyIdAndUserIdAndDateRange(companyId, userId, monthStart, monthEnd);
                }

                return invoices.size();
        }

        public BigDecimal getPendingPayments() {
                Long companyId = TenantUtils.getCurrentCompanyId();

                List<Invoice> pendingInvoices;
                if (isAdminOrManager()) {
                        List<Invoice> issued = invoiceRepository.findByCompanyIdAndStatus(companyId, Invoice.InvoiceStatus.ISSUED);
                        List<Invoice> partial = invoiceRepository.findByCompanyIdAndStatus(companyId, Invoice.InvoiceStatus.PARTIALLY_PAID);
                        pendingInvoices = new java.util.ArrayList<>();
                        pendingInvoices.addAll(issued);
                        pendingInvoices.addAll(partial);
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        pendingInvoices = invoiceRepository.findByCompanyIdAndUserId(companyId, userId)
                                .stream()
                                .filter(i -> i.getStatus() == Invoice.InvoiceStatus.ISSUED || 
                                           i.getStatus() == Invoice.InvoiceStatus.PARTIALLY_PAID)
                                .toList();
                }

                BigDecimal totalPending = BigDecimal.ZERO;
                for (Invoice invoice : pendingInvoices) {
                        List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(invoice.getId(), companyId);
                        BigDecimal totalPaid = payments.stream()
                                .map(Payment::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                        BigDecimal pending = invoice.getTotalAmount().subtract(totalPaid);
                        if (pending.compareTo(BigDecimal.ZERO) > 0) {
                                totalPending = totalPending.add(pending);
                        }
                }

                return totalPending;
        }

        public int getActiveCustomers() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                return customerRepository.findByCompanyIdAndActiveTrue(companyId).size();
        }

        public int getLowStock() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                return productRepository.findLowStockProductsByCompanyId(companyId).size();
        }

        public java.util.Map<String, Object> getSalesChart() {
                Long companyId = TenantUtils.getCurrentCompanyId();
                int days = 30;
                LocalDateTime endDate = LocalDateTime.now();
                LocalDateTime startDate = endDate.minusDays(days - 1);

                List<Invoice> invoices;
                if (isAdminOrManager()) {
                        invoices = invoiceRepository.findByCompanyIdAndDateRange(companyId, startDate, endDate);
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        invoices = invoiceRepository.findByCompanyIdAndUserIdAndDateRange(companyId, userId, startDate, endDate);
                }

                java.util.Map<String, BigDecimal> dailySales = new java.util.LinkedHashMap<>();
                for (int i = 0; i < days; i++) {
                        LocalDateTime date = startDate.plusDays(i);
                        String dateKey = date.toLocalDate().toString();
                        dailySales.put(dateKey, BigDecimal.ZERO);
                }

                for (Invoice invoice : invoices) {
                        if (invoice.getIssueDate() != null && invoice.getTotalAmount() != null) {
                                String dateKey = invoice.getIssueDate().toLocalDate().toString();
                                dailySales.merge(dateKey, invoice.getTotalAmount(), BigDecimal::add);
                        }
                }

                java.util.Map<String, Object> chartData = new java.util.HashMap<>();
                chartData.put("labels", dailySales.keySet().toArray(new String[0]));
                chartData.put("data", dailySales.values().stream()
                                .map(v -> v.doubleValue())
                                .toArray(Double[]::new));
                chartData.put("currency", "PEN");
                chartData.put("title", "Ventas de los últimos " + days + " días");

                return chartData;
        }

        public java.util.Map<String, Object> getDashboardAlerts() {
                Long companyId = TenantUtils.getCurrentCompanyId();

                List<Product> lowStockProducts = productRepository.findLowStockProductsByCompanyId(companyId);

                List<Invoice> overdueInvoices;
                if (isAdminOrManager()) {
                        overdueInvoices = invoiceRepository.findByCompanyId(companyId)
                                .stream()
                                .filter(i -> {
                                        if (i.getDueDate() == null || !i.getDueDate().isBefore(LocalDateTime.now())) {
                                                return false;
                                        }
                                        if (i.getStatus() == Invoice.InvoiceStatus.PAID || 
                                            i.getStatus() == Invoice.InvoiceStatus.CANCELLED ||
                                            i.getStatus() == Invoice.InvoiceStatus.DRAFT) {
                                                return false;
                                        }
                                        
                                        List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(i.getId(), companyId);
                                        BigDecimal totalPaid = payments.stream()
                                                .map(Payment::getAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        
                                        BigDecimal pending = i.getTotalAmount().subtract(totalPaid);
                                        return pending.compareTo(BigDecimal.ZERO) > 0;
                                })
                                .toList();
                } else {
                        Long userId = SecurityUtils.getCurrentUserId();
                        overdueInvoices = invoiceRepository.findByCompanyIdAndUserId(companyId, userId)
                                .stream()
                                .filter(i -> {
                                        if (i.getDueDate() == null || !i.getDueDate().isBefore(LocalDateTime.now())) {
                                                return false;
                                        }
                                        if (i.getStatus() == Invoice.InvoiceStatus.PAID || 
                                            i.getStatus() == Invoice.InvoiceStatus.CANCELLED ||
                                            i.getStatus() == Invoice.InvoiceStatus.DRAFT) {
                                                return false;
                                        }
                                        
                                        List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(i.getId(), companyId);
                                        BigDecimal totalPaid = payments.stream()
                                                .map(Payment::getAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        
                                        BigDecimal pending = i.getTotalAmount().subtract(totalPaid);
                                        return pending.compareTo(BigDecimal.ZERO) > 0;
                                })
                                .toList();
                }

                long newCustomersToday = customerRepository.findByCompanyId(companyId)
                        .stream()
                        .filter(c -> c.getCreatedAt() != null &&
                                        c.getCreatedAt().toLocalDate()
                                                        .equals(LocalDateTime.now().toLocalDate()) &&
                                        !c.getIsGeneric())
                        .count();

                java.util.Map<String, Object> alerts = new java.util.HashMap<>();
                alerts.put("lowStockCount", lowStockProducts.size());
                alerts.put("overdueInvoices", overdueInvoices.size());
                alerts.put("newCustomersToday", (int) newCustomersToday);
                alerts.put("pendingTasks", 0);

                return alerts;
        }
}

