package com.tinysakura.smartsearchbox.core.job;

import com.tinysakura.smartsearchbox.common.command.DocumentAddCommand;
import com.tinysakura.smartsearchbox.service.ElkClientService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 负责索引文档的job
 * @Author: chenfeihao@corp.netease.com
 * @Date: 2019/5/13
 */
public class DocumentIndexJob implements Runnable {
    private ElkClientService elkClientService;

    private LinkedBlockingQueue<DocumentAddCommand> documentAddBlockingQueue;

    private Long waitTime;

    /**
     *
     * @param elkClientService elk客户端
     * @param documentAddBlockingQueue 存放索引文档命令的阻塞队列
     * @param waitTime 从阻塞队列中取命令允许阻塞的最大时间
     */
    public DocumentIndexJob(ElkClientService elkClientService, LinkedBlockingQueue<DocumentAddCommand> documentAddBlockingQueue, Long waitTime) {
        this.elkClientService = elkClientService;
        this.documentAddBlockingQueue = documentAddBlockingQueue;
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        try {
            DocumentAddCommand command = documentAddBlockingQueue.poll(waitTime, TimeUnit.MILLISECONDS);

            elkClientService.addDocument(command.getIndex(), command.getDocumentType(), command.getDocument());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}