package com.lyentech.lib.global.workflow;

public interface Worker {


    /**执行任务
     * @param curNote   当前节点
     */
    void doWork(Node curNote);
}
