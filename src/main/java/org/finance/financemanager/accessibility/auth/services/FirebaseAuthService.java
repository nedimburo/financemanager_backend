package org.finance.financemanager.accessibility.auth.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;

    public UserRecord getUserByEmail(String email) throws FirebaseAuthException {
        return firebaseAuth.getUserByEmail(email);
    }

    public UserRecord updateUser(UserRecord.UpdateRequest request) throws FirebaseAuthException {
        return firebaseAuth.updateUser(request);
    }

    public void deleteUser(String id) throws FirebaseAuthException {
        firebaseAuth.deleteUser(id);
    }
}
