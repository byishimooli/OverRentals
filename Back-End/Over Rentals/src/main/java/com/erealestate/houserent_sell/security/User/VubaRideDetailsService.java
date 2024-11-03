package com.erealestate.houserent_sell.security.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.erealestate.houserent_sell.model.User;
import com.erealestate.houserent_sell.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class VubaRideDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return VubaDetails.buildUserDetails(user);
    }

}
