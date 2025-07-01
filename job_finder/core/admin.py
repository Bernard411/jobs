from django.contrib import admin
from .models import User, Company, Category, Job, Profile, Application, Message

admin.site.register(User)
admin.site.register(Company)
admin.site.register(Category)
admin.site.register(Job)
admin.site.register(Profile)
admin.site.register(Application)
admin.site.register(Message)
