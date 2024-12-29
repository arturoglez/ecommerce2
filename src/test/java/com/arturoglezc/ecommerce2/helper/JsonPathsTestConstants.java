package com.arturoglezc.ecommerce2.helper;

public class JsonPathsTestConstants {

    //Pagination
    public static final String CONTENT_JSON_PATH = "$.content";
    public static final String TOTAL_ELEMENTS_JSON_PATH = "$.totalElements";
    public static final String TOTAL_PAGES_JSON_PATH = "$.totalPages";
    public static final String PAGE_NUMBER_JSON_PATH = "$.number";

    //Product
    public static final String PRODUCT_NAME_JSON_PATH = "$.name";
    public static final String PRODUCT_PRICE_JSON_PATH = "$.price";
    public static final String PRODUCT_STOCK_JSON_PATH = "$.stock";
    public static final String PRODUCT_NAME_FIRST_ELEMENT_JSON_PATH = "$.content[0].name";
    public static final String PRODUCT_NAME_SECOND_ELEMENT_JSON_PATH = "$.content[1].name";

    //Order
    public static final String ORDER_CUSTOMER_NAME_JSON_PATH = "$.customerName";
    public static final String ORDER_DATE_JSON_PATH = "$.orderDate";
    public static final String ORDER_STATUS_JSON_PATH = "$.status";
    public static final String ORDER_CUSTOMER_NAME_FIRST_ELEMENT_JSON_PATH = "$.content[0].customerName";
    public static final String ORDER_CUSTOMER_NAME_SECOND_ELEMENT_JSON_PATH = "$.content[1].customerName";

    //OrderItem
    public static final String ORDER_ITEM_QUANTITY_JSON_PATH = "$.quantity";
    public static final String ORDER_ITEM_UNIT_PRICE_JSON_PATH = "$.unitPrice";
    public static final String ORDER_ITEM_ORDER_CUSTOMER_NAME_JSON_PATH = "$.order.customerName";
    public static final String ORDER_ITEM_PRODUCT_NAME_JSON_PATH = "$.product.name";
}