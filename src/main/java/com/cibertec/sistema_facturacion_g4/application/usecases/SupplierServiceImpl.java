package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.SupplierService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Supplier;
import com.cibertec.sistema_facturacion_g4.application.dto.SupplierDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.SupplierMapper;
import com.cibertec.sistema_facturacion_g4.domain.repositories.SupplierRepository;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierDTO save(SupplierDTO dto) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setCompanyId(companyId);
        if (supplier.getActive() == null) {
            supplier.setActive(true);
        }
        Supplier saved = supplierRepository.save(supplier);
        return supplierMapper.toDTO(saved);
    }

    @Override
    public Optional<SupplierDTO> findById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return supplierRepository.findByIdAndCompanyId(id, companyId)
                .map(supplierMapper::toDTO);
    }

    @Override
    public List<SupplierDTO> findAll() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return supplierRepository.findByCompanyId(companyId)
                .stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierDTO> findAllActive() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return supplierRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Supplier supplier = supplierRepository.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new BusinessException("Proveedor no encontrado"));
        supplierRepository.deleteById(supplier.getId());
    }

    @Override
    public void activateSupplier(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Supplier supplier = supplierRepository.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new BusinessException("Proveedor no encontrado"));
        supplier.setActive(true);
        supplierRepository.save(supplier);
    }

    @Override
    public void deactivateSupplier(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Supplier supplier = supplierRepository.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new BusinessException("Proveedor no encontrado"));
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }
}
