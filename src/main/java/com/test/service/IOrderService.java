package com.test.service;

import com.test.common.ServerResponse;

public interface IOrderService {

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String>cancel(Integer userId,Long orderNo);
}
