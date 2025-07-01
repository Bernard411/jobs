from django.urls import path
from . import views

urlpatterns = [
    path('register/', views.register, name='register'),
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout_view, name='logout'),
    path('', views.job_list, name='job_list'),
    path('job/<int:job_id>/', views.job_detail, name='job_detail'),
    path('post-job/', views.post_job, name='post_job'),
    path('profile/', views.profile, name='profile'),
    path('job/<int:job_id>/apply/', views.apply_job, name='apply_job'),
    path('job/<int:job_id>/applicants/', views.view_applicants, name='view_applicants'),
    path('application/<int:application_id>/shortlist/', views.shortlist_applicant, name='shortlist_applicant'),
    path('message/<int:recipient_id>/', views.send_message, name='send_message'),
]
