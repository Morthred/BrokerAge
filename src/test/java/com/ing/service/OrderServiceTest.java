package com.ing.service;

import com.ing.dto.OrderDTO;
import com.ing.model.*;
import com.ing.repository.AssetRepository;
import com.ing.repository.CustomerRepository;
import com.ing.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Asset asset;
    private Customer customer;
    private Asset TRYAsset;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("sampleUser");
        customer.setPassword("password");

        TRYAsset = Asset.builder().assetName("TRY").size(800).usableSize(800).build();
        customer.getAssets().add(TRYAsset);

        asset = Asset.builder().build();
        asset.setId(1L);
        asset.setAssetName("BTC");
        asset.setSize(100);
        asset.setUsableSize(80);

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setAsset(asset);
        order.setSize(3);
        order.setPrice(150.0);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());
        order.setOrderSideType(OrderSideType.BUY);
    }

    @Test
    public void testCreateOrder() {
        order.setAsset(asset);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(customerRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(customer));
        when(assetRepository.findByCustomerIdAndAssetName(customer.getId(), "TRY")).thenReturn(TRYAsset);
        when(assetRepository.findByCustomerIdAndAssetName(customer.getId(), order.getAsset().getAssetName())).thenReturn(asset);
        OrderDTO createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(order.getId(), createdOrder.getId());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void testListOrders() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        when(orderRepository.findByCustomerIdAndCreateDateBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(order));

        List<OrderDTO> orders = orderService.listOrders(customer.getId(), startDate, endDate);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderRepository, times(1)).findByCustomerIdAndCreateDateBetween(customer.getId(), startDate, endDate);
    }

    @Test
    public void testDeleteOrder() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName(customer.getId(), "TRY")).thenReturn(TRYAsset);
        when(assetRepository.findByCustomerIdAndAssetName(customer.getId(), order.getAsset().getAssetName())).thenReturn(asset);

        orderService.deleteOrder(order.getId());

        verify(orderRepository, times(1)).delete(order);
    }

    @Test
    public void testDeleteOrderThrowsExceptionWhenOrderNotPending() {
        order.setOrderStatus(OrderStatus.MATCHED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.deleteOrder(order.getId()));

        verify(orderRepository, never()).deleteById(order.getId());
    }

    @Test
    public void testListAssets() {
        when(orderRepository.findDistinctAssetNamesByCustomerId(anyLong())).thenReturn(Arrays.asList("Sample Asset"));

        List<String> assets = orderService.listAssets(customer.getId());

        assertNotNull(assets);
        assertEquals(1, assets.size());
        assertEquals("Sample Asset", assets.get(0));
        verify(orderRepository, times(1)).findDistinctAssetNamesByCustomerId(customer.getId());
    }
}