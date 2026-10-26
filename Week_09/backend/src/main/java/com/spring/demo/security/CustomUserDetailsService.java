package com.spring.demo.security;



// JUST KEEPING THIS HERE FOR CONTEXT
// WE *COULD* HAVE A SEPARATE UserDetailsService that implements 
// the interface of the same name
// I just choose to put everything in an implementation of UserDetailsManager
// instead, since it also extends this interface 




// package com.spring.demo.service;

// import java.util.Collection;
// import java.util.stream.Collectors;

// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.spring.demo.domain.User;
// import com.spring.demo.domain.Role;
// import com.spring.demo.persistence.UserRepository;

// @Service 
// public class CustomUserDetailsService implements UserDetailsService {

//     private UserRepository userRepo;


// 	public CustomUserDetailsService(UserRepository userRepo) {
// 		this.userRepo = userRepo;
// 	}

//  userExists - on UserDetailsManager impl, not typically on UserDetailsService impl
//  public boolean existsByUserName(String userName) {
// 		// check the database if the user already exists
// 		return userRepo.existsByUserName(userName);
//  }

// 	@Override
// 	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
// 		User user = userRepo.findByUserNameAndEnabledTrue(userName);
// 		if (user == null) {
// 			throw new UsernameNotFoundException("Invalid username or password.");
// 		}
// 		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
// 				mapRolesToAuthorities(user.getRoles()));
// 	}

// 	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
// 		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
// 	}
// }
