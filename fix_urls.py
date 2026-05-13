import os

directory = r'd:\Meya\Rangira_Agro_Farming\frontend\src'

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content

    # 1. Template literals with /api/files/
    content = content.replace("`http://localhost:8081/api/files/", "`${process.env.REACT_APP_API_URL ?? 'http://localhost:8081/api'}/files/")
    
    # 2. Template literals with variable directly (e.g. `http://localhost:8081${imageUrl}`)
    content = content.replace("`http://localhost:8081${", "`${process.env.REACT_APP_BACKEND_URL ?? 'http://localhost:8081'}${")
    
    # 3. index.js string
    content = content.replace(
        "'Backend connection error. Make sure the backend is running on http://localhost:8081'",
        "`Backend connection error. Make sure the backend is running on ${process.env.REACT_APP_BACKEND_URL ?? 'http://localhost:8081'}`"
    )
    
    # 4. BackendStatus.js JSX
    content = content.replace(
        "Make sure the Spring Boot server is running on http://localhost:8081",
        "Make sure the Spring Boot server is running on {process.env.REACT_APP_BACKEND_URL ?? 'http://localhost:8081'}"
    )

    # 5. api.js line 37
    content = content.replace(
        "'Network error: Backend may not be running. Check http://localhost:8081'",
        "`Network error: Backend may not be running. Check ${process.env.REACT_APP_BACKEND_URL ?? 'http://localhost:8081'}`"
    )

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {filepath}")

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.js'):
            process_file(os.path.join(root, file))
