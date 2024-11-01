/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.RoleDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Role;
import ShopApp.responses.ListResponse;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.Role.RoleService;
import ShopApp.utils.MessageKey;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mac
 */
@RestController
@RequestMapping("${api.prefix}/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final LocalizationUtils localizationUtils;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ListResponse> getAllAdminReole(@RequestParam("page") int page, @RequestParam("limit") int limit) {
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
    public ResponseEntity<?> getAllRole() {
        List<Role> listrole = roleService.getAllRole();
        return ResponseEntity.ok(listrole);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addRole(
            @Valid @RequestBody RoleDTO roleDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                    .build());
        }
        Role role = roleService.addRole(roleDTO);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message("yes")
                .items(role)
                .build());
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateRole(
            @PathVariable("id") long idRole,
            @Valid @RequestBody RoleDTO roleDTO,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                    .build());
        }
        Role role = roleService.updateRole(idRole, roleDTO);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message("yes")
                .items(role)
                .build());
    }

    @DeleteMapping("/delete/{id}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteRole(
            @PathVariable("id") long roleId,
            @PathVariable("active") int active) throws Exception {
        roleService.deleteRole(roleId, active > 0);
        String message = active > 0 ? "Successfully business the product." : localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY);
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .build());
    }

}
