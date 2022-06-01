# redisdemo
基于springboot 使用redis

# springboot 2.7.0 集成Redisson实现分布式相关操作

# springboot 使用Redisson

```
   <!--  org.redisson -->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.17.3</version>
        </dependency>
```

# redis五大对象使用

## 字符串和对象

Redisson将Redis中的字符串数据结构封装成了RBucket，通过RedissonClient的getBucket(key)方法获取一个RBucket对象实例，通过这个实例可以设置value或设置value和有效期，例如如下代码。

```
RBucket<String> rbucket =  redissonClient.getBucket("test");

// 只设置value，key不过期

rbucket.set("lihm");

// 设置value和key的有效期

rbucket.set("lihm", 30, TimeUnit.SECONDS);

// 通过key获取value

redissonClient.getBucket("name").get();


        /**
         * 字符串处理
         */
        RBucket<Book> test = redissonClient.getBucket("book");
        final Book value = test.get();
        Book book = new Book("史记", "sunshine1028@foxmail.com");
        test.set(book);
        return value;
 
```

## 列表对象

```
    /**
         * 列表对象
         */
        RList<String> rList = redissonClient.getList("list");
        final List<String> result = rList.readAll();
        rList.add("孔子");
        rList.add("孟子");
        rList.add("荀子");
```

## 哈希对象

```
       /**
         * 哈希对象
         */
        RMap<String, String> map = redissonClient.getMap("hash");
        final RMap<String, String> old = map;

        map.put("name", "lihm");
        map.put("email", "sunshine1028@foxmail.com");
```

## 集合对象

```
   /**
         * 集合对象
         */
        RSet<String> rSet = redissonClient.getSet("set", StringCodec.INSTANCE);
        final Set<String> result = rSet.readAll();

        rSet.add("孔子-性善论");
        rSet.add("孟子-老师（孔子孙子子思）");
        rSet.add("荀子-性恶论");
        rSet.add("韩非");
        rSet.add("秦始皇宰相李斯");
        rSet.add("lihm");
```

## 有序集合对象

```
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


     /**
         * 计分排序集
         */
        RScoredSortedSet<String> rSet = redissonClient.getScoredSortedSet("ScoredSortedSet");
        final Collection<String> result = rSet.readAll();
        rSet.add(0.33, "孔子-性善论");
        rSet.add(0.251, "孟子-老师（孔子孙子子思）");
        rSet.add(0.302, "荀子-性恶论");
        rSet.add(0.5, "韩非");
        rSet.add(1, "秦始皇宰相李斯");
        rSet.add(1.2, "lihm");
        rSet.rank("lihm"); // 获取元素在集合中的位置
        rSet.getScore("lihm"); // 获取元素的评分
```

## 队列

```
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
```

# 锁

```
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

```

RedissonLock 的ttl也不是永久的，默认是30s。

　　在加锁成功后，会注册一个定时任务监听这个锁，每隔10秒就去查看这个锁，如果还持有锁，就对过期时间进行续期。默认过期时间30秒，过10秒检查一次，一旦加锁的业务没有执行完，就会进行一次续期，把锁的过期时间再次重置成30秒。
如果在执行过程中线程死掉，不会续期。会等ttl到期后自动消失。

# 分布式主题--发布/订阅

```
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
```

# 限流器

```
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

```


# springboot 集成另外两个redis驱动 jedis、lettuce


## 使用lettuce 
springboot默认就是lettuce驱动，不需要任何配置，默认依赖spring-boot-starter-data-redis即可。
```
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>         
 </dependency>

```


## 使用jedis
Jedis是Redis的Java实现的客户端，其API提供了比较全面的Redis命令的支持；
Jedis中的方法调用是比较底层的暴露的Redis的API，也即Jedis中的Java方法基本和Redis的API保持着一致，了解Redis的API，也就能熟练的使用Jedis。
```
<!-- 使用jedis 需配置spring.redis.client-type=jedis -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>

除了配置依赖，还需要指定配置spring.redis.client-type=jedis

```





项目地址：https://github.com/lihm1028/redisdemo
