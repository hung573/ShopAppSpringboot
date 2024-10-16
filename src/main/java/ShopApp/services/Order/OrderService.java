/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ShopApp.services.Order;

import ShopApp.components.LocalizationUtils;
import ShopApp.dtos.CartItemDTO;
import ShopApp.dtos.OrderDTO;
import ShopApp.dtos.OrderUpdateDTO;
import ShopApp.exception.DataNotFoudException;
import ShopApp.models.Coupon;
import ShopApp.models.Order;
import ShopApp.models.OrderDetail;
import ShopApp.models.OrderStatus;
import ShopApp.models.Payment;
import ShopApp.models.Product;
import ShopApp.models.User;
import ShopApp.repositories.CouponRepository;
import ShopApp.repositories.OrderDetailRepository;
import ShopApp.repositories.OrderRepository;
import ShopApp.repositories.PaymentRepository;
import ShopApp.repositories.ProductRepository;
import ShopApp.repositories.UserRepository;
import ShopApp.responses.OrderResponse;
import ShopApp.services.Product.ProductService;
import ShopApp.utils.MessageKey;
import jakarta.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Around;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author mac
 */
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    private final LocalizationUtils localizationUtils;
    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Order creteOrder(OrderDTO orderDTO) throws Exception {
        Order order = new Order();
        Coupon coupon = new Coupon();
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        long couponId = orderDTO.getCouponId();
        if (couponId != 0) {
            coupon = couponRepository.findById(orderDTO.getCouponId())
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
            
            if (!coupon.isActive()) {
                throw new IllegalArgumentException("Coupon is not active");
            }
            order.setCoupon(coupon);
        }
        else{
            order.setCoupon(null);
        }
        Payment payment = paymentRepository.findById(orderDTO.getPaymentId())
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        
        // convert Order DTO -> Order
        // dung thu thien ModelMapper
//        modelMapper.typeMap(OrderDTO.class, Order.class)
//            .addMappings(mapper -> {
//                // Bỏ qua việc ánh xạ các thuộc tính CouponId và UserId vào id
//                mapper.skip(Order::setId);
//            });

//        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setFullName(orderDTO.getFullName());
        order.setEmail(orderDTO.getEmail());
        order.setPhoneNumber(orderDTO.getPhoneNumber());
        order.setAddress(orderDTO.getAddress());
        order.setNote(orderDTO.getNote());
        order.setOrderDate(new Date()); // thời gian hiện tại
        order.setStatus(OrderStatus.PENDING);
        order.setTotalMoney(orderDTO.getTotalMoney());
        order.setShippingMethod(orderDTO.getShippingMethod());
        order.setShippingAddress(orderDTO.getShippingAddress());
        // Lấy ngày hiện tại
        LocalDate localDate = LocalDate.now();
        // Thêm 1 ngày
        LocalDate nextDay = localDate.plusDays(1);
        // Chuyển đổi LocalDate thành Date
        ZonedDateTime zonedDateTime = nextDay.atStartOfDay(ZoneId.systemDefault());
        Date shippingDate = Date.from(zonedDateTime.toInstant());
        order.setShippingDate(shippingDate);
        order.setTrackingNumber("");
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setPayment(payment);
        order.setActive(true);

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
            if (couponId != 0) {
                if (!coupon.isActive()) {
                    throw new IllegalArgumentException("Coupon is not active");
                }
                order.setCoupon(coupon);
            }
            else{
                orderDetail.setCoupon(null);
            }

            // Thêm OrderDetail vào danh sách
            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);
        // Lưu danh sách OrderDetail vào cơ sở dữ liệu
        orderDetailRepository.saveAll(orderDetails);
        orderRepository.save(order);

        return order;

    }

    @Override
    public Order getOrderById(long id) throws Exception {

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
    public Order updateOrder(long id, OrderUpdateDTO orderDTO) throws Exception {
        Order order = getOrderById(id);
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));
        
        modelMapper.typeMap(OrderUpdateDTO.class, Order.class)
                .addMappings(mapper -> {
                    mapper.skip(Order::setId);
                    mapper.skip(Order::setCoupon);
                    mapper.skip(Order::setOrderDate);
                });
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        return orderRepository.save(order);

    }

    @Override
    @Transactional
    public void deleteOrder(long id) throws Exception {
        Order order = getOrderById(id);
        order.setActive(false);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getAllByUserId(long user_id) throws Exception {

        String currentPhoneNumberUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByPhoneNumber(currentPhoneNumberUser)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));

        List<Order> orders = orderRepository.findByUserId(user_id);

        for (Order order : orders) {
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

    @Override
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<OrderResponse> getOrderByKeyWord(String keyword, PageRequest pageRequest) throws Exception {
        Page<Order> pageOrders;
        pageOrders = orderRepository.searchOrders(keyword, pageRequest);
        return pageOrders.map(OrderResponse::fromOrder);
    }

    @Override
    public OrderResponse getOrderResponseById(long id) throws Exception {
        String currentPhoneNumberUser = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByPhoneNumber(currentPhoneNumberUser)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoudException(localizationUtils.getLocalizedMessage(MessageKey.NOT_FOUND)));

        // Kiểm tra nếu user hiện tại là chủ sở hữu của order hoặc là admin
        if (!order.getUser().getId().equals(user.getId()) && !isCurrentUserAdmin()) {
            throw new AccessDeniedException(localizationUtils.getLocalizedMessage(MessageKey.ERORR));
        }

        return OrderResponse.fromOrder(order);
    }

}
