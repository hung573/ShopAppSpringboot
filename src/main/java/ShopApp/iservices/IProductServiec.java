/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.dtos.ProductDTO;
import ShopApp.dtos.ProductImageDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Product;
import ShopApp.models.ProductImage;
import ShopApp.responses.ProductResponse;
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
    
    Product updateProduct(long id, ProductDTO productDTO)  throws DataNotFoudException;
    
    void deleteProduct(long id) throws DataNotFoudException;
    
    boolean existsByName(String name);
    
    ProductImage createProductImage(long productId,ProductImageDTO productImageDTO) throws Exception;
    
}
