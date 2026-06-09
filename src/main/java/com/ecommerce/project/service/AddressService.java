package com.ecommerce.project.service;

import com.ecommerce.project.entity.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);
    List<AddressDTO> getAddresses();
    AddressDTO getAddress(Long addressId);
    List<AddressDTO> getAddressesByUser(Long userId);
    AddressDTO updateAddress(AddressDTO addressDTO, Long addressId);
    void  deleteAddress(Long addressId);
}
