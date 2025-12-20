package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.ModuleDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.PermissionDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.RolePermissionsDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.PermissionService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Module;
import com.cibertec.sistema_facturacion_g4.domain.entities.Permission;
import com.cibertec.sistema_facturacion_g4.domain.entities.RolePermission;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ModuleRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.PermissionRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.RolePermissionRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final ModuleRepository moduleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<ModuleDTO> getAllModules() {
        return moduleRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModuleDTO> getModulesHierarchy() {
        List<Module> parents = moduleRepository.findByParentIdIsNullAndActiveTrue();
        return parents.stream()
                .map(this::convertToDTOWithChildren)
                .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public ModuleDTO getModuleByCode(String code) {
        return moduleRepository.findByCode(code)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findByActiveTrue().stream()
                .map(this::convertPermissionToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionsByModule(Long moduleId) {
        return permissionRepository.findByModuleIdAndActiveTrue(moduleId).stream()
                .map(this::convertPermissionToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDTO> getPermissionsByRole(String roleName) {
        return permissionRepository.findByRoleName(roleName).stream()
                .map(this::convertPermissionToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionDTO getPermissionByCode(String code) {
        return permissionRepository.findByCode(code)
                .map(this::convertPermissionToDTO)
                .orElse(null);
    }

    @Override
    public RolePermissionsDTO getRolePermissions(String roleName) {
        var role = roleRepository.findByName(roleName).orElse(null);
        var permissions = getPermissionsByRole(roleName);
        var permissionIds = permissions.stream()
                .map(PermissionDTO::getId)
                .collect(Collectors.toList());

        return RolePermissionsDTO.builder()
                .roleName(roleName)
                .roleDescription(role != null ? role.getDescription() : "")
                .permissionIds(permissionIds)
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional
    public void assignPermissionsToRole(String roleName, List<Long> permissionIds) {
        rolePermissionRepository.deleteByRoleName(roleName);

        permissionIds.forEach(permissionId -> {
            RolePermission rp = RolePermission.builder()
                    .roleName(roleName)
                    .permissionId(permissionId)
                    .build();
            rolePermissionRepository.save(rp);
        });
    }

    @Override
    @Transactional
    public void removePermissionFromRole(String roleName, Long permissionId) {
        rolePermissionRepository.deleteByRoleNameAndPermissionId(roleName, permissionId);
    }

    @Override
    public boolean hasPermission(String roleName, String permissionCode) {
        List<Permission> permissions = permissionRepository.findByRoleName(roleName);
        return permissions.stream()
                .anyMatch(p -> p.getCode().equals(permissionCode));
    }

    @Override
    public List<String> getUserPermissions(String roleName) {
        return permissionRepository.findByRoleName(roleName).stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    private ModuleDTO convertToDTO(Module module) {
        return ModuleDTO.builder()
                .id(module.getId())
                .code(module.getCode())
                .name(module.getName())
                .description(module.getDescription())
                .icon(module.getIcon())
                .route(module.getRoute())
                .parentId(module.getParentId())
                .displayOrder(module.getDisplayOrder())
                .active(module.getActive())
                .build();
    }

    private ModuleDTO convertToDTOWithChildren(Module module) {
        ModuleDTO dto = convertToDTO(module);
        
        List<PermissionDTO> permissions = getPermissionsByModule(module.getId());
        dto.setPermissions(permissions);
        
        List<Module> children = moduleRepository.findByParentIdAndActiveTrue(module.getId());
        if (!children.isEmpty()) {
            dto.setChildren(children.stream()
                    .map(this::convertToDTOWithChildren)
                    .sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                    .collect(Collectors.toList()));
        } else {
            dto.setChildren(new ArrayList<>());
        }
        
        return dto;
    }

    private PermissionDTO convertPermissionToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .name(permission.getName())
                .description(permission.getDescription())
                .moduleId(permission.getModuleId())
                .action(permission.getAction())
                .active(permission.getActive())
                .build();
    }
}
