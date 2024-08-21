/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.Role;

import ShopApp.dtos.RoleDTO;
import ShopApp.models.Role;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IRoleService {
    Page<Role> getAllRoleAdmin(PageRequest pageRequest);
    List<Role> getAllRole();
    Role getRoleByid(long id) throws Exception;
    Role addRole(RoleDTO roleDTO) throws Exception;
    Role updateRole(long roleId,RoleDTO roleDTO) throws Exception;
    void deleteRole(long roleId, boolean acitve) throws Exception;
}
