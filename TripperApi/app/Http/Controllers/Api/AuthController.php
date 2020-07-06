<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\User;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        $validatedData = $request->validate([
            'username' => 'required',
            'email' => 'email|required|unique:users',
            'password' => 'required'
        ]);
        $validatedData['password'] = bcrypt($request->password);
        $user = User::create($validatedData);
        $accessToken = $user->createToken('authToken')->accessToken;
        //return response(['user' => $user, 'access_token' => $accessToken]);
        return response($user);
    }
    public function login(Request $request)
    {
        $loginData = $request->validate([
            'email' => 'email|required',
            'password' => 'required'
        ]);

        if (!auth()->attempt($loginData)) {
            return $this->sendError('Validation Error.', 'Invalid credentials');
        }
        $accessToken = auth()->user()->createToken('authToken')->accessToken;
        //return response(['user' => auth()->user(), 'access_token' => $accessToken]);
        return response(auth()->user());
    }
}
