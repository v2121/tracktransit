from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^vehicle/login', views.vehicle_login, name='vehicle_login'),
    url(r'^vehicle/location', views.vehicle_location, name='vehicle_location'),
    url(r'^vehicle/logout', views.vehicle_logout, name='vehicle_logout'),
	url(r'^user/login', views.user_login, name='user_login'),
	url(r'^user/validate_token', views.user_validate_token, name='user_validate_token'),
	url(r'^vehicles', views.vehicles, name='vehicles'),
	url(r'^routes', views.routes, name='routes'),
]
