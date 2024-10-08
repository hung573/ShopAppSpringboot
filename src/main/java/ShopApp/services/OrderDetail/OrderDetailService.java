/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.OrderDetail;

import ShopApp.services.Order.OrderService;
import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.OrderDetailDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Order;
import ShopApp.models.OrderDetail;
import ShopApp.models.Product;
import ShopApp.repositories.OrderDetailRepository;
import ShopApp.repositories.OrderRepository;
import ShopApp.repositories.ProductRepository;
import ShopApp.responses.OrderDetailResponse;
import ShopApp.services.Product.ProductService;
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
public class OrderDetailService implements IOrderDetailService{
    
    private final OrderDetailRepository orderDetailRepository; 
    
    private final OrderRepository orderRepository;
    
    private final ProductRepository productRepository;
    
    private final OrderService orderService;
    
    private final ProductService productService;
    
    private final LocalizationUtils loaLocalizationUtils;
    
    @Override
    @Transactional
    public OrderDetail creteOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoudException(loaLocalizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND, "Order Id")));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoudException(loaLocalizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND, "Product Id")));

        
        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProduct())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetailById(long id) throws Exception {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(loaLocalizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND, "Order Id")));

    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(long id, OrderDetailDTO orderDetailDTO) throws Exception {
        
        OrderDetail orderDetail = getOrderDetailById(id);
        Order order = orderService.getOrderById(orderDetailDTO.getOrderId());
        Product product = productService.getProductById(orderDetailDTO.getProductId());
        
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setPrice(orderDetailDTO.getPrice());
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProduct());
        orderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(long id) throws Exception {
        OrderDetail orderDetail = getOrderDetailById(id);
        orderDetailRepository.delete(orderDetail);
    }

    @Override
    public List<OrderDetail> getAllByOrderId(long order_id) {
        return orderDetailRepository.findByOrderId(order_id);
    }

    @Override
    public Page<OrderDetailResponse> getAllOrderDetails(PageRequest pageRequest) {
        return orderDetailRepository.findAll(pageRequest)
                .map(OrderDetailResponse::fromOrderDetail);
    }
    
}
