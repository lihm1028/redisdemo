package com.example.redisdemo.lock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 继承AutoCloseable 在通过try()使用时会自动调用close()方法可以做资源的释放
 */
public class RedisLockImpl implements RedisLock, AutoCloseable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private StringRedisTemplate redisTemplate;

    private String prefix;

    private long time;

    private TimeUnit unit;

    private String randomSeed;

    public RedisLockImpl(StringRedisTemplate redisTemplate, String prefix, long tryTime, TimeUnit unit) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
        this.time = tryTime;
        this.unit = unit;
        this.randomSeed = UUID.randomUUID().toString();
    }

    @Override
    public String getName() {
        return prefix + ".lock";
    }

    /**
     * 有限时间等待加锁
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock() {
        if (StringUtils.isBlank(randomSeed)) {
            throw new RuntimeException("randomSeed 不能为空，必须为随机数");
        }
        long timeoutNanos = System.nanoTime() + unit.toNanos(time);
        while (true && System.nanoTime() <= timeoutNanos) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(getName(), randomSeed, 30L, TimeUnit.SECONDS);
            if (!success) {
                try {
                    final long interval = unit.toMillis(time) / 10;
                    this.logger.debug(Thread.currentThread().getName() + "未能获取Redis锁:{},尝试睡眠:[{}]毫秒后重试", this.getName(), interval);
                    Thread.sleep(interval);
                } catch (InterruptedException var6) {
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;

    }


    @Override
    public void unlock() {
        /**
         * 查询和删除必须原子操作
         */
        String luaScript = "if redis.call('get' ,KEYS[1]) == ARGV[1]\n" + "then\n" +
                " return redis.call('del',KEYS[1])\n" +
                "else\n" +
                "return 0\n" +
                "end";
        final Boolean execute = redisTemplate.execute(new DefaultRedisScript<Boolean>(luaScript, Boolean.class), Arrays.asList(getName()), randomSeed);
        System.out.println(execute);
    }

    @Override
    public void close() throws Exception {
        logger.info("执行AutoCloseable.close()");
        unlock();
    }
}
