/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.Product;

import ShopApp.dtos.ProductDTO;
import ShopApp.dtos.ProductImageDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.exception.InvalidParamExeption;
import ShopApp.models.Category;
import ShopApp.models.Product;
import ShopApp.models.ProductImage;
import static ShopApp.models.ProductImage.MAX_IMG_PER_PRODUCT;
import ShopApp.repositories.CategoryRepository;
import ShopApp.repositories.ProductImageRepository;
import ShopApp.repositories.ProductRepository;
import ShopApp.responses.ProductResponse;
import jakarta.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ProductService implements IProductServiec{
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    
    private static String UPLOADS_FOLDER = "uploads";

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws Exception{
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoudException("Không tìm thấy mã loại sản phẩm này."));
        
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .active(true)
                .build();

        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long id) throws DataNotFoudException{
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException("Không tìm thấy sản phẩm có id: "+ id));
    }

    @Override
    public Page<ProductResponse> getAllProduct(PageRequest pageRequest) {
        // page va limit sẽ được xử lý trên 
        return productRepository.findAll(pageRequest)
                .map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoudException{
        Product existingProduct = getProductById(id);
        
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoudException("Không tìm thấy mã loại sản phẩm này."));
        
        String name = productDTO.getName();
        if(name != null && !name.isEmpty() && !name.equals(existingProduct.getName())) {
            existingProduct.setName(productDTO.getName());
        }
        
        long category_id = productDTO.getCategoryId();
        if (category_id > 0 && category_id != existingProduct.getCategory().getId()) {
            existingProduct.setCategory(existingCategory);
        }
        
        float price = productDTO.getPrice();
        if(price >= 0 && price != existingProduct.getPrice()) {
            existingProduct.setPrice(productDTO.getPrice());
        }
        
        String description = productDTO.getDescription();
        if(description != null &&
                !description.isEmpty() && !description.equals(existingProduct.getDescription())) {
            existingProduct.setDescription(productDTO.getDescription());
        }
        
        String thumbnail = productDTO.getThumbnail();
        if(thumbnail != null &&
                !thumbnail.isEmpty() && !thumbnail.equals(existingProduct.getThumbnail())) {
            existingProduct.setThumbnail(productDTO.getThumbnail());
        }
        
        existingProduct.setActive(productDTO.isActive());
        return productRepository.save(existingProduct);
        
    }

    @Override
    @Transactional
    public void deleteProduct(long id, boolean active) throws DataNotFoudException{
        Product existingProduct = getProductById(id);
        existingProduct.setActive(active);
        productRepository.save(existingProduct);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    
    @Override
    public ProductImage createProductImage(long productId,ProductImageDTO productImageDTO) throws Exception{
        Product existingProduct = getProductById(productId);
        
        ProductImage productImage = ProductImage.builder()
                .product(existingProduct)
                .imageURL(productImageDTO.getImageUrl())
                .build();
        
        // khong cho them nhieu hon 5 anh
        int size  = productImageRepository.findByProductId(productId).size();
        if(size >= MAX_IMG_PER_PRODUCT){
            throw new IOException("Số lượng hình ảnh không được vượt quá 5");
        }
        return productImageRepository.save(productImage);
        
    }

    @Override
    public Page<ProductResponse> getAllProductSearch(long categoryId, String keyword, PageRequest pageRequest) {
        Page<Product> pageProducts;
        pageProducts = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return pageProducts.map(ProductResponse::fromProduct);
    }
    
    @Override
    public Page<ProductResponse> getAllProductSearchIsActive(long categoryId, String keyword, PageRequest pageRequest) {
        Page<Product> pageProducts;
        pageProducts = productRepository.searchProductsIsActive(categoryId, keyword, pageRequest);
        return pageProducts.map(ProductResponse::fromProduct);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) throws Exception{
        return productRepository.findProductsByIds(productIds);

    }

    @Override
    public int totalPages(int limit) throws Exception {
        return productRepository.findTotalPages(limit);
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        // Đường dẫn đến thư mục chứa file
        java.nio.file.Path uploadDir = Paths.get(UPLOADS_FOLDER);
        // Đường dẫn đầy đủ đến file cần xóa
        java.nio.file.Path filePath = uploadDir.resolve(filename);

        // Kiểm tra xem file tồn tại hay không
        if (Files.exists(filePath)) {
            // Xóa file
            Files.delete(filePath);
        } else {
            throw new FileNotFoundException("File not found: " + filename);
        }
    }




    
}
