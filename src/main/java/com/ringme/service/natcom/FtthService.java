package com.ringme.service.natcom;

import com.ringme.dto.natcom.ftth.FtthResponse;
import com.ringme.dto.natcom.selfcare.response.SelfcareResponse;
import com.ringme.dto.record.Response;
import com.ringme.model.selfcare.FtthPackage;

import java.util.List;

public interface FtthService {
    Response findAccountInfo(String ftthAccount);

    FtthResponse wsPayment(String ftthAccount, double money);

    double getExchangeRateHandle();

    SelfcareResponse<FtthPackage> getFtthPackageById(String language, long id);

    SelfcareResponse<List<FtthPackage>> getFtthPackages(String language);
}
