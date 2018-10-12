package com.test.controller.portal;


import com.test.common.Const;
import com.test.common.ResponseCode;
import com.test.common.ServerResponse;
import com.test.pojo.User;
import com.test.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 创建订单
     * @param session
     * @param orderNo
     * @return
     */

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Long orderNo, Integer shippingId){

        User user=(User)session.getAttribute(Const.CURRENT_USER);

        if(user==null){
            return ServerResponse.createByErrorCodeMassage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }



    /**
     * 取消订单
     * @param session
     * @param orderNo
     * @return
     */

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo){

        User user=(User)session.getAttribute(Const.CURRENT_USER);

        if(user==null){
            return ServerResponse.createByErrorCodeMassage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }


    @RequestMapping("get_order_cart.do")
    @ResponseBody
    public ServerResponse getOrderCart(HttpSession session, Long orderNo){

        User user=(User)session.getAttribute(Const.CURRENT_USER);

        if(user==null){
            return ServerResponse.createByErrorCodeMassage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }



}
