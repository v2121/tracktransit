from __future__ import unicode_literals

from django.db import models

# Create your models here.

class Route(models.Model):
    route_number = models.CharField(max_length=10)
    source = models.CharField(max_length=255)
    destination = models.CharField(max_length=255)
    route_polyline = models.TextField(null=True,blank=True)
    description = models.TextField(null=True,blank=True)

    def __unicode__(self):
        return self.route_number

class Vehicle(models.Model):
    registration_number = models.CharField(max_length=20)
    description = models.TextField(null=True,blank=True)

    def __unicode__(self):
        return self.registration_number

class VehicleRoute(models.Model):
    vehicle = models.ForeignKey(Vehicle, on_delete=models.CASCADE)
    route = models.ForeignKey(Route, on_delete=models.CASCADE)
    pin = models.CharField(max_length=10)

    class Meta:
        unique_together = (("vehicle", "route"),)

    def __unicode__(self):
        return "Vehicle: " + self.vehicle.registration_number + " - Route: " + self.route.route_number

class VehicleStatus(models.Model):
    vehicle_id = models.IntegerField(primary_key=True)
    route_number = models.CharField(max_length=10)
    latitude = models.FloatField()
    longitude = models.FloatField()
    updated_at = models.DateTimeField()
    status = models.CharField(max_length=25)

    class Meta:
        verbose_name_plural = "vehicle status"

    def __unicode__(self):
        return self.route_number + "(" + str(self.vehicle_id) + ") - " + self.status
