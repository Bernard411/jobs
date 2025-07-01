from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth import login, authenticate
from django.contrib.auth.decorators import login_required
from .forms import UserRegisterForm, JobForm, ProfileForm, ApplicationForm
from .models import Job, Category, Company, Profile, Application, User, Message
from django.contrib import messages
from django.db.models import Q
from django.contrib.auth import authenticate, login as auth_login
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.auth import logout as auth_logout

# User Registration

def register(request):
    if request.method == 'POST':
        form = UserRegisterForm(request.POST)
        if form.is_valid():
            user = form.save()
            login(request, user)
            return redirect('job_list')
    else:
        form = UserRegisterForm()
    return render(request, 'register.html', {'form': form})

# Login

def login_view(request):
    if request.method == 'POST':
        form = AuthenticationForm(request, data=request.POST)
        if form.is_valid():
            user = form.get_user()
            auth_login(request, user)
            return redirect('job_list')
    else:
        form = AuthenticationForm()
    return render(request, 'login.html', {'form': form})

# Logout

def logout_view(request):
    auth_logout(request)
    return redirect('login')

# Job Listing & Filtering

def job_list(request):
    jobs = Job.objects.all()
    category = request.GET.get('category')
    location = request.GET.get('location')
    company = request.GET.get('company')
    search = request.GET.get('search')
    if category:
        jobs = jobs.filter(category__id=category)
    if location:
        jobs = jobs.filter(location__icontains=location)
    if company:
        jobs = jobs.filter(company__id=company)
    if search:
        jobs = jobs.filter(Q(title__icontains=search) | Q(description__icontains=search))
    categories = Category.objects.all()
    companies = Company.objects.all()
    return render(request, 'job_list.html', {'jobs': jobs, 'categories': categories, 'companies': companies})

# Job Detail

def job_detail(request, job_id):
    job = get_object_or_404(Job, id=job_id)
    return render(request, 'job_detail.html', {'job': job})

# Post Job (Employer only)
@login_required
def post_job(request):
    if request.user.role != 'employer':
        return redirect('job_list')
    if request.method == 'POST':
        form = JobForm(request.POST)
        if form.is_valid():
            job = form.save(commit=False)
            job.posted_by = request.user
            job.save()
            return redirect('job_list')
    else:
        form = JobForm()
    return render(request, 'post_job.html', {'form': form})

# Profile Builder & CV Upload
@login_required
def profile(request):
    profile, created = Profile.objects.get_or_create(user=request.user)
    if request.method == 'POST':
        form = ProfileForm(request.POST, request.FILES, instance=profile)
        if form.is_valid():
            form.save()
            messages.success(request, 'Profile updated!')
            return redirect('profile')
    else:
        form = ProfileForm(instance=profile)
    return render(request, 'profile.html', {'form': form})

# Apply for Job (Job Seeker only)
@login_required
def apply_job(request, job_id):
    job = get_object_or_404(Job, id=job_id)
    if request.user.role != 'job_seeker':
        return redirect('job_list')
    if request.method == 'POST':
        form = ApplicationForm(request.POST)
        if form.is_valid():
            application = form.save(commit=False)
            application.job = job
            application.applicant = request.user
            application.save()
            messages.success(request, 'Application submitted!')
            return redirect('job_detail', job_id=job.id)
    else:
        form = ApplicationForm()
    return render(request, 'apply_job.html', {'form': form, 'job': job})

# Employer: View Applicants, Shortlist, Message
@login_required
def view_applicants(request, job_id):
    job = get_object_or_404(Job, id=job_id)
    applications = Application.objects.filter(job=job)
    return render(request, 'view_applicants.html', {'job': job, 'applications': applications})

@login_required
def shortlist_applicant(request, application_id):
    application = get_object_or_404(Application, id=application_id, job__posted_by=request.user)
    application.shortlisted = True
    application.save()
    messages.success(request, 'Applicant shortlisted!')
    return redirect('view_applicants', job_id=application.job.id)

@login_required
def send_message(request, recipient_id):
    recipient = get_object_or_404(User, id=recipient_id)
    if request.method == 'POST':
        content = request.POST.get('content')
        if content:
            Message.objects.create(sender=request.user, recipient=recipient, content=content)
            messages.success(request, 'Message sent!')
            return redirect('profile')
    return render(request, 'send_message.html', {'recipient': recipient})
