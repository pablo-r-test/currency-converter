package com.zooplus.currency_converter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zooplus.currency_converter.model.domain.User;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

    User findByUsername(String username);

}
