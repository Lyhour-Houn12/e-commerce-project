package com.ecommerce.project.controller;

import com.ecommerce.project.entity.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<?> createAddress(@RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedUser();
        AddressDTO addresses = addressService.createAddress(addressDTO, user);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/addresses")
    public ResponseEntity<?> getAddress() {
        return  ResponseEntity.ok(addressService.getAddresses());
    }
    @GetMapping("/address/{addressId}")
    public ResponseEntity<?> getAddress(@PathVariable Long addressId) {
        return  ResponseEntity.ok(addressService.getAddress(addressId));
    }
    @GetMapping("/addresses/{userId}")
    public ResponseEntity<?> getAddresses(@PathVariable Long userId) {
        return  ResponseEntity.ok(addressService.getAddressesByUser(userId));
    }
    @PutMapping("/address/{addressId}")
    public ResponseEntity<?> updateAddress(@RequestBody AddressDTO addressDTO, @PathVariable Long addressId) {
        AddressDTO addresses = addressService.updateAddress(addressDTO, addressId);
        return ResponseEntity.ok(addresses);
    }
    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        String message = String.format("Delete address with id: %d successfully.", addressId);
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok(message);
    }


}
