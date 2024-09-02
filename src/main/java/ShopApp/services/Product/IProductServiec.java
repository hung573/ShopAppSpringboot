/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.Product;

import ShopApp.dtos.ProductDTO;
import ShopApp.dtos.ProductImageDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Product;
import ShopApp.models.ProductImage;
import ShopApp.responses.ProductResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IProductServiec {
    
    Product createProduct(ProductDTO productDTO) throws Exception;
    
    Product getProductById(long id) throws DataNotFoudException;
    
    Page<ProductResponse> getAllProduct(PageRequest pageRequest);
    
    Page<ProductResponse> getAllProductSearchIsActive(long categoryId, String keyword, PageRequest pageRequest);
    
    Page<ProductResponse> getAllProductSearch(long categoryId, String keyword, PageRequest pageRequest);
    
    Product updateProduct(long id, ProductDTO productDTO)  throws DataNotFoudException;
    
    void deleteProduct(long id, boolean active) throws DataNotFoudException;
    
    boolean existsByName(String name);
    
    ProductImage createProductImage(long productId,ProductImageDTO productImageDTO) throws Exception;
    
    List<ProductResponse> findProductsByIds(List<Long> productIds) throws Exception;
    
    int totalPages(int limit) throws Exception;
    
    void deleteFile(String filename) throws IOException;
    
}
