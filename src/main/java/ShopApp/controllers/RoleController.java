/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.models.Role;
import ShopApp.responses.ListResponse;
import ShopApp.services.RoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author mac
 */
@Controller
@RequestMapping("${api.prefix}/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    
    @GetMapping("/admin")
    private ResponseEntity<ListResponse> getAllAdminReole(@RequestParam("page") int page, @RequestParam("limit") int limit){
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("id").ascending());
        
        Page<Role> rolePage = roleService.getAllRoleAdmin(pageRequest);
        // tong trang
        int totalPages = rolePage.getTotalPages();
        
        List<Role> categories = rolePage.getContent();
        
        // Create response
        ListResponse<Role> roleListResponse = ListResponse.<Role>builder()
                .items(categories)
                .page(page)
                .totalPages(totalPages)
                .build();

        return ResponseEntity.ok(roleListResponse);
    }
    
    @GetMapping("/login")
    private ResponseEntity<?> getAllRole(){
        List<Role> listrole = roleService.getAllRole();
        return ResponseEntity.ok(listrole);
    }
}
