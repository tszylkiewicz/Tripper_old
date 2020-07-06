package com.example.tripper.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.tripper.model.User;
import com.example.tripper.repository.UserRepository;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class UserViewModel extends ViewModel {

    private UserRepository userRepository = new UserRepository();
    private User currentUser;

    public Single<User> signUp(String email, String username, String password) {
        return userRepository.signUp(email, username, password).observeOn(mainThread()).subscribeOn(Schedulers.io());
    }

    public Single<User> signIn(String email, String password) {
        return userRepository.signIn(email, password).observeOn(mainThread()).subscribeOn(Schedulers.io());
    }

    public Single<User> update(User user) {
        return userRepository.updateUser(user).observeOn(mainThread()).subscribeOn(Schedulers.io());
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
