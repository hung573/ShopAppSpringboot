/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.Order;

import ShopApp.dtos.OrderDTO;
import ShopApp.dtos.OrderUpdateDTO;
import ShopApp.models.Order;
import ShopApp.responses.OrderResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *
 * @author mac
 */
public interface IOrderService {
    Order creteOrder (OrderDTO orderDTO) throws Exception;
    
    Order getOrderById(long id) throws Exception;
    
    OrderResponse getOrderResponseById(long id) throws Exception;
    
    Order updateOrder(long id, OrderUpdateDTO orderDTO) throws Exception;
    
    void deleteOrder(long id) throws Exception;
    
    List<Order> getAllByUserId(long user_id) throws Exception;
    
    Page<Order> getAllOrders(PageRequest pageRequest);
    
    Page<OrderResponse> getOrderByKeyWord(String keyword, PageRequest pageRequest) throws Exception;

}
