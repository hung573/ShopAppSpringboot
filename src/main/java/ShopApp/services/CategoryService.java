/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.iservices.ICategoryService;
import ShopApp.dtos.CategoryDTO;
import ShopApp.models.Category;
import ShopApp.repositories.CategoryRepository;
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
public class CategoryService implements ICategoryService{
    
    private final CategoryRepository categoryRepository;
    
    @Override
    public Page<Category> getAllCategories(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category by id: "+ id +" not found"));
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category
                .builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category updateCategory(long id, CategoryDTO categoryDTO) {
        Category newCategory = getCategoryById(id);
        newCategory.setName(categoryDTO.getName());
        categoryRepository.save(newCategory);
        return newCategory;
    }

    @Override
    public void deleteCategory(long id) {
        // xoa cung
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }


    
}
