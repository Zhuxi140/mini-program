package com.zhuxi.service.Cache;

import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.RedisException;
import com.zhuxi.utils.IdSnowFLake;
import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import src.main.java.com.zhuxi.pojo.DTO.Order.OrderRedisDTO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderRealShowVO;
import src.main.java.com.zhuxi.pojo.VO.Order.OrderShowVO;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderRedisCache {

    private final RedisUntil redisUntil;
    private final RedisCacheProperties rCP;
    private final IdSnowFLake idSnowFLake;

    public OrderRedisCache(RedisUntil redisUntil, RedisCacheProperties rCP, IdSnowFLake idSnowFLake) {
        this.redisUntil = redisUntil;
        this.rCP = rCP;
        this.idSnowFLake = idSnowFLake;
    }


    public String gerOrderLockKey(String orderSn) {
        return rCP.getOrderCache().getOrderLockPrefix() +":" + orderSn;
    }

    public String getOrderKeyByTime(Long userId){
        return rCP.getOrderCache().getOrderSortSetPrefix() +":" + userId;
    }

    public String getOrderDetailKey(String orderSn) {
        return rCP.getOrderCache().getOrderDetailHashPrefix() + ":" + orderSn;
    }

    public String getOrderGroupKey() {
        return rCP.getOrderCache().getOrderGroupPrefix();
    }

    public void saveOrderLock(String orderSn,Integer productQuantity) {
        String key = gerOrderLockKey(orderSn);
        redisUntil.hPut(key,"Quantity",productQuantity);
    }

    public Integer getOrderLock(String orderSn) {
        String key = gerOrderLockKey(orderSn);
        Object o = redisUntil.hGet(key, "Quantity");
        return o == null ? null : (Integer) o;
    }

    public void deleteLockKey(String key){
        String keys = gerOrderLockKey(key);
        redisUntil.delete(keys);
    }

    /**
     * 预加载订单数据到Redis中
     * @param orderRedisList 订单数据
     * @param userId 用户id
     */
    public void syncOrderData(List<OrderRedisDTO> orderRedisList,Long userId){
        String orderListKey = getOrderKeyByTime(userId);
        String orderGroupKey = getOrderGroupKey();
        Map<String,List<String>> Data = new HashMap<>();
        for (OrderRedisDTO oRDto : orderRedisList){
            Long groupId = oRDto.getGroupId();
            String orderSn = oRDto.getOrderSn();
            if (groupId != null){
                String groupIdd = groupId.toString();
                if (Data.containsKey(groupIdd)) {
                    Data.get(groupIdd).add(orderSn);
                }else{
                    Data.put(groupIdd,new ArrayList<>(Collections.singletonList(orderSn)));
                }
            }
        }

        HashMap<Object, Object> HashMap = new HashMap<>();
        redisUntil.executePipeline(p -> {
            ZSetOperations<String, Object> ZSet = p.opsForZSet();
            HashOperations<String, Object, Object> hash = p.opsForHash();
            orderRedisList.forEach(oRDto -> {
                Long id = oRDto.getId();
                LocalDateTime createdAt = oRDto.getCreatedAt();
                int status = oRDto.getStatus();
                idSnowFLake.getIdInt();
                long epochMilli = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                String orderSn = oRDto.getOrderSn();
                String orderDetailKey = getOrderDetailKey(orderSn);
                HashMap.put("id", id);
                HashMap.put("orderSn", orderSn);
                HashMap.put("groupId", oRDto.getGroupId());
                HashMap.put("productName", oRDto.getProductName());
                HashMap.put("specName", oRDto.getSpecName());
                HashMap.put("coverUrl", oRDto.getCoverUrl());
                HashMap.put("totalAmount", oRDto.getTotalAmount());
                HashMap.put("status",status);
                HashMap.put("createdAt", epochMilli);
                hash.putAll(orderDetailKey, HashMap);
                ZSet.add(orderListKey, orderSn, epochMilli);

                switch(status){
                    case 0:{
                        // 待付款
                        p.expire(orderDetailKey,30, TimeUnit.MINUTES);
                    } break;
                    case 1,2,6,4:{
                        // 待发货/待收货/已退款/已取消
                        p.expire(orderDetailKey,7, TimeUnit.DAYS);
                    }  break;
                    case 3:{
                        // 已完成
                        p.expire(orderDetailKey,30, TimeUnit.DAYS);
                    } break;
                }

                HashMap.clear();
            });

            p.expire(orderListKey,30, TimeUnit.DAYS);

            if (!Data.isEmpty()) {
                hash.putAll(orderGroupKey, Data);
                p.expire(orderGroupKey,7, TimeUnit.DAYS);
            }
        });
    }

    /**
     * 获取订单号列表(按时间排序)
     * @param userId 用户id
     * @param lastScore 上一页最后一个订单的时间戳
     * @param pageSize 页大小（默认为10）
     * @return 订单号列表
     */
    public Set<ZSetOperations.TypedTuple<Object>> getOrderSns(Long userId, Long lastScore, Integer pageSize){
        String key = getOrderKeyByTime(userId);

        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisUntil.ZSetReverseRangeScore(
                key,
                lastScore == 0L ? Double.POSITIVE_INFINITY : lastScore,
                0,
                pageSize
        );

        if (CollectionUtils.isEmpty(typedTuples)){
            log.error("获取的Order缓存orderSn为null(未命中)");
            return null;
        }

        return typedTuples;
    }

    /**
     * 获取订单详细信息列表
     * @param typedTuples 订单号列表
     * @return 订单详细信息列表
     */
    public List<OrderRealShowVO> getOrderRealShowVals(Set<ZSetOperations.TypedTuple<Object>> typedTuples){
        LinkedHashSet<String> orderSns = typedTuples
                .stream()
                .map(t -> Objects.requireNonNull(t.getValue()).toString())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<String> keys = orderSns.stream()
                .map(this::getOrderDetailKey)
                .toList();

        Collection<Object> strings = new ArrayList<>(Arrays.asList("id","orderSn", "groupId", "productName", "specName", "coverUrl", "totalAmount", "status", "createdAt"));

        List<Object> results = redisUntil.executePipeline(p -> {
            keys.forEach(key -> {
                p.opsForHash().multiGet(key, strings);
            });
        });

        if (CollectionUtils.isEmpty(results)){
            return null;
        }

        List<OrderShowVO> orderShowVOS = covertOrderShowVO(results);

        return covertOrderRealShowVO(orderShowVOS,orderSns,strings);
    }


    /**
     * 订单详细信息列表转转换为pojo对象
     * @param results 待转换数据
     * @return 订单详细信息列表对象
     */
    private List<OrderShowVO> covertOrderShowVO(List<Object> results){

        return results.stream()
                .map(map -> {
                    if (!(map instanceof List<?> list)) {
                        throw new RedisException(Message.TYPE_TURN_ERROR);
                    }
                    OrderShowVO orderShowVO = new OrderShowVO();
                    orderShowVO.setId(Long.valueOf((Integer) list.get(0)));
                    orderShowVO.setOrderSn((String) list.get(1));
                    Object groupId = list.get(2);
                    Long groupIdd = null;
                    if (groupId instanceof Long) {
                        groupIdd = (Long) groupId;
                    }else if(groupId instanceof Integer groupId1){
                        groupIdd = groupId1.longValue();
                    }
                    orderShowVO.setGroupId(groupIdd);
                    orderShowVO.setProductName((String) list.get(3));
                    orderShowVO.setSpecName((String) list.get(4));
                    orderShowVO.setCoverUrl((String) list.get(5));
                    orderShowVO.setTotalAmount(BigDecimal.valueOf((Double) list.get(6)));
                    orderShowVO.setStatus((Integer) list.get(7));
                   Long created = (Long ) list.get(8);
                    LocalDateTime createdAt = Instant.ofEpochMilli(created).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    orderShowVO.setCreatedAt(createdAt);
                    return orderShowVO;
                }).toList();
    }

    /**
     * 订单详细信息列表转转换为pojo对象
     * @param orderShowVOS 订单详细信息列表对象
     * @param orderSns 订单号列表
     * @param strings 待转换数据
     * @return 订单详细信息列表对象
     */
    private List<OrderRealShowVO> covertOrderRealShowVO(List<OrderShowVO> orderShowVOS,LinkedHashSet<String> orderSns,Collection<Object> strings){
        List<Long> groupIds = orderShowVOS.stream()
                .map(OrderShowVO::getGroupId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, List<String>> groupOrderSns = getGroupOrderSns(groupIds);
        List<OrderShowVO> lackOrderSn = getLackOrderSn(groupOrderSns, orderSns, strings);

        return buildDisplayList(orderSns, orderShowVOS, lackOrderSn, groupOrderSns);
    }

    /**
     * 获取组映射关系
     * @param groupIds 组id列表
     * @return 组映射关系
     */
    private Map<Long,List<String>> getGroupOrderSns(List<Long> groupIds){
        String orderGroupKey = getOrderGroupKey();
        if (groupIds == null || CollectionUtils.isEmpty(groupIds)){
            return new HashMap<>();
        }
        Collection<Object> list = groupIds.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        Map<Long,List<String>> groupOrderSns =  new HashMap<>();
        List<Object> results = redisUntil.hMultiGet(orderGroupKey, list);
        for(int i = 0; i < groupIds.size() && i < results.size(); i++){
            Long groupId = new ArrayList<>(groupIds).get(i);
            Object result = results.get(i);
            if (!(result instanceof List<?> resultList)){
                log.warn("组映射格式错误");
                continue;
            }

            List<String> orderSns = resultList.stream()
                    .map(String::valueOf)
                    .toList();

            if(CollectionUtils.isEmpty(orderSns)){
                log.warn("组映射为空");
                continue;
            }
            groupOrderSns.put(groupId,orderSns);
        }

        return groupOrderSns;
    }

    /**
     * 获取缺少的订单号列表
     * @param groupOrderSns 组映射
     * @param orderSns 订单号列表
     * @param strings 待转换数据
     * @return 缺少的订单号列表
     */
    private List<OrderShowVO> getLackOrderSn(Map<Long,List<String>> groupOrderSns, LinkedHashSet<String> orderSns,Collection<Object> strings){
            if (CollectionUtils.isEmpty(groupOrderSns)){
                return new ArrayList<>();
            }
        Set<String> collect = groupOrderSns.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        Set<String> needQueryOrderSns = collect.stream()
                .filter(s -> !orderSns.contains(s))
                .collect(Collectors.toSet());
        List<String> key = needQueryOrderSns.stream()
                .map(this::getOrderDetailKey)
                .toList();

        List<Object> objects = redisUntil.executePipeline(pipe -> {
            key.forEach(t -> pipe.opsForHash().multiGet(t, strings));
        });

        return covertOrderShowVO(objects);
    }

    /**
     * 构建显示列表（严格保留原顺序，合并组订单）
     * @param currentOrderSns 原当前页`orderSn`列表
     * @param currentOrderVOs 当前页订单详情（含`groupId`）
     * @param supplementOrderVOs 补充的订单详情（组内未在当前页的订单）
     * @param groupOrderSns 组映射（`groupId`→该组所有`orderSn`）
     * @return 显示列表（混合`OrderShowVO`和`OrderRealShowVO`，保持原顺序）
     */
    private List<OrderRealShowVO> buildDisplayList(
            LinkedHashSet<String> currentOrderSns,
            List<OrderShowVO> currentOrderVOs,
            List<OrderShowVO> supplementOrderVOs,
            Map<Long, List<String>> groupOrderSns
    ){
        List<OrderShowVO> orderShowVOS = mergeOrderVOs(currentOrderVOs, supplementOrderVOs);
        Map<String,OrderShowVO> orderSnToVO = orderShowVOS.stream()
                .collect(Collectors.toMap(OrderShowVO::getOrderSn, v -> v,(oldVal, newVal)-> oldVal));
        Map<Long, List<OrderShowVO>> longListMap = buildGroupToVOs(groupOrderSns, orderSnToVO);

        List<OrderRealShowVO> displayList = new ArrayList<>();
        Set<Long> processedGroups = new HashSet<>();
        for (String orderSn : currentOrderSns){
            OrderShowVO vo = orderSnToVO.get(orderSn);
            if (vo == null){
                continue;
            }
            Long groupId = vo.getGroupId();
            if (groupId == null){
                OrderRealShowVO orderRealShowVO = new OrderRealShowVO(null,Collections.singletonList(vo));
                displayList.add(orderRealShowVO);
            }else{
                if (!processedGroups.contains(groupId)){
                    List<OrderShowVO> groupOrderVOS = longListMap.get(groupId);
                    if (groupOrderVOS != null && !groupOrderVOS.isEmpty()){
                        OrderRealShowVO orderRealShowVO = new OrderRealShowVO(groupId,groupOrderVOS);
                        displayList.add(orderRealShowVO);
                        processedGroups.add(groupId);
                    }else {
                        OrderRealShowVO orderRealShowVO = new OrderRealShowVO(null,Collections.singletonList(vo));
                        displayList.add(orderRealShowVO);
                    }
                }
            }
        }

        return displayList;
    }

    /**
     * 合并当前订单与补充订单（去重）
     * @param currentOrderVOs 当前页订单详情
     * @param supplementOrderVOs 补充的订单详情
     * @return 合并后的订单详情列表（无重复）
     */
    private List<OrderShowVO> mergeOrderVOs(List<OrderShowVO> currentOrderVOs, List<OrderShowVO> supplementOrderVOs) {
        Set<OrderShowVO> orderVOSet = new LinkedHashSet<>(currentOrderVOs);
        if (supplementOrderVOs != null){
            orderVOSet.addAll(supplementOrderVOs);
        }
        return new ArrayList<>(orderVOSet);
    }

    /**
     * 构建`groupId`→组内订单列表映射
     * @param groupOrderSns 组映射（`groupId`→该组所有`orderSn`）
     * @param orderSnToVO `orderSn`→`OrderShowVO`映射
     * @return `groupId`→组内订单列表（按`createAt`降序）
     */
    private Map<Long, List<OrderShowVO>> buildGroupToVOs(Map<Long, List<String>> groupOrderSns, Map<String, OrderShowVO> orderSnToVO) {
        Map<Long, List<OrderShowVO>> groupToVOs = new HashMap<>();
        if (groupOrderSns == null) {
            return groupToVOs;
        }
        for (Map.Entry<Long, List<String>> entry : groupOrderSns.entrySet()) {
            Long groupId = entry.getKey();
            List<String> orderSns = entry.getValue();
            // 获取组内所有订单详情（按`orderSn`顺序）
            List<OrderShowVO> groupVOs = orderSns.stream()
                    .map(orderSnToVO::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // 按`createAt`降序排序（与SortSet顺序一致）
            groupVOs.sort(Comparator.comparing(OrderShowVO::getCreatedAt).reversed());
            groupToVOs.put(groupId, groupVOs);
        }
        return groupToVOs;
    }


    /**
     * 获取最后一条数据的分数
     * @param orderRealShowVOS 订单列表
     * @return 最后一条数据的分数
     */
    public Long getLastScores(List<OrderRealShowVO> orderRealShowVOS){
        List<OrderShowVO> orderShowVO = orderRealShowVOS.get(orderRealShowVOS.size() - 1).getOrderShowVO();
        OrderShowVO vo = orderShowVO.get(orderShowVO.size() - 1);
        LocalDateTime createdAt = vo.getCreatedAt();
        long epochMilli = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return epochMilli;
    }


    /**
     * 根据`orderSn`获取订单ID
     * @param orderSn 订单编号
     * @return 订单ID
     */
    public Long getOrderIdBySn(String orderSn){
        String orderDetailKey = getOrderDetailKey(orderSn);
        Object orderId = redisUntil.hGet(orderDetailKey, "id");
        if ( orderId instanceof Long){
            return (Long) orderId;
        }else if(orderId instanceof Integer){
            return Long.valueOf(orderId.toString());
        }
        return null;
    }

    public void deleteOrder(String orderSn){
        String orderDetailKey = getOrderDetailKey(orderSn);
        redisUntil.delete(orderDetailKey);
        redisUntil.deleteZSetOneFiled(getOrderGroupKey(), orderSn);
    }

    public void syncOrderStatus(String orderSn,Integer status){

        String orderDetailKey = getOrderDetailKey(orderSn);
        redisUntil.hPut(orderDetailKey, "status", status);
    }





}
