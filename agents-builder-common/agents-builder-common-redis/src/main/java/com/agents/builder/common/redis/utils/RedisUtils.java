package com.agents.builder.common.redis.utils;


import com.agents.builder.common.core.utils.SpringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * redis操作
 *
 * @author Angus
 * @date 2022/06/13
 * @since 1.0.0
 **/
public  class RedisUtils {

    private static final RedisTemplate<String, Object> redisTemplate = SpringUtils.getBean("redisTemplate");



    public static void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
    }


    public static void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }


    public static Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    public static Boolean del(String key) {
        return redisTemplate.delete(key);
    }


    public static Long del(List<String> keys) {
        return redisTemplate.delete(keys);
    }


    public static Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }


    public static Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    public static Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    public static Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }


    public static Long incrExpire(String key, long time) {
        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (count != null && count == 1) {
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return count;
    }


    public static Long decr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, -delta);
    }


    public static Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }


    public static Boolean hSet(String key, String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return expire(key, time);
    }


    public static void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }


    public static Map hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    public static Boolean hSetAll(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        return expire(key, time);
    }


    public static void hSetAll(String key, Map<String, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }


    public static void hDel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }


    public static Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }


    public static Long hIncr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }


    public static Long hDecr(String key, String hashKey, Long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, -delta);
    }


    public static Double zIncr(String key, Object value, Double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }


    public static Double zDecr(String key, Object value, Double score) {
        return redisTemplate.opsForZSet().incrementScore(key, value, -score);
    }


    public static Map<Object, Double> zReverseRangeWithScore(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end)
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));
    }


    public static Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }


    public static Map<Object, Double> zAllScore(String key) {
        return Objects.requireNonNull(redisTemplate.opsForZSet().rangeWithScores(key, 0, -1))
                .stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));
    }


    public static Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }


    public static Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }




    public static Long sAddExpire(String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        expire(key, time);
        return count;
    }


    public static Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }


    public static Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }


    public static Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }


    public static List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }


    public static Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }


    public static Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }


    public static Long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }


    public static Long lPush(String key, Object value, long time) {
        Long index = redisTemplate.opsForList().rightPush(key, value);
        expire(key, time);
        return index;
    }


    public static Long lPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }


    public static Long lPushAll(String key, Long time, Object... values) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        expire(key, time);
        return count;
    }


    public static Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }


    public static Boolean bitAdd(String key, int offset, boolean b) {
        return redisTemplate.opsForValue().setBit(key, offset, b);
    }


    public static Boolean bitGet(String key, int offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }


    public static Long bitCount(String key) {
        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }


    public static List<Long> bitField(String key, int limit, int offset) {
        return redisTemplate.execute((RedisCallback<List<Long>>) con ->
                con.bitField(key.getBytes(),
                        BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(limit)).valueAt(offset)));
    }


    public static byte[] bitGetAll(String key) {
        return redisTemplate.execute((RedisCallback<byte[]>) con -> con.get(key.getBytes()));
    }


    public static Long hyperAdd(String key, Object... value) {
        return redisTemplate.opsForHyperLogLog().add(key, value);
    }


    public static Long hyperGet(String... key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }


    public static void hyperDel(String key) {
        redisTemplate.opsForHyperLogLog().delete(key);
    }


    public static Long geoAdd(String key, Double x, Double y, String name) {
        return redisTemplate.opsForGeo().add(key, new Point(x, y), name);
    }


    public static List<Point> geoGetPointList(String key, Object... place) {
        return redisTemplate.opsForGeo().position(key, place);
    }


    public static Distance geoCalculationDistance(String key, String placeOne, String placeTow) {
        return redisTemplate.opsForGeo()
                .distance(key, placeOne, placeTow, RedisGeoCommands.DistanceUnit.KILOMETERS);
    }


    public static GeoResults<RedisGeoCommands.GeoLocation<Object>> geoNearByPlace(String key, String place, Distance distance, long limit, Sort.Direction sort) {
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates();
        // 判断排序方式
        if (Sort.Direction.ASC == sort) {
            args.sortAscending();
        } else {
            args.sortDescending();
        }
        args.limit(limit);
        return redisTemplate.opsForGeo()
                .radius(key, place, distance, args);
    }


    public static List<String> geoGetHash(String key, String... place) {
        return redisTemplate.opsForGeo()
                .hash(key, place);
    }

    public static Set<String> keys(String pattern) {
        if ("*".equals(pattern)){
            throw new IllegalArgumentException("pattern 不能为 *");
        }
        return redisTemplate.keys(pattern);
    }

}
