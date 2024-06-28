/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.CategoryDTO;
import ShopApp.models.Category;
import ShopApp.services.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

/**
 *
 * @author mac
 */
@Controller
@RequestMapping("${api.prefix}/categories")
@ RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryServiec;
    
    @GetMapping("")
    private ResponseEntity<List<Category>> getAllCategory(@RequestParam("page") int page, @RequestParam("limit") int limit){
        
        List<Category> categories = categoryServiec.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @PostMapping("/add")
    private ResponseEntity<?> addCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result){
        
        if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errormessage);
        }
        categoryServiec.createCategory(categoryDTO);
        return ResponseEntity.ok("Thêm mới thành công Category");
    }
    
    @GetMapping("/{id}")
    private ResponseEntity<Category> getIdCategory(@PathVariable("id") long idCategory){
        Category category = categoryServiec.getCategoryById(idCategory);
        return ResponseEntity.ok(category);
    }
    
    @PutMapping("/update/{id}")
    private ResponseEntity<String> updateCategory(@PathVariable("id") long idCategory,@ Valid @RequestBody CategoryDTO categoryDTO){
        categoryServiec.updateCategory(idCategory, categoryDTO);
        return ResponseEntity.ok("Cập nhật thành công Category Id: " + idCategory);
    }
    
    @DeleteMapping("/{id}")
    private ResponseEntity<String> deleteCategory(@PathVariable("id") long idCategory){
        categoryServiec.deleteCategory(idCategory);
        return ResponseEntity.ok("Xoá thành công Category Id: "+ idCategory);
    }
}
