package com.docmall.basic.kakaopay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by kakaopay
 */

@ToString
public class ReadyResponse {
    private String tid;
    private String created_at;
    private String next_redirect_pc_url; //결제 진행 qr코드 주소

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNext_redirect_pc_url() {
        return next_redirect_pc_url;
    }

    public void setNext_redirect_pc_url(String next_redirect_pc_url) {
        this.next_redirect_pc_url = next_redirect_pc_url;
    }

}
