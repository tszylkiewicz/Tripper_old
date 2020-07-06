<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Validator;
use Illuminate\Http\Request;
use App\Point;

class PointController extends Controller
{
    public function index()
    {
        return Point::all();
    }

    public function show(Point $point)
    {
        return $point;
    }

    public function store(Request $request)
    {
        $point = Point::create($request->all());

        return response()->json($point, 201);
    }

    public function update(Request $request, Point $point)
    {
        $point->update($request->all());

        return response()->json($point, 200);
    }

    public function delete(Point $point)
    {
        $point->delete();

        return response()->json(null, 204);
    }


    public function multipleStore(Request $request)
    {
        $points = $request->all();
        $result = array();
        foreach ($points as $point) {          
            $pointResult = Point::create($point);
            $result[] = $pointResult;
        }

        return response($result);
    }

    public function getTripPoints($id)
    {
        $points = Point::where('trip_id', $id)->get();

        if (is_null($points)) {
            return $this->sendError('Public trips not found.');
        }

        return response($points->toArray());
    }
}
