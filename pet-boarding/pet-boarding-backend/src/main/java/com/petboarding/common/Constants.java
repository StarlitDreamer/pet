package com.petboarding.common;

public class Constants {

    // User roles
    public static final Integer ROLE_OWNER = 1;
    public static final Integer ROLE_PROVIDER = 2;

    // Order statuses (match database definition)
    // 0-待支付 1-待接单 2-已接单 3-已入住 4-寄养中 5-已退房 6-已完成 7-已取消 8-已拒绝
    public static final Integer ORDER_PENDING = 0;
    public static final Integer ORDER_PAID = 1;
    public static final Integer ORDER_ACCEPTED = 2;
    public static final Integer ORDER_CHECKED_IN = 3;
    public static final Integer ORDER_BOARDING = 4;
    public static final Integer ORDER_CHECKED_OUT = 5;
    public static final Integer ORDER_COMPLETED = 6;
    public static final Integer ORDER_CANCELLED = 7;
    public static final Integer ORDER_REJECTED = 8;

    // Shop status
    public static final Integer SHOP_CLOSED = 0;
    public static final Integer SHOP_ACTIVE = 1;
    public static final Integer SHOP_REVIEWING = 2;

    // Service status
    public static final Integer SERVICE_OFF_SHELF = 0;
    public static final Integer SERVICE_ON_SHELF = 1;

    // Complaint status
    public static final Integer COMPLAINT_PENDING = 0;
    public static final Integer COMPLAINT_PROCESSING = 1;
    public static final Integer COMPLAINT_RESOLVED = 2;
    public static final Integer COMPLAINT_DISMISSED = 3;

    // Message read status
    public static final Integer MESSAGE_UNREAD = 0;
    public static final Integer MESSAGE_READ = 1;

    // Coupon status
    public static final Integer COUPON_UNUSED = 0;
    public static final Integer COUPON_USED = 1;
    public static final Integer COUPON_EXPIRED = 2;

    private Constants() {
    }
}
