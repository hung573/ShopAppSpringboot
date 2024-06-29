/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.dtos.ProductDTO;
import ShopApp.dtos.ProductImageDTO;
import ShopApp.models.Product;
import ShopApp.models.ProductImage;
import static ShopApp.models.ProductImage.MAX_IMG_PER_PRODUCT;
import ShopApp.responses.ProductListResponse;
import ShopApp.responses.ProductResponse;
import ShopApp.iservices.IProductServiec;
import ShopApp.services.ProductService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author mac
 */
@Controller
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final IProductServiec productService;
    
    @GetMapping("")
    private ResponseEntity<ProductListResponse> getAllProduct(@RequestParam("page") int page, @RequestParam("limit") int limit){
        
        // Tạo Pageable từ page và limit
        PageRequest pageRequest = PageRequest.of(page, limit,
                Sort.by("createdAt").descending());
        
        Page<ProductResponse> productPage = productService.getAllProduct(pageRequest);
        // tong trang
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .page(page)
                .totalPages(totalPages)
                .build());
    }
    @GetMapping("/{id}")
    private ResponseEntity<String> getIdProduct(@PathVariable("id") long idProduct){
        return ResponseEntity.ok("Get Successfully id: "+ idProduct);
    }
    
    // thay doi cach upImg
//    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping("/add")
    private ResponseEntity<?> addProduct(
            @Valid @RequestBody ProductDTO productDTO,
//            @ModelAttribute("files") List<MultipartFile> files,
            BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errormessage);
            }
            
            Product newProduct = productService.createProduct(productDTO);

//            List<MultipartFile> ListfileIMG = productDTO.getFiles();
//            ListfileIMG = ListfileIMG == null ? new ArrayList<MultipartFile>() : ListfileIMG;
//            for(MultipartFile fileIMG : ListfileIMG){
//                if(fileIMG.getSize() == 0){
//                    continue;
//                }
//                // kiểm tra kích thước file ảnh khong vuot qua 10MB
//                if (fileIMG.getSize() > 10 * 1024 * 1024) {
//                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//                                .body("File ảnh không được vượt quá 10MB");
//                }
//                // kiểm tra đây có phải là file ảnh hay không
//                String contentType = fileIMG.getContentType();
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//                                .body("File chưa đúng định dạng!!!");
//                }
//                // luu file
//                String fileName = storeFile(fileIMG);
//                // luu vao doi tuong product trong DB lam sau
//                ProductImage productImage = productService.createProductImage(newProduct.getId(),
//                        ProductImageDTO.builder()
//                                .imageUrl(fileName)
//                                .build());
//                // thuc chat la luu vao product_image
//            }
            return ResponseEntity.ok("add thanh cong: " + productDTO);
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImage(
            @ModelAttribute("files") List<MultipartFile> files,
            @PathVariable("id") long productId) throws Exception {
        
        Product existingProduct = productService.getProductById(productId);
        List<ProductImage> productImages = new ArrayList<>();
        files = files == null ? new ArrayList<MultipartFile>() : files;
        if (files.size() > MAX_IMG_PER_PRODUCT) {
            return ResponseEntity.badRequest().body("Bạn không uploads nhiều hơn 5 bực ảnh cho 1 sản phẩm");
        }
        for(MultipartFile fileIMG : files){
            if(fileIMG.getSize() == 0){
                continue;
            }
            // kiểm tra kích thước file ảnh khong vuot qua 10MB
            if (fileIMG.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File ảnh không được vượt quá 10MB");
            }
            // kiểm tra đây có phải là file ảnh hay không
//            String contentType = fileIMG.getContentType();
            if (!isImageFile(fileIMG) || fileIMG.getOriginalFilename() == null) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File: "+fileIMG.getOriginalFilename()+" chưa đúng định dạng!!!");
            }
            // luu file
            String fileName = storeFile(fileIMG);
            // luu vao doi tuong product trong DB lam sau
            ProductImage productImage = productService.createProductImage(existingProduct.getId(),
                    ProductImageDTO.builder()
                            .imageUrl(fileName)
                            .build());
            productImages.add(productImage);
        }
        return ResponseEntity.ok(productImages);
    }
    
    // Ham kiem tra anh co đúng định dạng hay không
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    
    private String storeFile(MultipartFile fileIMG) throws IOException{
        String fileName = StringUtils.cleanPath(fileIMG.getOriginalFilename());
        // Them UUID vao truoc ten file de dam bao ten file la duy nhat
        String newFileName =UUID .randomUUID().toString() + "_" +fileName ;
        // Duong dan den thu muc muon luu file
        Path uploadDir = Paths.get("uploads");
        // Kiem tra va tao thu muc neu no chua ton tai
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Duong dan day du ten file
        Path destination = Paths.get(uploadDir.toString(), newFileName);
        // Sao chep file vao thu muc dich
        Files.copy(fileIMG.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return  newFileName;
    }
    
    @PutMapping("/update/{id}")
    private ResponseEntity<String> updateProduct(@PathVariable("id") long idProduct){
        return ResponseEntity.ok("Update Product Successfully id: "+ idProduct);
    }
    @DeleteMapping("/{id}")
    private ResponseEntity<String> deleteProduct(@PathVariable("id") long idProduct){
        return ResponseEntity.ok("Delete Product Successfully id: "+ idProduct);
    }
    

}
