from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render
from django.http import HttpResponse, HttpResponseBadRequest
from django.contrib.auth import authenticate
from django.db import connection
from datetime import datetime
from models import Vehicle, Route, VehicleRoute, VehicleStatus
from main import encryption_utils as crypt
import json

STATUS_LOGGED_IN = 'logged_in'
STATUS_LOGGED_OUT = 'logged_out'
TOKEN_KEY = '66e67b8d-445f-43d2-8bb2-38981629ecf4'
TOKEN_VALID_TIMEOUT = 3600 * 24

# Create your views here.

def index(request):
    return HttpResponse("{}", content_type='application/json')

@csrf_exempt
def vehicle_login(request):
    try:
        login_request = json.loads(request.body)
        r = VehicleRoute.objects.get(vehicle__registration_number=login_request['vehicle_number'],pin=login_request['pin'])

        vehicle_id = r.vehicle.id
        vss = VehicleStatus.objects.filter(vehicle_id=vehicle_id)
        if vss.count() == 0:
            vs = VehicleStatus()
            vs.vehicle_id = vehicle_id
            vs.route_number = r.route.route_number
            vs.latitude = 0
            vs.longitude = 0
            vs.updated_at = datetime.now()
            vs.status = STATUS_LOGGED_IN
            vs.save()
        else:
            vs = vss[0]
            vs.route_number = r.route.route_number
            vs.updated_at = datetime.now()
            vs.status = STATUS_LOGGED_IN
            vs.save()

        return HttpResponse(json.dumps({'vehicle_id': vehicle_id, 'route_number': r.route.route_number}), content_type='application/json')
    except:
        pass
    return HttpResponseBadRequest()

@csrf_exempt
def vehicle_location(request):
    try:
        location_request = json.loads(request.body)
        vehicle_id = location_request['vehicle_id']
        lat = location_request['latitude']
        lng = location_request['longitude']

        vs = VehicleStatus.objects.get(vehicle_id=vehicle_id)
        vs.latitude = lat
        vs.longitude = lng
        vs.updated_at = datetime.now()
        vs.status = STATUS_LOGGED_IN
        vs.save(update_fields=['latitude', 'longitude', 'updated_at', 'status'])

        return HttpResponse(json.dumps({'success': True}), content_type='application/json')
    except:
        pass
    return HttpResponseBadRequest()

@csrf_exempt
def vehicle_logout(request):
    try:
        logout_request = json.loads(request.body)
        vehicle_number = logout_request['vehicle_number']
        vehicle_id = Vehicle.objects.get(registration_number=vehicle_number).id

        vs = VehicleStatus.objects.get(vehicle_id=vehicle_id)
        vs.status = STATUS_LOGGED_OUT
        vs.save(update_fields=['status'])

        return HttpResponse(json.dumps({'success': True}), content_type='application/json')
    except:
        pass
    return HttpResponseBadRequest()

def routes(request):
    routes = Route.objects.values_list('route_number', flat=True)
    routes_list = []
    for r in routes:
        routes_list.append(str(r))
    return HttpResponse(json.dumps({'routes': routes_list}), content_type='application/json')

@csrf_exempt
def user_login(request):
    try:
        user_login_request = json.loads(request.body)
        username = user_login_request['username']
        password = user_login_request['password']
        user = authenticate(username=username, password=password)

        user_data = {
            'username': user.username,
            'created_at': datetime.now().strftime('%d/%m/%Y %H:%M:%S')
        }
        token = crypt.encrypt(json.dumps(user_data), TOKEN_KEY)

        if user is not None:
            return HttpResponse(json.dumps({
                'user': {
                    'username': user.username,
                    'first_name': user.first_name,
                    'last_name': user.last_name,
                    'token': token
                }
            }), content_type='application/json')
    except:
        pass
    return HttpResponseBadRequest()

@csrf_exempt
def user_validate_token(request):
    try:
        user_token_request = json.loads(request.body)
        token_data = crypt.decrypt(user_token_request['token'], TOKEN_KEY)
        token = json.loads(token_data)
        token_creation_time = datetime.strptime(token['created_at'], '%d/%m/%Y %H:%M:%S')
        seconds_after_creation = (datetime.now() - token_creation_time).seconds
        valid = (seconds_after_creation < TOKEN_VALID_TIMEOUT)
        return HttpResponse(json.dumps({'valid': valid}), content_type='application/json')
    except:
        pass
    return HttpResponseBadRequest()

@csrf_exempt
def vehicles(request):
    try:
        vehicles_request = json.loads(request.body)

        latitude = vehicles_request['lat']
        longitude = vehicles_request['lng']
        route = vehicles_request.get('route', None)

        query = '''
        select vehicle_id, route_number, latitude, longitude, ST_Distance(ST_Point(%f, %f), ST_Point(longitude, latitude), false) as distance_in_meters
        from vehicle_vehiclestatus where ST_Distance(ST_Point(%f, %f), ST_Point(longitude, latitude), false) < %f
        and ((extract('epoch' from current_timestamp)) - extract('epoch' from updated_at)) < 5*60
        and status = 'logged_in'
        order by distance_in_meters desc
        limit 100
        '''

        q = query % (longitude, latitude, longitude, latitude, 1000)

        if route is not None:
            q = '''select vehicle_id, route_number, latitude, longitude
            from vehicle_vehiclestatus where route_number = '%s'
            and ((extract('epoch' from current_timestamp)) - extract('epoch' from updated_at)) < 5*60
            and status = 'logged_in'
            limit 100
            ''' % route

        cursor = connection.cursor()
        cursor.execute(q)

        vehicles = []

        for row in cursor.fetchall():
            v = {
                'vehicle_id': row[0],
                'latitude': row[2],
                'longitude': row[3],
                'route_number': str(row[1])
            }
            vehicles.append(v)

        return HttpResponse(json.dumps({'vehicles': vehicles}), content_type='application/json')
    except:
        pass
    return HttpResponseBadRequest()
