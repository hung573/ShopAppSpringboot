/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package ShopApp.services;

import ShopApp.iservices.IRoleService;
import ShopApp.models.Role;
import ShopApp.repositories.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor

public class RoleService implements IRoleService{
    
    private final RoleRepository roleRepository;
    
    @Override
    public Page<Role> getAllRoleAdmin(PageRequest pageRequest) {
        return roleRepository.findAll(pageRequest);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

}
