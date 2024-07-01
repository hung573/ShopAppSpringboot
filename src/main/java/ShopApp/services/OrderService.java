/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.dtos.OrderDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.iservices.IOrderService;
import ShopApp.models.Order;
import ShopApp.models.OrderStatus;
import ShopApp.models.User;
import ShopApp.repositories.OrderRepository;
import ShopApp.repositories.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Around;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper; 
    @Override
    public Order creteOrder(OrderDTO orderDTO) throws Exception{
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoudException("Không tìm thấy user có id: "+ orderDTO.getUserId()));
        // convert Order DTO -> Order
        // dung thu thien ModelMapper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date()); // thời gian hiện tại
        order.setStatus(OrderStatus.PENDING);
        // shipping date phải lớn hơn ngày hơm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoudException("Ngày giao hàng không hợp lệ");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        return order; 
    
    }

    @Override
    public Order getOrderById(long id) throws Exception{
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException("Không tìm thấy OrderId hợp lệ"));
    }

    @Override
    public Order updateOrder(long id, OrderDTO orderDTO) throws Exception{
        Order order = getOrderById(id);
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoudException("User Id không hợp lệ."));
        
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        return orderRepository.save(order);
        
    }

    @Override
    public void deleteOrder(long id) throws Exception{
        Order order = getOrderById(id);
        order.setActive(false);
        orderRepository.save(order);

    }

    @Override
    public List<Order> getAllByUserId(long user_id) {
        return orderRepository.findByUserId(user_id);
    }
    
}
