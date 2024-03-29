package com.example.redisdemo.controller;

import com.example.redisdemo.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redisson")
public class RedissonController {

    RRateLimiter rateLimiter;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedissonClient redissonClient;


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 订阅主题
     *
     * @param redissonClient
     * @return
     */
    @Bean
    CommandLineRunner commandLineRunner1(RedissonClient redissonClient) {
        return args -> {
            final RTopic myTopic = redissonClient.getTopic("myTopic");


//            RShardedTopic myTopic = redissonClient.getShardedTopic("myTopic");

            myTopic.addListener(Book.class, new MessageListener<Book>() {

                @Override
                public void onMessage(CharSequence channel, Book msg) {
                    logger.info("[commandLineRunner1]收到主题:{}的消息内容：{}", String.valueOf(channel), msg);

                }
            });

            myTopic.addListener(Book.class, new MessageListener<Book>() {

                @Override
                public void onMessage(CharSequence channel, Book msg) {
                    logger.info("[commandLineRunner11]收到主题:{}的消息内容：{}", channel.toString(), msg);

                }
            });


        };
    }

    /**
     * 订阅主题
     *
     * @param redissonClient
     * @return
     */
    @Bean
    CommandLineRunner commandLineRunner2(RedissonClient redissonClient) {
        return args -> {
            final RTopic myTopic = redissonClient.getTopic("myTopic");
//            RShardedTopic myTopic = redissonClient.getShardedTopic("myTopic");

            myTopic.addListener(Book.class, new MessageListener<Book>() {

                @Override
                public void onMessage(CharSequence channel, Book msg) {
                    logger.info("[commandLineRunner2]收到主题:{}的消息内容：{}", String.valueOf(channel), msg);

                }
            });


        };
    }

    /**
     * redisson
     */
    @GetMapping("/string")
    public String string() {

        /**
         * 字符串处理
         */
        RBucket<String> test = redissonClient.getBucket("string", StringCodec.INSTANCE);
        final String value = test.get();
        test.set("lihm1", 30, TimeUnit.SECONDS); //设置过期的key
        test.set("lihm2");
        return value;
    }

    /**
     * 字符串对象
     */
    @GetMapping("/obj")
    public Book stringobj() {

        /**
         * 字符串处理
         */
        RBucket<Book> test = redissonClient.getBucket("book");
        final Book value = test.get();
        Book book = new Book("史记", "sunshine1028@foxmail.com");
        test.set(book);
        return value;
    }

    /**
     * 哈希对象
     */
    @GetMapping("/hash")
    public Map<String, String> hash() {
        /**
         * 哈希对象
         */
        RMap<String, String> map = redissonClient.getMap("hash");
        final RMap<String, String> old = map;

        map.put("name", "lihm");
        map.put("email", "sunshine1028@foxmail.com");
        return old;
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    public List<String> list() {
        /**
         * 列表对象
         */
        RList<String> rList = redissonClient.getList("list");
        final List<String> result = rList.readAll();
        rList.add("孔子");
        rList.add("孟子");
        rList.add("荀子");
        return result;
    }

    /**
     * 集合
     */
    @GetMapping("/set")
    public Set<String> set() {
        /**
         * 字符串处理
         */
        RSet<String> rSet = redissonClient.getSet("set", StringCodec.INSTANCE);
        final Set<String> result = rSet.readAll();

        rSet.add("孔子-性善论");
        rSet.add("孟子-老师（孔子孙子子思）");
        rSet.add("荀子-性恶论");
        rSet.add("韩非");
        rSet.add("秦始皇宰相李斯");
        rSet.add("lihm");
        return result;
    }

    /**
     * 有序集合
     */
    @GetMapping("/zset")
    public Collection<String> zset() {
        /**
         * 有序集合
         */
        RSortedSet<String> rSet = redissonClient.getSortedSet("zset");
        final Collection<String> result = rSet.readAll();


        rSet.add("孔子-性善论");
        rSet.add("孟子-老师（孔子孙子子思）");
        rSet.add("荀子-性恶论");
        rSet.add("韩非");
        rSet.add("秦始皇宰相李斯");
        rSet.add("lihm");

        return result;
    }

    /**
     * 计分排序集
     * ScoredSortedSet
     */
    @GetMapping("/zscoredset")
    public Collection<String> zscoredset() {
        /**
         * 计分排序集
         */
        RScoredSortedSet<String> rSet = redissonClient.getScoredSortedSet("ScoredSortedSet",StringCodec.INSTANCE);
        final Collection<String> result = rSet.readAll();
        rSet.add(0.33, "孔子-性善论");
        rSet.add(0.251, "孟子-老师（孔子孙子子思）");
        rSet.add(0.302, "荀子-性恶论");
        rSet.add(0.5, "韩非");
        rSet.add(1, "秦始皇宰相李斯");
        rSet.add(1.2, "lihm");
        rSet.rank("lihm"); // 获取元素在集合中的位置
        rSet.getScore("lihm"); // 获取元素的评分

        return result;
    }

    /**
     * 队列
     */
    @GetMapping("/queue")
    public Collection<Book> queue() {
        /**
         * 分布式队列
         */
        RQueue<Book> rQueue = redissonClient.getQueue("queue");
        final Collection<Book> result = rQueue.readAll();

        rQueue.add(new Book("孔子", "1@qq.com"));
        rQueue.add(new Book("孟子", "1@qq.com"));
        rQueue.add(new Book("荀子", "1@qq.com"));
        rQueue.add(new Book("韩非", "1@qq.com"));
        rQueue.add(new Book("秦始皇宰相李斯", "1@qq.com"));


//        final Book obj = rQueue.peek();
//        final Book someObj = rQueue.poll();
//
//        System.out.println(obj);
//        System.out.println(someObj);

        return result;
    }

    /**
     * 分布式主题--发布/订阅
     */
    @GetMapping("/topic/publish")
    public long topicPublish() {
        /**
         * 分布式主题
         */
        RTopic myTopic = redissonClient.getTopic("myTopic");
//        RShardedTopic myTopic = redissonClient.getShardedTopic("myTopic");

        int i = new Random().nextInt(1000);
        final Book book = new Book("孔子" + i, "kongzi@qq.com");
        book.setId(i);
        final long receivedClient = myTopic.publish(book);
        System.out.println("收到消息的客户端数量" + receivedClient);
        return receivedClient;

    }

    /**
     * 分布式锁
     */
    @GetMapping("/lock")
    public void lock() {

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String lockKey = "redisson.lock";
                    RLock lock = redissonClient.getLock(lockKey);
                    boolean hasLocked = lock.tryLock();
                    System.out.println(Thread.currentThread().getName() + ":" + hasLocked);

//
                }
            }).start();
        }

    }

    /**
     * 限流器
     */
    @GetMapping("/rateLimiter")
    public void rateLimiter() {

        RRateLimiter rateLimiter = redissonClient.getRateLimiter("rate_limiter");
        /**
         * 1秒之内允许10访问数
         * 注意trySetRate和setRate 区别
         * trySetRate 场所设置如果已存在不修改，返回false，否者为true
         * setRate 更新配置
         */
        rateLimiter.trySetRate(RateType.OVERALL, 4, 1, RateIntervalUnit.SECONDS);
        rateLimiter.setRate(RateType.OVERALL, 4, 1, RateIntervalUnit.SECONDS);


        try {
            System.out.println("config: " + new ObjectMapper().writeValueAsString(rateLimiter.getConfig()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {

            executorService.submit(() -> {
                try {

                    logger.info("线程" + Thread.currentThread().getId() + " availablePermits：" + rateLimiter.availablePermits());
                    rateLimiter.acquire(2);// 每次请求占用2个资源数
                    logger.info("线程" + Thread.currentThread().getId() + "进入数据区：" + System.currentTimeMillis());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    @PostConstruct
    public void setup() {
        rateLimiter = redissonClient.getRateLimiter("rate_limiter");
        rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
//        rateLimiter.setRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
    }

    /**
     * 限流器2
     */
    @GetMapping("/rateLimiter2")
    public boolean rateLimiter2(HttpServletResponse response) {

//        rateLimiter.setRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);

        try {
            System.out.println("config: " + new ObjectMapper().writeValueAsString(rateLimiter.getConfig()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (!rateLimiter.tryAcquire()) {
            logger.info("线程" + Thread.currentThread().getId() + " 接口限流了 ");
            try {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                final ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write("接口限流了".getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;

    }


    @GetMapping("/bloomFilter")
    public boolean contains() {
        final RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("dbKeys", StringCodec.INSTANCE);
        /**
         * 设置初始容量和误报率
         */
        bloomFilter.tryInit(1000000L, 0.02);

        bloomFilter.add("老子");
        bloomFilter.add("孔子");
        bloomFilter.add("孟子");
        bloomFilter.add("荀子");
        bloomFilter.add("庄子");
        bloomFilter.add("韩非");
        bloomFilter.add("李斯");
        bloomFilter.add("连横合纵-战国时期伟大的谋略");
        bloomFilter.add("连横-张仪");
        bloomFilter.add("合纵-苏秦");
        System.out.println(bloomFilter.contains("连横合纵"));
        return bloomFilter.contains("韩非");
    }


}
