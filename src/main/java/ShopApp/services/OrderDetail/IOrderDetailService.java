/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ShopApp.services.OrderDetail;

import ShopApp.dtos.OrderDetailDTO;
import ShopApp.models.OrderDetail;
import ShopApp.responses.OrderDetailResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
    
    Page<OrderDetailResponse> getAllOrderDetails(PageRequest pageRequest);

}
