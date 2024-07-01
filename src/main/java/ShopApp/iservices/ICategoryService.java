/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.dtos.CategoryDTO;
import ShopApp.models.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface ICategoryService {
    
    Page<Category> getAllCategories(PageRequest pageRequest);
    
    Category getCategoryById(long id);
    
    Category createCategory(CategoryDTO categoryDTO);
    
    Category updateCategory(long id, CategoryDTO categoryDTO);
    
    void deleteCategory(long id);
}
