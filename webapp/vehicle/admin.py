from django.contrib import admin
from vehicle.models import Route, Vehicle, VehicleRoute

# Register your models here.
admin.site.register(Route)
admin.site.register(Vehicle)
admin.site.register(VehicleRoute)
