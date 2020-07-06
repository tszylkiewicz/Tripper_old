<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

// =============================================================================================
// Auth endpoints
// =============================================================================================
Route::post('/register', 'Api\AuthController@register');
Route::post('/login', 'Api\AuthController@login');

// =============================================================================================
// User endpoints
// =============================================================================================
Route::get('/user/{id}', 'Api\UserController@show');
Route::put('/user/{id}', 'Api\UserController@update');

// =============================================================================================
// Point endpoints
// =============================================================================================
Route::get('/point/{point}', 'Api\PointController@index');
Route::get('/point/{point}', 'Api\PointController@show');
Route::post('/point/{point}', 'Api\PointController@multipleStore');
Route::put('/point/{point}', 'Api\PointController@multipleStore');
Route::delete('/point/{point}', 'Api\PointController@multipleStore');

Route::post('/point', 'Api\PointController@multipleStore');
Route::get('/point/trip/{id}', 'Api\PointController@getTripPoints');

// =============================================================================================
// Trip endpoints
// =============================================================================================
Route::post('/trip', 'Api\TripController@store');
Route::put('/trip/{id}', 'Api\TripController@update');
Route::get('/trip/user/{id}', 'Api\TripController@showUserTrips');
Route::get('/trip/public', 'Api\TripController@showPublicTrips');

