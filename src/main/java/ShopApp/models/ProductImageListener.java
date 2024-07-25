/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.models;

import ShopApp.services.Product_ImgRedisService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mac
 */
@AllArgsConstructor
public class ProductImageListener {
    private final Product_ImgRedisService product_ImgRedisService;
    private static final Logger logger = LoggerFactory.getLogger(ProductImageListener.class);
    
    @PrePersist
    public void prePersist(ProductImage productImage) {
        logger.info("prePersist");
    }

    @PostPersist //save = persis
    public void postPersist(ProductImage productImage) {
        // Update Redis cache
        logger.info("postPersist");
        product_ImgRedisService.clear();
    }

    @PreUpdate
    public void preUpdate(ProductImage productImage) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preUpdate");
    }

    @PostUpdate
    public void postUpdate(ProductImage productImage) {
        // Update Redis cache
        logger.info("postUpdate");
        product_ImgRedisService.clear();
    }

    @PreRemove
    public void preRemove(ProductImage productImage) {
        //ApplicationEventPublisher.instance().publishEvent(event);
        logger.info("preRemove");
    }

    @PostRemove
    public void postRemove(ProductImage productImage) {
        // Update Redis cache
        logger.info("postRemove");
        product_ImgRedisService.clear();
    }
}
