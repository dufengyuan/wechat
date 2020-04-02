package com.chat.utils;

public enum  SearchFriendStatus {
    SUCCESS(0,"查询成功"),
    NOTEXIST(1,"该用户不存在"),
    EXIST(2,"该用户已经是你的好友"),
    YOURSELF(3,"不能添加自己");

    private final int status;
    private final String msg;

    SearchFriendStatus(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
    public static String getMsg(Integer status) {
        for(SearchFriendStatus item: SearchFriendStatus.values()){
            if (item.status == status) {
                return item.msg;
            }

        }
        return null;
    }
    public Integer getStatus() {
        return status;
    }
}
