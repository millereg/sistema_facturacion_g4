package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.CustomerService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Customer;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.CustomerMapper;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CustomerRepository;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import com.cibertec.sistema_facturacion_g4.shared.utils.ValidationUtils;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerServiceImpl implements CustomerService {
    
    private CustomerRepository customerRepo;
    private CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepo = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerDTO save(CustomerDTO dto) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            if (!ValidationUtils.isValidEmail(dto.getEmail())) {
                throw new BusinessException("Email inválido: " + dto.getEmail());
            }
            dto.setEmail(ValidationUtils.normalizeEmail(dto.getEmail()));
        }
        
        Customer customer;
        
        if (dto.getId() != null) {
            customer = customerRepo.findByIdAndCompanyId(dto.getId(), companyId)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));
            
            if (Boolean.TRUE.equals(customer.getIsGeneric())) {
                throw new BusinessException("No se puede editar el cliente genérico");
            }
            
            if (dto.getType() != null) customer.setType(dto.getType());
            if (dto.getDocumentType() != null) customer.setDocumentType(dto.getDocumentType());
            if (dto.getDocumentNumber() != null) customer.setDocumentNumber(dto.getDocumentNumber());
            if (dto.getBusinessName() != null) customer.setBusinessName(dto.getBusinessName());
            if (dto.getFirstName() != null) customer.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) customer.setLastName(dto.getLastName());
            if (dto.getEmail() != null) customer.setEmail(dto.getEmail());
            if (dto.getPhone() != null) customer.setPhone(dto.getPhone());
            if (dto.getAddress() != null) customer.setAddress(dto.getAddress());
            if (dto.getActive() != null) customer.setActive(dto.getActive());
        } else {
            customer = customerMapper.toEntity(dto);
            customer.setCompanyId(companyId);
            customer.setIsGeneric(false);
        }
        
        Customer resultado = customerRepo.save(customer);
        return customerMapper.toDTO(resultado);
    }

    @Override
    public Optional<CustomerDTO> findById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return customerRepo.findByIdAndCompanyId(id, companyId)
                .map(customerMapper::toDTO);
    }

    @Override
    public List<CustomerDTO> findAll() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        List<Customer> clientes = customerRepo.findByCompanyId(companyId);
        return clientes.stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Customer customer = customerRepo.findByIdAndCompanyId(id, companyId)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado"));
        
        if (Boolean.TRUE.equals(customer.getIsGeneric())) {
            throw new BusinessException("No se puede eliminar el cliente genérico");
        }
        
        customerRepo.deleteById(customer.getId());
    }

    @Override
    public Optional<CustomerDTO> getGenericCustomer() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return customerRepo.findByCompanyIdAndIsGenericTrue(companyId)
                .map(customerMapper::toDTO);
    }
}
