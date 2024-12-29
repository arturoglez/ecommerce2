package com.arturoglezc.ecommerce2.order;

import java.time.LocalDateTime;

public class OrderTestConstants {
    public static final String ORDER_CUSTOMER_NAME = "Arturo Gonzalez";
    public static final LocalDateTime ORDER_DATE = LocalDateTime.of(2024, 12, 27, 14, 30, 5);
    public static final OrderStatus ORDER_STATUS = OrderStatus.PENDING;

    public static final String ORDER_CUSTOMER_NAME_ALT = "Arturo Gonzalez";
    public static final LocalDateTime ORDER_DATE_ALT = LocalDateTime.of(2024, 12, 28, 10, 0, 10);
    public static final OrderStatus ORDER_STATUS_ALT = OrderStatus.PAID;

    public static final String ORDER_CUSTOMER_NAME_ALT_2 = "Anna Matushko";
    public static final LocalDateTime ORDER_DATE_ALT_2 = LocalDateTime.of(2024, 12, 29, 18, 45, 2);
    public static final OrderStatus ORDER_STATUS_ALT_2 = OrderStatus.SHIPPED;

    public static final String ORDER_CUSTOMER_NAME_ALT_3 = "Santa Claus";
    public static final LocalDateTime ORDER_DATE_ALT_3 = LocalDateTime.of(2024, 12, 25, 12, 0, 1);
    public static final OrderStatus ORDER_STATUS_ALT_3 = OrderStatus.DELIVERED;
}