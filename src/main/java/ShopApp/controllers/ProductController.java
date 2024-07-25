/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.controllers;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.ProductDTO;
import ShopApp.dtos.ProductImageDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Product;
import ShopApp.models.ProductImage;
import static ShopApp.models.ProductImage.MAX_IMG_PER_PRODUCT;
import ShopApp.responses.ProductResponse;
import ShopApp.iservices.IProductServiec;
import ShopApp.responses.ListResponse;
import ShopApp.responses.ObjectResponse;
import ShopApp.services.ProductRedisService;
import ShopApp.services.ProductService;
import ShopApp.utils.MessageKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author mac
 */

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final ProductRedisService productRedisService;
    private final LocalizationUtils localizationUtils;

    
    @GetMapping("")
    private ResponseEntity<ListResponse> getAllProduct(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) throws JsonProcessingException, Exception{

        // Điều chỉnh page để bắt đầu từ 1 thay vì 0
        int adjustedPage = page > 0 ? page - 1 : 0;
        
        // Tạo Pageable từ adjustedPage và limit
        PageRequest pageRequest = PageRequest.of(adjustedPage, limit,
                Sort.by("id").ascending()
        );
        
        List<ProductResponse> productsListRedist = productRedisService.getAllProducts(keyword, categoryId, pageRequest);
        int totalPages = 0;
        if (productsListRedist == null) {
            Page<ProductResponse> productPage = productService.getAllProductSearch(categoryId, keyword, pageRequest);
            // tong trang
            totalPages = productPage.getTotalPages();
            productsListRedist = productPage.getContent();
            productRedisService.saveAllProducts(productsListRedist, keyword, categoryId, pageRequest);
        }
        if ( !keyword.isEmpty() || categoryId > 0) {
            Page<ProductResponse> productPage = productService.getAllProductSearch(categoryId, keyword, pageRequest);
            // tong trang
            totalPages = productPage.getTotalPages();
            productsListRedist = productPage.getContent();
            productRedisService.saveAllProducts(productsListRedist, keyword, categoryId, pageRequest);
        }
        else{
            totalPages = productService.totalPages(limit);
        }
         // Create response
        ListResponse<ProductResponse> ProductListResponse = ListResponse
                .<ProductResponse>builder()
                .items(productsListRedist)
                .page(page)
                .totalPages(totalPages)
                .build();

        return ResponseEntity.ok(ProductListResponse);
    }
    
    @GetMapping("/{id}")
    private ResponseEntity<ObjectResponse> getIdProduct(@PathVariable("id") long idProduct){
        Product product = new Product();
        try {
            product = productService.getProductById(idProduct);
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.GETID_SUCCESSFULLY))
                    .items(ProductResponse.fromProduct(product))
                    .build());
        } catch (DataNotFoudException ex) {
            return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, ex.getMessage()))
                        .build());
        }

    }
    
    // Lay danh sach gio hang 
    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // thay doi cach upImg
//    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping("/add")
    private ResponseEntity<ObjectResponse> addProduct(
            @Valid @RequestBody ProductDTO productDTO,
//            @ModelAttribute("files") List<MultipartFile> files,
            BindingResult result){
        try {
            if (result.hasErrors()) {
                List<String> errormessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, errormessage))
                        .build());
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
            return ResponseEntity.ok(ObjectResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKey.ADD_SUCCESSFULLY))
                    .items(newProduct)
                    .build());
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(ObjectResponse.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKey.ERORR, e.getMessage()))
                        .build());
        }
    }
    
    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImage(
            @ModelAttribute("files") List<MultipartFile> files,
            @PathVariable("id") long productId){
        
        try {
            Product existingProduct = productService.getProductById(productId);
            List<ProductImage> productImages = new ArrayList<>();
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > MAX_IMG_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessageKey.UPLOAD_IMAGES_MAX_5));
            }
            for(MultipartFile fileIMG : files){
                if(fileIMG.getSize() == 0){
                    continue;
                }
                // kiểm tra kích thước file ảnh khong vuot qua 10MB
                if (fileIMG.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body(localizationUtils.getLocalizedMessage(MessageKey.UPLOAD_IMAGES_FILE_LARGE));
                }
                // kiểm tra đây có phải là file ảnh hay không
                // String contentType = fileIMG.getContentType();
                if (!isImageFile(fileIMG) || fileIMG.getOriginalFilename() == null) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .body(localizationUtils.getLocalizedMessage(MessageKey.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }
                // luu file
                String fileName = storeFile(fileIMG);
                // luu vao doi tuong product trong DB lam sau
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),ProductImageDTO.builder()
                                .imageUrl(fileName)
                                .build());
                productImages.add(productImage);
            }
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
    private ResponseEntity<?> updateProduct(@PathVariable("id") long idProduct, @Valid @RequestBody ProductDTO productDTO){
        
        try {
            Product updateProduct = productService.updateProduct(idProduct, productDTO);
            return ResponseEntity.ok(updateProduct);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    @DeleteMapping("/delete/{id}")
    private ResponseEntity<String> deleteProduct(@PathVariable("id") long idProduct){
        try {
            productService.deleteProduct(idProduct);
            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKey.DELETE_SUCCESSFULLY));
        } catch (DataNotFoudException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
    @PostMapping("/genfakerProducts")
    private ResponseEntity<?> genfakerProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {
            try {
                String productName = faker.commerce().productName();
                if (productService.existsByName(productName)) {
                    continue;
                }
                ProductDTO productDTO = ProductDTO.builder()
                        .name(productName)
                        .price((float) faker.number().numberBetween(10, 90_000_000))
                        .description(faker.lorem().sentence())
                        .thumbnail("")
                        .categoryId((long) faker.number().numberBetween(2, 5))
                        .build();
                productService.createProduct(productDTO);
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }
        return ResponseEntity.ok("Gen Faker Products Successfully");
    }
    

}
