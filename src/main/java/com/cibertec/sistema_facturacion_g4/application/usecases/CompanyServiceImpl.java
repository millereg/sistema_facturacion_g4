package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.CompanyService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Company;
import com.cibertec.sistema_facturacion_g4.application.dto.CompanyDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.CompanyMapper;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CompanyRepository;
import com.cibertec.sistema_facturacion_g4.shared.utils.ValidationUtils;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Override

    public CompanyDTO save(CompanyDTO dto) {
        if (dto.getTaxId() != null && !dto.getTaxId().isEmpty()) {
            if (!ValidationUtils.isValidRUC(dto.getTaxId())) {
                throw new BusinessException("RUC inválido: " + dto.getTaxId());
            }
            dto.setTaxId(ValidationUtils.normalizeRUC(dto.getTaxId()));
        }
        
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (!ValidationUtils.isValidEmail(dto.getEmail())) {
                throw new BusinessException("Email inválido: " + dto.getEmail());
            }
            dto.setEmail(ValidationUtils.normalizeEmail(dto.getEmail()));
        }
        
        Company company = companyMapper.toEntity(dto);
        Company temp = companyRepository.save(company);
        return companyMapper.toDTO(temp);
    }

    @Override

    public Optional<CompanyDTO> findById(Long id) {
        return companyRepository.findById(id).map(companyMapper::toDTO);
    }

    @Override

    public List<CompanyDTO> findAll() {
        return companyRepository.findAll().stream().map(companyMapper::toDTO).toList();
    }

    @Override
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }
}
