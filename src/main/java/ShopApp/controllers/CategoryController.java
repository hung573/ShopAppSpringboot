    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.CategoryDTO;
import ShopApp.models.Category;
import ShopApp.responses.ListResponse;
import ShopApp.services.Category.CategoryService;
import ShopApp.components.LocalizationUtils;
import ShopApp.services.Category.ICategoryRedisService;
import ShopApp.services.Category.ICategoryService;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.Category.CategoryRedisService;
import ShopApp.utils.MessageKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import org.springframework.web.servlet.LocaleResolver;

/**
 *
 * @author mac
 */
@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryServiec;
    private final LocalizationUtils localizationUtils;
    private final CategoryRedisService redisService;
    
    @GetMapping("")
    private ResponseEntity<ListResponse> getAllCategory(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) throws JsonProcessingException{
        
        String name = "all_categoris";
        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending());
        List<Category> categories = redisService.getAllItems(keyword, pageRequest, name);
        if (categories == null || categories.isEmpty()) {
            Page<Category> categoryPage = categoryServiec.getAllCategories(pageRequest);
        // tong trang
            int totalPages = categoryPage.getTotalPages();
            categories = categoryPage.getContent();
            redisService.saveAllItems(categories, keyword, pageRequest, name);
        }
        
        // Create response
        ListResponse<Category> categoryListResponse = ListResponse.<Category>builder()
                .items(categories)
                .page(page)
                .totalPages(0)
                .build();

        return ResponseEntity.ok(categoryListResponse);
    }
    @GetMapping("/{id}")
    private ResponseEntity<ObjectResponse> getIdCategory(@PathVariable("id") long idCategory){
        try {
            Category category = categoryServiec.getCategoryById(idCategory);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                    .items(category)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND, e.getMessage()))
                    .build());
        }
        
    }
    
    @PostMapping("/add")
    private ResponseEntity<ObjectResponse> addCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result){
        
        try {
            if (result.hasErrors()) {
            List<String> errormessage = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                        .build());
            }
            Category category = categoryServiec.createCategory(categoryDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message("yes")
                    .items(category)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    
    @PutMapping("/update/{id}")
    private ResponseEntity<ObjectResponse> updateCategory(
            @PathVariable("id") long idCategory,
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result){
        
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        ObjectResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage.get(0)))
                            .build());
            }
            Category category = categoryServiec.updateCategory(idCategory, categoryDTO);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                    .items(category)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                        ObjectResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                            .build());
        }
    }
    
    @DeleteMapping("/{id}")
    private ResponseEntity<MessageResponse> deleteCategory(
            @PathVariable("id") long idCategory){
        try {
            categoryServiec.deleteCategory(idCategory);
            return ResponseEntity.ok(MessageResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY))
                .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                        MessageResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                            .build());
        }
    }
}
