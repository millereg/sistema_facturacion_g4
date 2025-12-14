package com.cibertec.sistema_facturacion_g4.shared.context;

public class TenantContext {
    private static final ThreadLocal<Long> currentCompanyId = new ThreadLocal<>();

    public static void setCurrentCompanyId(Long companyId) {
        currentCompanyId.set(companyId);
    }

    public static Long getCurrentCompanyId() {
        return currentCompanyId.get();
    }

    public static void clear() {
        currentCompanyId.remove();
    }

    public static boolean isSet() {
        return currentCompanyId.get() != null;
    }
}
