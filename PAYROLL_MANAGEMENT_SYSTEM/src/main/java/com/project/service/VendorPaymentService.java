package com.project.service;

import com.project.entity.VendorPayment;

public interface VendorPaymentService {

   VendorPayment requestPayment(Long organizationId, VendorPayment request);

}
