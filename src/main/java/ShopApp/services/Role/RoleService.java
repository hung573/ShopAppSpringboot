/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package ShopApp.services.Role;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.RoleDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Role;
import ShopApp.repositories.RoleRepository;
import ShopApp.utils.MessageKey;
import jakarta.transaction.Transactional;
import java.io.FileNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService{
    
    private final RoleRepository roleRepository;
    private final LocalizationUtils localizationUtils;

    
    @Override
    public Page<Role> getAllRoleAdmin(PageRequest pageRequest) {
        return roleRepository.findAll(pageRequest);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Role addRole(RoleDTO roleDTO) throws Exception {
        Role role = roleRepository.findByName(roleDTO.getName());
        if (role != null) {
            throw new Exception("Role này đã tồn tại trong hệ thống");
        }
        
        Role newRole = Role.builder()
                .name(roleDTO.getName().toUpperCase())
                .build();
        return roleRepository.save(newRole);
    }

    @Override
    @Transactional
    public Role updateRole(long roleId,RoleDTO roleDTO) throws Exception {
        Role role = roleRepository.findByName(roleDTO.getName());
        String name = roleDTO.getName().toUpperCase();
        if (name != null && !name.isEmpty() && name != " " && !name.equals(role.getName())) {
            role.setName(roleDTO.getName().toUpperCase());
            roleRepository.save(role);
        }
        return role;
    }

    @Override
    public Role getRoleByid(long id) throws Exception {
        return roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
    }

    @Override
    public void deleteRole(long roleId, boolean acitve) throws Exception {
        Role role = getRoleByid(roleId);
        role.setActive(acitve);
        roleRepository.save(role);
        
    }

}
