package com.ing.service;

import com.ing.dto.OrderDTO;
import com.ing.model.*;
import com.ing.repository.AssetRepository;
import com.ing.repository.CustomerRepository;
import com.ing.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public OrderDTO createOrder(Order order) throws IllegalStateException {
        // Ensure the customer and asset are properly associated
        if (order.getCustomer() == null || order.getAsset() == null) {
            throw new IllegalStateException("Order must have a valid customer and asset.");
        }

        Optional<Customer> customer = customerRepository.findById(order.getCustomer().getId());
        if (customer.isEmpty()) {
            throw new IllegalStateException("Customer not found.");
        }

        // Get customer's TRY balance
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomer().getId(), "TRY");

        // Get customer's asset balance (only relevant for SELL orders)
        Asset asset = assetRepository.findByCustomerIdAndAssetName(order.getCustomer().getId(), order.getAsset().getAssetName());

        // Handle the BUY logic
        if (order.getOrderSideType() == OrderSideType.BUY) {
            double totalCost = order.getSize() * order.getPrice();
            if (tryAsset == null || tryAsset.getUsableSize() < totalCost) {
                throw new IllegalStateException("Insufficient TRY balance to buy " + order.getAsset().getAssetName());
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() - totalCost);
            assetRepository.save(tryAsset);
        }
        // Handle the SELL logic
        else if (order.getOrderSideType() == OrderSideType.SELL) {
            if (asset == null || asset.getUsableSize() < order.getSize()) {
                throw new IllegalStateException("Insufficient " + order.getAsset().getAssetName() + " balance to sell.");
            }
            asset.setUsableSize(asset.getUsableSize() - order.getSize());
            assetRepository.save(asset);
        }


        // Save order with the proper references
        order.setCustomer(customer.get());
        order.setAsset(asset);

        order.setOrderStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        return convertToDTO(order);
    }


    public List<OrderDTO> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void deleteOrder(Long orderId) throws IllegalStateException{
        // Find the order by its ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        // Ensure the order is in PENDING status
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be canceled.");
        }

        // Get the customer's TRY balance and the asset they are trying to buy/sell
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomer().getId(), "TRY");
        Asset asset = assetRepository.findByCustomerIdAndAssetName(order.getCustomer().getId(), order.getAsset().getAssetName());

        // Handle the refund based on the order side
        if (order.getOrderSideType() == OrderSideType.BUY) {
            // Refund the TRY balance for a BUY order
            tryAsset.setUsableSize(tryAsset.getUsableSize() + (order.getSize() * order.getPrice()));
            assetRepository.save(tryAsset);
        } else if (order.getOrderSideType() == OrderSideType.SELL) {
            // Refund the asset balance for a SELL order
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            assetRepository.save(asset);
        }

        // Delete the order since it's canceled
        orderRepository.delete(order);
    }



    public List<String> listAssets(Long customerId) {
        return orderRepository.findDistinctAssetNamesByCustomerId(customerId);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setAssetName(order.getAsset().getAssetName());
        orderDTO.setOrderSideType(order.getOrderSideType());
        orderDTO.setSize(order.getSize());
        orderDTO.setPrice(order.getPrice());
        orderDTO.setOrderStatus(order.getOrderStatus());
        orderDTO.setCreateDate(order.getCreateDate());
        return orderDTO;
    }
}