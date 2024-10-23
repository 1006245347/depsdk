package com.lyentech.lib.global.workflow;

public interface Node {

    //节点id
    int getId();

    //任务完成时触发
    void onCompleted();
}
