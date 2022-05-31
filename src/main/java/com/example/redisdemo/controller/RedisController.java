package com.example.redisdemo.controller;

import com.example.redisdemo.lock.RedisLockImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    private RedissonClient redissonClient;

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

    /**
     * springboot集成的redis锁
     */
    @GetMapping("/lock2")
    public void lock2() {

        for (int i = 0; i < 5; i++) {

            executor.submit(() -> {


                final RLock rLock = redissonClient.getLock("redisson");
                try {
//                    rLock.lock();//一直锁阻塞，并自动续期
//                    rLock.lock(3, TimeUnit.SECONDS); //最大锁定期限3秒
                    if (rLock.tryLock(3, TimeUnit.SECONDS))//有限的等待获取锁，超过时间返回false
                    {
                        logger.info(Thread.currentThread().getName() + "获取到锁true :");
                        Thread.sleep(80000);
                        rLock.unlock();
                        logger.info(Thread.currentThread().getName() + "释放锁 :");
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
