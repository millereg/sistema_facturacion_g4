package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.ProductService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.ProductMapper;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.services.StockDomainService;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import com.cibertec.sistema_facturacion_g4.shared.utils.ValidationUtils;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StockDomainService stockDomainService;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, StockDomainService stockDomainService,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.stockDomainService = stockDomainService;
        this.productMapper = productMapper;
    }

    public ProductDTO save(ProductDTO dto) {
        Long companyId = TenantUtils.getCurrentCompanyId();

        if (dto.getPrice() != null) {
            if (!ValidationUtils.isValidAmount(dto.getPrice())) {
                throw new BusinessException("Precio inválido: debe ser mayor o igual a 0");
            }
        }

        Product product = productMapper.toEntity(dto);
        product.setCompanyId(companyId);

        Product prod = productRepository.save(product);
        if (stockDomainService.isStockLow(prod)) {
            int sugerido = stockDomainService.getSuggestedReorder(prod);
            log.warn("¡ALERTA! Stock bajo para: {} - Stock actual: {} - Sugerido reabastecer: {} unidades",
                    prod.getName(), prod.getStock(), sugerido);
        }

        return productMapper.toDTO(prod);
    }

    public Optional<ProductDTO> findById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return productRepository.findByIdAndCompanyId(id, companyId).map(productMapper::toDTO);
    }

    public List<ProductDTO> findAll() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return productRepository.findByCompanyId(companyId).stream().map(productMapper::toDTO).toList();
    }

    @Override
    public void deleteById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Product product = productRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));
        productRepository.delete(product);
    }
}
