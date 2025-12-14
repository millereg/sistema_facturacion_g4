package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.CategoryService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Category;
import com.cibertec.sistema_facturacion_g4.application.dto.CategoryDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.CategoryMapper;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CategoryRepository;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDTO save(CategoryDTO dto) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Category category = categoryMapper.toEntity(dto);
        category.setCompanyId(companyId);
        if (category.getActive() == null) {
            category.setActive(true);
        }
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDTO(saved);
    }

    @Override
    public Optional<CategoryDTO> findById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return categoryRepository.findByIdAndCompanyId(id, companyId)
                .map(categoryMapper::toDTO);
    }

    @Override
    public List<CategoryDTO> findAll() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return categoryRepository.findByCompanyId(companyId)
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> findAllActive() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return categoryRepository.findByCompanyIdAndActiveTrue(companyId)
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Category category = categoryRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Categoría no encontrada"));
        categoryRepository.deleteById(category.getId());
    }

    @Override
    public void activateCategory(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        categoryRepository.findByIdAndCompanyId(id, companyId).ifPresent(category -> {
            category.setActive(true);
            categoryRepository.save(category);
        });
    }

    @Override
    public void deactivateCategory(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        categoryRepository.findByIdAndCompanyId(id, companyId).ifPresent(category -> {
            category.setActive(false);
            categoryRepository.save(category);
        });
    }
}