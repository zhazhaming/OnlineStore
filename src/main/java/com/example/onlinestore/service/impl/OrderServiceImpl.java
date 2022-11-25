package com.example.onlinestore.service.impl;

import com.example.onlinestore.common.Constant;
import com.example.onlinestore.exception.OnlineStore_Exception;
import com.example.onlinestore.exception.OnlineStore_ExceptionEnum;
import com.example.onlinestore.filter.UserFilter;
import com.example.onlinestore.model.dao.CartMapper;
import com.example.onlinestore.model.dao.OrderItemMapper;
import com.example.onlinestore.model.dao.OrderMapper;
import com.example.onlinestore.model.dao.ProductMapper;
import com.example.onlinestore.model.pojo.Order;
import com.example.onlinestore.model.pojo.OrderItem;
import com.example.onlinestore.model.pojo.Product;
import com.example.onlinestore.model.request.CreateOrderReq;
import com.example.onlinestore.model.vo.CartVO;
import com.example.onlinestore.model.vo.OrderItemVO;
import com.example.onlinestore.model.vo.OrderVO;
import com.example.onlinestore.service.CartService;
import com.example.onlinestore.service.OrderService;
import com.example.onlinestore.service.UserService;
import com.example.onlinestore.utils.OrderCodeFactory;
import com.example.onlinestore.utils.QRCodeGenerator;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    CartService cartService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    UserService userService;

    @Value ("${file.upload.ip}")
    String ip;

    // 数据库事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String create(CreateOrderReq createOrderReq){
        //拿到用户id
        Integer userId = UserFilter.currUser.getId ( );
        //从购物车中查找已经勾选的商品
        List<CartVO> CartVOList = cartService.list (userId);
        ArrayList<CartVO> CartTemp = new ArrayList<> (  );
        for (int i = 0; i < CartVOList.size ( ); i++) {
            CartVO cartVO =  CartVOList.get (i);
            if (cartVO.getSelected ().equals (Constant.CartStatus.CHECKED)){
                CartTemp.add (cartVO);
            }
        }
        CartVOList = CartTemp;
        if (CartVOList==null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.CART_EMPTY);
        }
        //判断商品是否存在、上下架、库存
        vailSaleStatusAndStock(CartVOList);
        //把购物车对象转化为订单
        List<OrderItem> orderItemList = cartVOListToOrderItemList (CartVOList);
        //扣除库存
        for (int i = 0; i < orderItemList.size ( ); i++) {
            OrderItem orderItem =  orderItemList.get (i);
            Product product = productMapper.selectByPrimaryKey (orderItem.getProductId ());
            int stock = product.getStock ()-orderItem.getQuantity ();
            if (stock<0){
                throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NOT_ENOUGH);
            }
            product.setStock (stock);
            productMapper.updateByPrimaryKeySelective (product);
        }
        //把购物车中已勾选的商品删除
        cleanCart(CartVOList);
        // 生成订单
        Order order = new Order ();
        // 生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode (Long.valueOf (userId));
        order.setOrderNo (orderNo);
        order.setUserId (userId);
        order.setTotalPrice (totalPrice(orderItemList));
        order.setReceiverName (createOrderReq.getReceiverName ());
        order.setReceiverMobile (createOrderReq.getReceiverMobile ());
        order.setReceiverAddress (createOrderReq.getReceiverAddress ());
        order.setOrderStatus (Constant.OrderStatusEnum.NOT_PAID.getCode ( ));
        order.setPostage (0);
        order.setPaymentType (1);
        //插入到order表中
        orderMapper.insertSelective (order);
        // 循环保存每个商品到Order_item
        for (int i = 0; i < orderItemList.size ( ); i++) {
            OrderItem orderItem = orderItemList.get (i);
            orderItem.setOrderNo (order.getOrderNo ());
            orderItemMapper.insertSelective (orderItem);
        }
        // 放回结果
        return orderNo;
    }

    //计算购物车的总价值
    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalprice=0;
        for (int i = 0; i < orderItemList.size ( ); i++) {
            OrderItem orderItem = orderItemList.get (i);
            totalprice += orderItem.getTotalPrice ();
        }
        return totalprice;
    }

    //清除购物车中已购买的商品
    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size ( ); i++) {
            CartVO cartVO = cartVOList.get (i);
            cartMapper.deleteByPrimaryKey (cartVO.getId ());
        }
    }

    //将购物车中的每一个商品转化为每一个商品订单
    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<> (  );
        for (int i = 0; i < cartVOList.size ( ); i++) {
            CartVO cartVO = cartVOList.get (i);
            OrderItem orderItem = new OrderItem ( );
            orderItem.setProductId (cartVO.getProductId ());
            orderItem.setProductName (cartVO.getProductName ());
            orderItem.setProductImg (cartVO.getProductImage ());
            orderItem.setUnitPrice (cartVO.getPrice ());
            orderItem.setTotalPrice (cartVO.getTotalprice ());
            orderItem.setQuantity (cartVO.getQuantity ());
            orderItemList.add (orderItem);
        }
        return orderItemList;
    }

    // 验证商品的上下架状态，和库存
    private void vailSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size ( ); i++) {
            CartVO cartVO =  cartVOList.get (i);
            //判断商品是否存在、状态是否上架
            Product product = productMapper.selectByPrimaryKey (cartVO.getProductId ());
            if (product==null || product.getStatus ().equals (Constant.SaleStatus.NOT_SALE)){
                throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if (cartVO.getQuantity ()>product.getStock ()){
                throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detail(String OrderNo){
        Order order = orderMapper.selectByOrderNo (OrderNo);
        if (order==null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_ORDER);
        }
        Integer userId = UserFilter.currUser.getId ( );
        if (order.getUserId ()!=userId){
            throw  new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_YOUR_ORDER);
        }
        OrderVO orderVO = getOrderVO( order);

        return orderVO;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO ();
        BeanUtils.copyProperties (order,orderVO);
        //拼接orderItemList
        List<OrderItem> orderItemList = orderItemMapper.selectByorderNo (order.getOrderNo ( ));
        List<OrderItemVO> orderItemVOList = new ArrayList<> (  );
        for (int i = 0; i < orderItemList.size ( ); i++) {
            OrderItem orderItem = orderItemList.get (i);
            OrderItemVO orderItemVO = new OrderItemVO ();
            BeanUtils.copyProperties (orderItem,orderItemVO);
            orderItemVOList.add (orderItemVO);
        }
        orderVO.setOrderItemVOList (orderItemVOList);
        orderVO.setOrderStatusName (Constant.OrderStatusEnum.codeOf (orderVO.getOrderStatus ()).getValue ());
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum,Integer pageSize){
        Integer userId = UserFilter.currUser.getId ( );
        PageHelper.startPage (pageNum,pageSize);
        List<Order> orderList = orderMapper.selectForCustomer (userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo orderPageInfo = new PageInfo<> ( orderList );
        orderPageInfo.setList (orderVOList);
        return orderPageInfo;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<> (  );
        for (int i = 0; i < orderList.size ( ); i++) {
            Order order = orderList.get (i);
            OrderVO orderVO = getOrderVO (order);
            orderVOList.add (orderVO);
        }
        return orderVOList;
    }

    /**
     * 取消订单
     * @param orderNo
     */
    @Override
    public void cancel(String orderNo){
        // 查询是否有该订单
        Order order = orderMapper.selectByOrderNo (orderNo);
        if (order==null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_ORDER);
        }
        // 验证用户身份
        Integer userId = UserFilter.currUser.getId ( );
        if (! order.getUserId ().equals (userId)){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_YOUR_ORDER);
        }
        // 判断订单状态（是否付款）
        if (order.getOrderStatus ().equals (Constant.OrderStatusEnum.NOT_PAID.getCode ())){
            //执行取消操作
            order.setOrderStatus (Constant.OrderStatusEnum.CANCELED.getCode ());
            order.setEndTime (new Date (  ));
            orderMapper.updateByPrimaryKeySelective (order);
        }else {
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.WRONG_ORDER);
        }
    }

    @Override
    public String qrcode(String orderNo){
        //获取路径
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes ( );
        HttpServletRequest request = requestAttributes.getRequest ( );
        //获取端口号
        String address = ip+":"+request.getLocalPort ( );
        String payUrl = "Http://"+address+"/pay?orderNo="+orderNo;
        //生成二维码图片
        try {
            QRCodeGenerator.generateQRCodeImage (payUrl,350,350,Constant.FILER_UPLOAD_DIR+orderNo+".png");
        } catch (WriterException e) {
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        //生成访问二维码路径
        String pngAddress = "http://"+address+"/images/"+orderNo+".png";
        return pngAddress;
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize){
        PageHelper.startPage (pageNum,pageSize);
        List<Order> orderList = orderMapper.selectForAdmin ();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo orderPageInfo = new PageInfo<> ( orderList );
        orderPageInfo.setList (orderVOList);
        return orderPageInfo;
    }

    @Override
    public void pay(String orderNo){
        Order order = orderMapper.selectByOrderNo (orderNo);
        if (order==null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus ().equals (Constant.OrderStatusEnum.NOT_PAID.getCode ())){
            order.setOrderStatus (Constant.OrderStatusEnum.PAID.getCode ( ));
            order.setPayTime (new Date (  ));
            orderMapper.updateByPrimaryKeySelective (order);
        }else {
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.WRONG_ORDER);
        }
    }

    @Override
    public void delivered(String orderNo){
        Order order = orderMapper.selectByOrderNo (orderNo);
        if (order==null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_ORDER);
        }
        if (order.getOrderStatus ().equals (Constant.OrderStatusEnum.PAID.getCode ())){
            order.setOrderStatus (Constant.OrderStatusEnum.DELIVERED.getCode ( ));
            order.setDeliveryTime (new Date (  ));
            orderMapper.updateByPrimaryKeySelective (order);
        }else {
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.WRONG_ORDER);
        }
    }

    @Override
    public void finish(String orderNo){
        Order order = orderMapper.selectByOrderNo (orderNo);
        if (order==null){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_ORDER);
        }
        if (!userService.checkAdminRole (UserFilter.currUser)&& !
                order.getUserId ().equals (UserFilter.currUser.getId ())){
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.NO_YOUR_ORDER);
        }
        if (order.getOrderStatus ().equals (Constant.OrderStatusEnum.DELIVERED.getCode ())){
            order.setOrderStatus (Constant.OrderStatusEnum.FINISHED.getCode ( ));
            order.setEndTime (new Date (  ));
            orderMapper.updateByPrimaryKeySelective (order);
        }else {
            throw new OnlineStore_Exception (OnlineStore_ExceptionEnum.WRONG_ORDER);
        }

    }
}
