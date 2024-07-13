/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.CartItemDTO;
import ShopApp.dtos.OrderDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.iservices.IOrderService;
import ShopApp.models.Order;
import ShopApp.models.OrderDetail;
import ShopApp.models.OrderStatus;
import ShopApp.models.Product;
import ShopApp.models.User;
import ShopApp.repositories.OrderDetailRepository;
import ShopApp.repositories.OrderRepository;
import ShopApp.repositories.ProductRepository;
import ShopApp.repositories.UserRepository;
import ShopApp.utils.MessageKey;
import jakarta.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Around;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper; 
    private final LocalizationUtils localizationUtils;
    @Override
    @Transactional
    public Order creteOrder(OrderDTO orderDTO) throws Exception{
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
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
            throw new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.ORDER_SHIPPINGDATE_INVALID));
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);
        
        // Tạo danh sách các đối tượng OrderDetail từ cartItems
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            // Tạo một đối tượng OrderDetail từ CartItemDTO
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // Lấy thông tin sản phẩm từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // Tìm thông tin sản phẩm từ cơ sở dữ liệu (hoặc sử dụng cache nếu cần)
            Product product = productService.getProductById(productId);

            // Đặt thông tin cho OrderDetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            // Các trường khác của OrderDetail nếu cần
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalMoney(quantity * product.getPrice());
            
            // Thêm OrderDetail vào danh sách
            orderDetails.add(orderDetail);
        }

        // Lưu danh sách OrderDetail vào cơ sở dữ liệu
        orderDetailRepository.saveAll(orderDetails);
        return order;
    
    }

    @Override
    public Order getOrderById(long id) throws Exception{
        
        String currentPhoneNumberUser = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByPhoneNumber(currentPhoneNumberUser)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        
        // Kiểm tra nếu user hiện tại là chủ sở hữu của order hoặc là admin
        if (!order.getUser().getId().equals(user.getId()) && !isCurrentUserAdmin()) {
            throw new AccessDeniedException(localizationUtils.getLocalizedMessage(MessageKey.ERORR));
        }
        
        return order;
    }
    
    private boolean isCurrentUserAdmin() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    @Transactional
    public Order updateOrder(long id, OrderDTO orderDTO) throws Exception{
        Order order = getOrderById(id);
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        return orderRepository.save(order);
        
    }

    @Override
    @Transactional
    public void deleteOrder(long id) throws Exception{
        Order order = getOrderById(id);
        order.setActive(false);
        orderRepository.save(order);

    }

    @Override
    public List<Order> getAllByUserId(long user_id) throws Exception{
        
        String currentPhoneNumberUser = SecurityContextHolder.getContext().getAuthentication().getName();
        
        User user = userRepository.findByPhoneNumber(currentPhoneNumberUser)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        
        List<Order> orders = orderRepository.findByUserId(user_id);
        
        for(Order order : orders){
           // Kiểm tra nếu user hiện tại là chủ sở hữu của order hoặc là admin
            if (!order.getUser().getId().equals(user.getId()) && !isCurrentUserAdmin()) {
                throw new AccessDeniedException(localizationUtils.getLocalizedMessage(MessageKey.ERORR));
            } 
        }
        
        return orders;
    }

    @Override
    public Page<Order> getAllOrders(PageRequest pageRequest) {
        return orderRepository.findAll(pageRequest);
    }
    
}
