package com.landray.asyh.asyhmsg.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class KKSMSSEND {
    private Integer id;
    private Integer smsId;
    private Integer sendTime;
    private Integer sender;
    private String senderName;
    private String externNo;
    private String recivePhone;
    private String receiverName;
    private String content;
    private Integer state;
}
