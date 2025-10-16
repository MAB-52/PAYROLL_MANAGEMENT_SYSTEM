package com.project.service;

import java.util.List;

import com.project.entity.ClientVendor;

public interface ClientVendorService {
    ClientVendor createVendor(Long orgId, ClientVendor vendor);
    List<ClientVendor> getVendorsByOrganization(Long orgId);
}
