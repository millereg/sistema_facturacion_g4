package com.cibertec.sistema_facturacion_g4.shared.utils;

import com.cibertec.sistema_facturacion_g4.shared.context.TenantContext;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;

public class TenantUtils {
    public static Long getCurrentCompanyId() {
        Long companyId = TenantContext.getCurrentCompanyId();
        if (companyId == null) {
            throw new BusinessException("No hay contexto de empresa establecido");
        }
        return companyId;
    }

    public static boolean belongsToCurrentCompany(Long companyId) {
        Long currentCompanyId = TenantContext.getCurrentCompanyId();
        return currentCompanyId != null && currentCompanyId.equals(companyId);
    }

    public static void validateCompanyAccess(Long entityCompanyId) {
        Long currentCompanyId = getCurrentCompanyId();
        if (!currentCompanyId.equals(entityCompanyId)) {
            throw new BusinessException("No tiene permiso para acceder a recursos de otra empresa");
        }
    }
}
