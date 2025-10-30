package com.ringme.dto.natcom.selfcare.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelfcareResponse<MinHieu> {
    private String errorCode; // 0: Thành công, 1 : Lỗi cùng thống báo tương ứng
    private String message;
    private Object object;
    private String userMsg;
    private MinHieu wsResponse;

    public SelfcareResponse(String userMsg) {
        this.userMsg = userMsg;
    }
}
