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
import ShopApp.components.converters.CategoryMessageConverter;
import ShopApp.responses.MessageResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.Category.CategoryRedisService;
import ShopApp.utils.MessageKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryServiec;
    private final LocalizationUtils localizationUtils;
    private final CategoryRedisService redisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    
    @GetMapping("")
    public ResponseEntity<ListResponse> getAllCategory(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) throws JsonProcessingException {

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
    public ResponseEntity<?> getIdCategory(@PathVariable("id") long idCategory) throws Exception {
        Category category = categoryServiec.getCategoryById(idCategory);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                .items(category)
                .build());
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
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
        Category category = categoryServiec.createCategory(categoryDTO);
        this.kafkaTemplate.send("insert-a-category", category);//producer
        this.kafkaTemplate.setMessageConverter(new CategoryMessageConverter());
        return ResponseEntity.ok(ObjectResponse.builder()
                .message("yes")
                .items(category)
                .build());
    }
    
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") long idCategory,
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) throws Exception {

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
        Category category = this.categoryServiec.updateCategory(idCategory, categoryDTO);
        return ResponseEntity.ok(ObjectResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.UPDATE_SUCCESSFULLY))
                .items(category)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteCategory(
            @PathVariable("id") long idCategory) throws Exception {
        categoryServiec.deleteCategory(idCategory);
        return ResponseEntity.ok(MessageResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY))
                .build());
    }
}
