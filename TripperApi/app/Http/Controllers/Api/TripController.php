<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Validator;
use Illuminate\Http\Request;
use App\Trip;

class TripController extends Controller
{
    public function index()
    {
        return Trip::all();
    }

    public function show(Trip $trip)
    {
        return $trip;
    }

    /*public function store(Request $request)
    {
        $trip = Point::create($request->all());

        return response()->json($trip, 201);
    }

    public function update(Request $request, Trip $trip)
    {
        $trip->update($request->all());

        return response()->json($trip, 200);
    }*/

    public function delete(Trip $trip)
    {
        $trip->delete();

        return response()->json(null, 204);
    }


    public function store(Request $request)
    {
        $input = $request->all();        

        $trip = Trip::create($input);
        return response($trip->toArray());
    }

    public function showUserTrips($id)
    {
        $trips = Trip::where('user_id', $id)->get();

        if (is_null($trips)) {
            return $this->sendError('User trips not found.');
        }

        return response($trips->toArray());
    }

    
    public function showPublicTrips()
    {
        $trips = Trip::where('shared', 1)->get();

        if (is_null($trips)) {
            return $this->sendError('Public trips not found.');
        }

        return response($trips->toArray());
    }



    public function update(Request $request, $id)
    {

        $trip = Trip::find($id);

        $validatedData = $request->validate([
            'name' => 'required',
            'description' => 'required',
            'shared' => 'required'
        ]);

        $trip->name = $request['name'];
        
        if(empty($request['description'])){
            $trip->description = NULL;
        }else {
            $trip->description = $request['description'];
        }
        $trip->shared = $request['shared'];
        
        $trip->save();
        return response($trip->toArray());
    }
}
