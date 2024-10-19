package com.website.Backend.service;

import com.website.Backend.dto.RequestResponse;
import com.website.Backend.entity.OurUsers;
import com.website.Backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public RequestResponse register(RequestResponse registrationRequest)
    {
        RequestResponse resp = new RequestResponse();

        try{
            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(registrationRequest.getEmail());
            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setCity(registrationRequest.getCity());
            ourUsers.setRole(registrationRequest.getRole());
            ourUsers.setName(registrationRequest.getName());
            OurUsers ourUsersResult = usersRepository.save(ourUsers);
            if(ourUsersResult.getId()>0)
            {
                resp.setOurUsers(ourUsersResult);
                resp.setMessage("Registered Successfully");
                resp.setStatusCode(200);
            }
        }catch (Exception e)
        {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return  resp;
    }

    public RequestResponse login(RequestResponse loginRequest)
    {
        RequestResponse respone = new RequestResponse();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            var user = usersRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken =jwtUtils.generateRefreshToken(new HashMap<>(),user);
            respone.setStatusCode(200);
            respone.setToken(jwt);
            respone.setRole(user.getRole());
            respone.setRefreshToken(refreshToken);
            respone.setExpirationTime("24hrs");
            respone.setMessage("Successfully Logged In");
        }catch (Exception e)
        {
            respone.setStatusCode(500);
            respone.setMessage(e.getMessage());
        }

        return  respone;
    }

    public RequestResponse refreshToken(RequestResponse refreshTokenRequest)
    {
        RequestResponse respone = new RequestResponse();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            OurUsers users = usersRepository.findByEmail(ourEmail).orElseThrow();

            if(jwtUtils.isTokenValid(refreshTokenRequest.getToken(),users))
            {
                var jwt = jwtUtils.generateToken(users);
                respone.setStatusCode(200);
                respone.setToken(jwt);
                respone.setRefreshToken(refreshTokenRequest.getRefreshToken());
                respone.setExpirationTime("24hrs");
                respone.setMessage("Successfully Refreshed Token");
            }
            return respone;
        }catch (Exception e)
        {
            respone.setStatusCode(500);
            respone.setMessage(e.getMessage());
            return  respone;
        }


    }

    public RequestResponse getAllUsers()
    {
        RequestResponse response = new RequestResponse();
        try{
            List<OurUsers> usersList = usersRepository.findAll();
            if(!usersList.isEmpty())
            {
                response.setOurUsersList(usersList);
                response.setStatusCode(200);
                response.setMessage("Successful");
            }
            else {
                response.setStatusCode(404);
                response.setMessage("No User Found");
            }
            return response;

        }catch (Exception e)
        {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return response;
        }
    }

    public RequestResponse getUserById(Integer id)
    {
        RequestResponse response = new RequestResponse();

        try{
            OurUsers ourUsers = usersRepository.findById(id).orElseThrow(()->new RuntimeException("User not Found"));
            response.setOurUsers(ourUsers);
            response.setStatusCode(200);
            response.setMessage("User id with: "+id+"found");
        }
        catch (Exception e)
        {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());

        }

        return response;
    }

    public RequestResponse deleteUser(Integer id)
    {
        RequestResponse response = new RequestResponse();

        try{
            Optional<OurUsers> result = usersRepository.findById(id);
            if(result.isPresent())
            {
                usersRepository.deleteById(id);
                response.setStatusCode(200);
                response.setMessage("Successful");
            }

        }
        catch (Exception e)
        {
            response.setStatusCode(500);
            response.setMessage("Error Occured :"+e.getMessage());
        }
        return response;
    }


    public  RequestResponse updateUser(Integer id, OurUsers newUsers)
    {
        RequestResponse response = new RequestResponse();

        try{
            Optional<OurUsers> updateUser = usersRepository.findById(id);
            if(updateUser.isPresent())
            {
                OurUsers result = updateUser.get();
                result.setCity(newUsers.getCity());
                result.setEmail(newUsers.getEmail());
                result.setName(newUsers.getName());
                response.setRole(newUsers.getRole());

                if(newUsers.getPassword() != null && !newUsers.getPassword().isEmpty())
                {
                    result.setPassword(passwordEncoder.encode(newUsers.getPassword()));
                }

                OurUsers updatedUser = usersRepository.save(result);
                response.setOurUsers(updatedUser);

                response.setStatusCode(200);
                response.setMessage("Successful");
            }
            else{
                response.setStatusCode(404);
                response.setMessage("User Not Found");
            }

        }catch(Exception e)
        {
            response.setStatusCode(500);
            response.setMessage("Error occured: "+e.getMessage());
        }
        return response;
    }

    public RequestResponse getMyInfo(String email)
    {
        RequestResponse response = new RequestResponse();

        try{
            Optional<OurUsers> users = usersRepository.findByEmail(email);
            if(users.isPresent()){
                response.setOurUsers(users.get());
                response.setStatusCode(200);
                response.setMessage("Successful");
            }
            else {
                response.setStatusCode(404);
                response.setMessage("User Not found for update");
            }
        }catch(Exception e)
        {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }

        return response;
    }

}
