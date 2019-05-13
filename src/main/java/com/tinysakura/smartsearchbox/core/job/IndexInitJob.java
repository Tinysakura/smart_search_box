package com.tinysakura.smartsearchbox.core.job;

import com.tinysakura.smartsearchbox.common.command.IndexCreateCommand;
import com.tinysakura.smartsearchbox.service.ElkClientService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 负责初始化索引的job
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */

public class IndexInitJob implements Runnable {

    private ElkClientService elkClientService;

    private LinkedBlockingQueue<IndexCreateCommand> indexCreateBlockingQueue;

    private Long waitTime;

    /**
     *
     * @param elkClientService elk客户端
     * @param indexCreateBlockingQueue 存放索引初始化命令的阻塞队列
     * @param waitTime 从阻塞队列中取命令允许阻塞的最大时间
     */
    public IndexInitJob(ElkClientService elkClientService, LinkedBlockingQueue<IndexCreateCommand> indexCreateBlockingQueue, Long waitTime) {
        this.elkClientService = elkClientService;
        this.indexCreateBlockingQueue = indexCreateBlockingQueue;
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        try {
            IndexCreateCommand command = indexCreateBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS);

            if (command.getIndex() == null) {
                elkClientService.createIndex(command.getIndexName());
            } else {
                elkClientService.createIndex(command.getIndexName(), command.getIndex());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}