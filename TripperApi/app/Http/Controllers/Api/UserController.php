<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use App\User;

class UserController extends Controller
{
    public function index()
    {
        return User::all();
    }

    /*public function show(User $user)
    {
        return $user;
    }*/

    public function store(Request $request)
    {
        $user = User::create($request->all());

        return response()->json($user, 201);
    }

    /*public function update(Request $request, User $user)
    {
        $user->update($request->all());

        return response()->json($user, 200);
    }*/

    public function delete(User $user)
    {
        $user->delete();

        return response()->json(null, 204);
    }

    public function show($id)
    {
        $user = User::find($id);

        if (is_null($user)) {
            return $this->sendError('User not found.');
        }

        return response($user->toArray());
    }

    public function update(Request $request, $id)
    {

        $user = User::find($id);

        $user->username = $request['username'];
        if(empty($request['firstname'])){
            $user->firstname = NULL;
        }else {
            $user->firstname = $request['firstname'];
        }

        if(empty($request['lastname'])){
            $user->lastname = NULL;
        }else {
            $user->lastname = $request['lastname'];
        }
        
        $user->save();
        return response($user->toArray());
    }    
}
