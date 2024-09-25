package com.liuiie.demo.utils.mapper;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @Author: liuql
 * @Description: 执行批量方法的工具类
 */
@Slf4j
@Data
public class BatchInsertOrUpdateUtil {

    @NonNull
    private SqlSessionFactory sqlSessionFactory;

    @NonNull
    private DataSourceTransactionManager manager;

    public BatchInsertOrUpdateUtil(@NonNull SqlSessionFactory sqlSessionFactory, @NonNull DataSourceTransactionManager manager) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.manager = manager;
    }

    /**
     * 事务声明
     */
    private DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    {
        // 非只读模式
        def.setReadOnly(false);
        //事务隔离级别 采用数据库默认的
        def.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_DEFAULT);
        //事务传播行为 - required  支持当前事务，如果当前没有事务，就新建一个事务。调用这个方式时，如果外层方法上加了@Transactional注解，事务由外层方法控制。否则
        def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
    }


    /**
     * 一次性插入最大 行数
     */
    private final int BATCH_COUNT = 300;

    /**
     * 批量处理修改或者插入
     *
     * @param data        需要被处理的数据
     * @param mapperClass Mybatis的Mapper类
     * @param function    自定义处理逻辑
     * @return int 影响的总行数
     */
    public <T, U, R> int batchUpdateOrInsert(List<T> data, Class<U> mapperClass, BiFunction<List<T>, U, R> function) {
        if (data.size() == 0) {
            log.warn("批插入的数据为空！");
            return 0;
        }
        TransactionStatus status = manager.getTransaction(def);
        int batchCount = BATCH_COUNT;
        SqlSession batchSqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            U mapper = batchSqlSession.getMapper(mapperClass);
            int size = data.size();
            // 每批最后一个的下标
            int batchLastIndex = batchCount;

            for (int index = 0; index < size; ) {

                if (batchLastIndex >= size) {
                    //一波流
                    batchLastIndex = size;
                    //执行批处理
                    function.apply(data.subList(index, batchLastIndex), mapper);
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                    // LogHelper.info(log,"batchInsertWpRealOverdueDetail", "index:" + index + " batchLastIndex:" + batchLastIndex);
                    // 数据插入完毕，退出循环
                    break;
                } else {
                    // 多插几次 BATCH_COUNT
                    function.apply(data.subList(index, batchLastIndex), mapper);
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                    // LogHelper.info(log, "batchInsertWpRealOverdueDetail", "index:" + index + " batchLastIndex:" + batchLastIndex);
                    // 设置下一批下标
                    index = batchLastIndex;
                    batchLastIndex = index + batchCount;
                }
            }
            manager.commit(status);
        } catch (Exception e) {
            log.error("批处理失败！",e);
            manager.rollback(status);
            throw e;
        } finally {
            batchSqlSession.close();

        }
        return data.size();
    }
}
