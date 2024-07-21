/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.responses.Product_ImgResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IProduct_ImgService {
    Page<Product_ImgResponse> getAllProductIMGSearch(String keyword, PageRequest pageRequest);
    int totalPages(int limit) throws Exception;

}
