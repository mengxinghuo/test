package com.test.dao;

import com.test.pojo.Cart;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

   //查询购物车中被选中的商品
    List<Cart> selectCheckedCartByUserId(Integer userId);
}