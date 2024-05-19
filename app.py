from flask import Flask,shutil, request, render_template
import subprocess
import os
import requests

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/generate', methods=['POST'])
def generate():
    app_name = request.form['appName']
    package_name = request.form['packageName']
    
    # Customize the project
    template_path = 'path/to/your/template/project'
    output_path = f'output/{package_name}'
    customize_project(template_path, output_path, app_name, package_name)
    
    # Trigger the CI build process
    trigger_ci_build(output_path)
    
    return 'APK generation triggered. You will receive the APK shortly.'

def customize_project(template_path, output_path, app_name, package_name):
    if os.path.exists(output_path):
        shutil.rmtree(output_path)
    shutil.copytree(template_path, output_path)
    
    # Modify the necessary files (AndroidManifest.xml, strings.xml, etc.)
    manifest_path = os.path.join(output_path, 'app/src/main/AndroidManifest.xml')
    with open(manifest_path, 'r') as file:
        manifest_content = file.read()
    manifest_content = manifest_content.replace('com.example.template', package_name)
    with open(manifest_path, 'w') as file:
        file.write(manifest_content)
    
    strings_path = os.path.join(output_path, 'app/src/main/res/values/strings.xml')
    with open(strings_path, 'r') as file:
        strings_content = file.read()
    strings_content = strings_content.replace('Template App', app_name)
    with open(strings_path, 'w') as file:
        file.write(strings_content)
    
    print("Project customized successfully.")

def trigger_ci_build(output_path):
    # This function should trigger your CI build process, e.g., via an API call to GitHub Actions, Jenkins, etc.
    print("Triggering CI build...")
    # Example for GitHub Actions
    response = requests.post(
        'https://api.github.com/repos/Divyatej-2024/App/app.yml/dispatches',
        headers={'Authorization': 'token YOUR_GITHUB_TOKEN'},
        json={'ref': 'main', 'inputs': {'project_path': output_path}}
    )
    if response.status_code == 204:
        print("CI build triggered successfully.")
    else:
        print("Failed to trigger CI build.", response.text)

if __name__ == '__main__':
    app.run(debug=True)
def trigger_ci_build(output_path):
    print("Triggering CI build...")
    response = requests.post(
        'https://api.github.com/repos/yourusername/yourrepo/actions/workflows/android-build.yml/dispatches',
        headers={'Authorization': 'token YOUR_GITHUB_TOKEN'},
        json={'ref': 'main', 'inputs': {'project_path': output_path}}
    )
    if response.status_code == 204:
        print("CI build triggered successfully.")
    else:
        print("Failed to trigger CI build.", response.text)

# Function to handle the webhook event from GitHub Actions
@app.route('/webhook', methods=['POST'])
def webhook():
    data = request.json
    if data['action'] == 'completed' and data['workflow_run']['conclusion'] == 'success':
        artifact_url = get_artifact_url(data['repository']['owner']['login'], data['repository']['name'])
        return f"APK is ready. Download it here: {artifact_url}"
    return 'Build not successful.'

def get_artifact_url(owner, repo):
    response = requests.get(
        f'https://api.github.com/repos/{owner}/{repo}/actions/artifacts',
        headers={'Authorization': 'token YOUR_GITHUB_TOKEN'}
    )
    artifacts = response.json()['artifacts']
    for artifact in artifacts:
        if artifact['name'] == 'app-release.apk':
            return artifact['archive_download_url']
    return None
