package ShopApp.services;

import ShopApp.iservices.IProduct_ImgService;
import ShopApp.models.ProductImage;
import ShopApp.repositories.ProductImageRepository;
import ShopApp.responses.Product_ImgResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class Product_ImgService implements IProduct_ImgService{
    
    private final ProductImageRepository productImageRepository;

    @Override
    public Page<Product_ImgResponse> getAllProductIMGSearch(String keyword, PageRequest pageRequest) {
        Page<ProductImage> pageProductIMG;
        pageProductIMG = productImageRepository.searchProductIMG(keyword, pageRequest);
        return pageProductIMG.map(Product_ImgResponse::fromProductIMG); 
    }
    
}
