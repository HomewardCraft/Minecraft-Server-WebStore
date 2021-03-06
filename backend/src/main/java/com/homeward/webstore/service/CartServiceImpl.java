package com.homeward.webstore.service;

import com.homeward.webstore.common.enums.StatusEnum;
import com.homeward.webstore.common.util.CartUtils;
import com.homeward.webstore.common.util.CommonUtils;
import com.homeward.webstore.common.util.JwtUtils;
import com.homeward.webstore.mapper.AuthenticationMapper;
import com.homeward.webstore.mapper.CartMapper;
import com.homeward.webstore.mapper.ItemMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final AuthenticationMapper authenticationMapper;
    private final ItemMapper itemMapper;
    private final CartMapper cartMapper;


    public CartServiceImpl(AuthenticationMapper authenticationMapper, ItemMapper itemMapper, CartMapper cartMapper) {
        this.authenticationMapper = authenticationMapper;
        this.itemMapper = itemMapper;
        this.cartMapper = cartMapper;
    }


    /**
     * 返回购物车内的所有商品的价格总和
     * @return Float
     */
    @Override
    public Float commit() {
        String uuid = JwtUtils.getUUID();
        return cartMapper.getTotalPrice(uuid);
    }


    /**
     * 创建用户的购物车
     * @param itemId
     * @param request
     */
    @Override
    @Transactional
    public void insertCart(Integer itemId, HttpServletRequest request) {
        String uuid = JwtUtils.getUUID();

        // 传入的商品id是否为空
        if (itemMapper.getItemName(itemId) == null) {
            CommonUtils.throwRuntimeException(StatusEnum.ITEM_NOT_FOUND);
        }

        // 从cart表查找玩家购物车信息
        List<Integer> itemIdList = authenticationMapper.itemIdList(uuid, itemId);

        // 是否有重复的购物车
        CartUtils.isSingleColumn(itemIdList);

        // 玩家是否已经有购物车了
        if (itemIdList.contains(itemId)) {
            CommonUtils.throwRuntimeException(StatusEnum.DUPLICATE_CART_FOUND);
        }

        Integer itemAmount = 1;

        cartMapper.insertCart(uuid, itemId, itemAmount);
    }

    /**
     * 更新用户的购物车
     * @param map
     * @param request
     */
    @Override
    @Transactional
    public void updateCart(Map<String, String> map, HttpServletRequest request) {
        String uuid = JwtUtils.getUUID();

        Set<String> keySet =map.keySet();
        String idString = null;
        String value = null;
        for (String key : keySet) {
            if (key.contains("quantity")) {
                /* 获取value */
                value = map.get(key);
                /* 格式化并获取key */
                idString = key.replaceAll("[^0-9]", "");
                break;
            }
        }

        // 如果没获取到表示传入的key格式不合法或者为空
        if (StringUtils.isBlank(idString)) {
            CommonUtils.throwRuntimeException(StatusEnum.FORM_DATA_INVALID);
        }

        // 商品id是否合法
        if (value == null || value.equals("") ||
                Integer.parseInt(value) < 1 || Integer.parseInt(value) > 99) {
            CommonUtils.throwRuntimeException(StatusEnum.ITEM_AMOUNT_INVALID);
        }

        Integer itemAmount = Integer.parseInt(value);

        Integer itemId = Integer.parseInt(idString);

        List<Integer> itemIdList = authenticationMapper.itemIdList(uuid, itemId);

        CartUtils.isSingleColumn(itemIdList);

        // 玩家是否拥有购物车
        if (!itemIdList.contains(itemId)) {
            CommonUtils.throwRuntimeException(StatusEnum.CART_CANNOT_UPDATE);
        }

        cartMapper.updateCart(uuid, itemId, itemAmount);
    }


    /**
     * 删除用户的购物车
     * @param itemId item id
     */
    @Override
    @Transactional
    public void deleteCart(Integer itemId, HttpServletRequest request) {
        String uuid = JwtUtils.getUUID();

        List<Integer> itemIdList = authenticationMapper.itemIdList(uuid, itemId);

        CartUtils.isSingleColumn(itemIdList);

        if (!itemIdList.contains(itemId)) {
            CommonUtils.throwRuntimeException(StatusEnum.CART_CANNOT_DELETE);
        }

        cartMapper.deleteCart(uuid, itemId);
    }
}