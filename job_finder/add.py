import os
import django
import random
from django.utils import timezone

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'job_finder.settings')
django.setup()

from core.models import Job, Company, Category, User

# Dummy data
titles = [
    "Software Developer", "Marketing Specialist", "Data Analyst", "Sales Executive",
    "Frontend Engineer", "Backend Developer", "System Administrator", "DevOps Engineer",
    "Content Writer", "Graphic Designer", "Project Manager", "UI/UX Designer",
    "Business Analyst", "QA Engineer", "SEO Expert", "HR Manager",
    "Mobile App Developer", "Network Engineer", "Technical Support", "Database Administrator"
]

descriptions = [
    "Great opportunity for career growth.",
    "Join a fast-growing startup.",
    "Work with a talented and supportive team.",
    "Looking for passionate professionals.",
    "Remote work available.",
    "Flexible working hours and great benefits.",
    "Be part of a mission-driven company.",
    "Opportunity to work on global projects."
]

locations = [
    "Lilongwe", "Blantyre", "Mzuzu", "Zomba", "Mangochi", "Salima", "Kasungu", "Karonga"
]

def create_dummy_companies():
    company_names = ["TechCorp", "AgroSolutions", "HealthFirst", "EduNation", "BuildMasters"]
    for name in company_names:
        Company.objects.get_or_create(
            name=name,
            defaults={
                "description": f"{name} is a leading company in its sector.",
                "location": random.choice(locations),
            }
        )

def create_dummy_categories():
    category_names = ["IT", "Marketing", "Sales", "Engineering", "Healthcare", "Education"]
    for name in category_names:
        Category.objects.get_or_create(name=name)

def create_dummy_employers():
    for i in range(5):
        username = f"employer{i}"
        if not User.objects.filter(username=username).exists():
            User.objects.create_user(
                username=username,
                email=f"{username}@example.com",
                password="password123",
                role="employer"
            )

def create_jobs():
    companies = list(Company.objects.all())
    categories = list(Category.objects.all())
    users = list(User.objects.filter(role='employer'))

    if not companies or not categories or not users:
        print("Error: Failed to create necessary data.")
        return

    for i in range(20):
        job = Job.objects.create(
            title=random.choice(titles),
            description=random.choice(descriptions),
            category=random.choice(categories),
            company=random.choice(companies),
            location=random.choice(locations),
            posted_by=random.choice(users),
            posted_at=timezone.now()
        )
        print(f"✅ Added Job: {job.title}")

if __name__ == '__main__':
    print("📦 Creating dummy data...")
    create_dummy_companies()
    create_dummy_categories()
    create_dummy_employers()
    print("🚀 Adding jobs...")
    create_jobs()
