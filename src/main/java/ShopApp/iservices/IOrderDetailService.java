/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.iservices;

import ShopApp.dtos.OrderDetailDTO;
import ShopApp.models.OrderDetail;
import java.util.List;

/**
 *
 * @author mac
 */
public interface IOrderDetailService {
    
    OrderDetail creteOrderDetail (OrderDetailDTO orderDetailDTO) throws Exception;
    
    OrderDetail getOrderDetailById(long id) throws Exception;
    
    OrderDetail updateOrderDetail(long id, OrderDetailDTO orderDetailDTO) throws Exception;
    
    void deleteOrderDetail(long id) throws Exception;
    
    List<OrderDetail> getAllByOrderId(long order_id);
}
