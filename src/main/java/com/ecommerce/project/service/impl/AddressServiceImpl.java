package com.ecommerce.project.service.impl;

import com.ecommerce.project.entity.Address;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil  authUtil;
    private final UserRepository userRepository;
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        if(addresses.isEmpty()){
            throw new APIException("Address not found");
        }
        return addresses.stream()
                .map(item -> {
                    return modelMapper.map(item, AddressDTO.class);
                }).toList();
    }
    @Override
    public AddressDTO getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByUser(Long userId) {
        userId  = authUtil.loggedInUserId();
        List<Address> addresses = addressRepository.findAddressesByUserId(userId);
        if(addresses.isEmpty()){
            throw new APIException("Address not found");
        }
        return addresses.stream()
                .map(item -> {return modelMapper.map(item, AddressDTO.class);})
                .toList();
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId) {
        Address addressFromDb = getAddressBy(addressId);

        addressFromDb.setCity(addressDTO.getCity());
        addressFromDb.setCountry(addressDTO.getCountry());
        addressFromDb.setPinCode(addressDTO.getPinCode());
        addressFromDb.setStreet(addressDTO.getStreet());
        addressFromDb.setBuildingName(addressDTO.getBuildingName());
        addressFromDb.setPinCode(addressDTO.getPinCode());

        addressFromDb =  addressRepository.save(addressFromDb);
        User user = addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(addressFromDb);
        userRepository.save(user);
        return  modelMapper.map(addressFromDb, AddressDTO.class);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address addressFromDb = getAddressBy(addressId);

        User user =  addressFromDb.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.delete(addressFromDb);
    }

    private Address getAddressBy(Long  addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
    }
}
