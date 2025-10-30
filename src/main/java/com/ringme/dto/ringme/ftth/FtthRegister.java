package com.ringme.dto.ringme.ftth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ringme.enums.selfcare.FtthRegisterStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FtthRegister implements Serializable {
    private Long id;

    private String packageCode;
    private String packageName;
    private String name;
    private String isdner;
    private String isdnee;
    private String emailee;
    private String proviceId;
    private String proviceName;
    private String districtId;
    private String districtName;
    private String commune;
    private String note;
    private String orderCode;
    private String language;

    private FtthRegisterStatus status;
    private Date createdAt;
    private Date updatedAt;

    public String getIsdnee() {
        if(isdnee == null)
            return "";

        if(isdnee.startsWith("509"))
            isdnee = isdnee.replace("509", "");

        if(isdnee.startsWith("+509"))
            isdnee = isdnee.replace("+509", "");

        return isdnee;
    }
}
