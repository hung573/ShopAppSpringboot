/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.components.LocalizationUtils;
import ShopApp.iservices.ICategoryService;
import ShopApp.dtos.CategoryDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Category;
import ShopApp.repositories.CategoryRepository;
import ShopApp.utils.MessageKey;
import jakarta.transaction.Transactional;
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
    private final LocalizationUtils localizationUtils;

    
    @Override
    public Page<Category> getAllCategories(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest);
    }

    @Override
    public Category getCategoryById(long id) throws Exception{
        return categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
    }

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) throws Exception{
        Category newCategory = Category
                .builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    @Transactional
    public Category updateCategory(long id, CategoryDTO categoryDTO) throws Exception{
       
        Category newCategory = getCategoryById(id);
        String name = categoryDTO.getName();
        if (name != null && !name.isEmpty() && name != " " && !name.equals(newCategory.getName())) {
            newCategory.setName(categoryDTO.getName());
            categoryRepository.save(newCategory);
        }
        return newCategory;
    }

    @Override
    @Transactional
    public void deleteCategory(long id) throws Exception{
        // xoa cung
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    @Override
    public List<Category> getAllCategoriesHelCheck() {
        return categoryRepository.findAll();
    }


    
}
