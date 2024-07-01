/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.dtos.OrderDTO;
import ShopApp.models.Order;
import java.util.List;

/**
 *
 * @author mac
 */
public interface IOrderService {
    Order creteOrder (OrderDTO orderDTO) throws Exception;
    Order getOrderById(long id) throws Exception;
    Order updateOrder(long id, OrderDTO orderDTO) throws Exception;
    void deleteOrder(long id) throws Exception;
    List<Order> getAllByUserId(long user_id);
}
