package com.arturoglezc.ecommerce2.order;

import com.arturoglezc.ecommerce2.orderitem.OrderItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 100)
    private String customerName;

    @NotNull
    private LocalDateTime orderDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    @AssertTrue
    public boolean isValidStatus() {
        Set<OrderStatus> validStatuses = EnumSet.allOf(OrderStatus.class);
        return status != null && validStatuses.contains(status);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Order() {
    }

    public Order(String customerName, LocalDateTime orderDate, OrderStatus status) {
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.status = status;
    }
}