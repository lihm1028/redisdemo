package com.example.redisdemo.controller;

import com.alibaba.fastjson.JSON;
import com.example.redisdemo.lock.RedisLockImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/")
public class RedisController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private StringRedisTemplate redisTemplate;


    private ExecutorService executor = Executors.newFixedThreadPool(5);


    @GetMapping("/initRank")
    public void initRank() {
        String SCORE_RANK="score_rank";

    redisTemplate.opsForZSet()
            .add(SCORE_RANK,"lihm",1);

        Set<ZSetOperations.TypedTuple<String>> tuples=new HashSet<>();

        tuples.add(new DefaultTypedTuple<>("horse",2.2));
        tuples.add(new DefaultTypedTuple<>("tianxin",1.2));

        redisTemplate.opsForZSet()
                .add(SCORE_RANK,tuples);


    }

    @GetMapping("/rank")
    public void list() {
        String SCORE_RANK="score_rank";

        Set<String> range = redisTemplate.opsForZSet().reverseRange(SCORE_RANK, 0, 10);
        System.out.println("获取到的排行列表:" + JSON.toJSONString(range));
        Set<ZSetOperations.TypedTuple<String>> rangeWithScores = redisTemplate.opsForZSet().reverseRangeWithScores(SCORE_RANK, 0, 10);
        System.out.println("获取到的排行和分数列表:" + JSON.toJSONString(rangeWithScores));
    }

    /**
     * springboot集成的redis锁
     */
    @GetMapping("/lock1")
    public void lock1() {

        for (int i = 0; i < 5; i++) {

            executor.submit(() -> {

                try (RedisLockImpl lock = new RedisLockImpl(redisTemplate, "test", 2, TimeUnit.SECONDS)) {
                    if (lock.tryLock()) {
                        logger.info(Thread.currentThread().getName() + "获取到锁true :");
                        Thread.sleep(200);
                        lock.unlock();
                        logger.info(Thread.currentThread().getName() + "释放锁 :");
                    } else {
                        logger.info(Thread.currentThread().getName() + "未获取到锁false:");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    logger.info(Thread.currentThread().getName() + " finally :");
                }


            }, "T" + i);

        }
    }


}
