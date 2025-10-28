# 🚀 How to Start Rangira Agro Farming Application

## ⚡ Quick Start (3 Methods)

---

## Method 1: Command Line (RECOMMENDED)

### **Step 1: Open Command Prompt**
- Press `Windows + R`
- Type: `cmd`
- Press Enter

### **Step 2: Navigate to Project**
```bash
cd D:\Rangira-Agro-Farming
```

### **Step 3: Run the Application**
```bash
mvn spring-boot:run
```

### **Step 4: Wait for Success Message**
Look for this in console:
```
Started RangiraAgroFarmingApplication in XX.XXX seconds
```

✅ **App is now running!**

### **Step 5: Test in Browser**
Open: http://localhost:8080/swagger-ui.html

---

## Method 2: Using IntelliJ IDEA

1. Open project in IntelliJ
2. Navigate to: `src/main/java/com/raf/Rangira/Agro/Farming/RangiraAgroFarmingApplication.java`
3. Click the green ▶️ play button next to `public class RangiraAgroFarmingApplication`
4. Or right-click → Run 'RangiraAgroFarmingApplication'
5. Wait for "Started..." in console
6. Open: http://localhost:8080/swagger-ui.html

---

## Method 3: Using VS Code

1. Open project in VS Code
2. Press `F5`
3. Or click Run → Start Debugging
4. Wait for "Started..." in terminal
5. Open: http://localhost:8080/swagger-ui.html

---

## ✅ Verify App is Running

### Test 1: Swagger UI
```
http://localhost:8080/swagger-ui.html
```
Should show interactive API documentation

### Test 2: Get Provinces
```
http://localhost:8080/api/provinces
```
Should return JSON with 5 provinces

### Test 3: Health Check
If you see one of these working, app is running! ✅

---

## 🔧 Troubleshooting

### Problem 1: "mvn command not found"

**Solution:** Use IDE method (IntelliJ/VS Code) instead

---

### Problem 2: "Port 8080 already in use"

**Solution A:** Kill the process using port 8080
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Solution B:** Change port in `application.properties`
```properties
server.port=8081
```
Then use: `http://localhost:8081`

---

### Problem 3: "Cannot connect to database"

**Solution:** Use H2 in-memory database

Edit `src/main/resources/application.properties`:

**Comment out MySQL:**
```properties
#spring.datasource.url=jdbc:mysql://localhost:3306/rangira_agro_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
#spring.datasource.username=root
#spring.datasource.password=root
#spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Uncomment H2:**
```properties
spring.datasource.url=jdbc:h2:mem:rangira_agro_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Change Dialect:**
```properties
#spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

**Restart the app**

---

## 📝 Success Indicators

When app starts successfully, you'll see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.7)

2025-10-23 10:30:00.000  INFO --- [main] c.r.R.A.F.RangiraAgroFarmingApplication  : Starting RangiraAgroFarmingApplication
...
2025-10-23 10:30:15.234  INFO --- [main] c.r.R.A.F.config.DataSeeder              : ===========================================
2025-10-23 10:30:15.235  INFO --- [main] c.r.R.A.F.config.DataSeeder              : Starting Data Seeding for Rangira Agro Farming
2025-10-23 10:30:15.236  INFO --- [main] c.r.R.A.F.config.DataSeeder              : ===========================================
2025-10-23 10:30:15.500  INFO --- [main] c.r.R.A.F.config.DataSeeder              : Seeding Rwandan Location Hierarchy...
2025-10-23 10:30:16.000  INFO --- [main] c.r.R.A.F.config.DataSeeder              : Created Province: Kigali City
2025-10-23 10:30:16.100  INFO --- [main] c.r.R.A.F.config.DataSeeder              : Created Province: Northern Province
...
2025-10-23 10:30:20.000  INFO --- [main] c.r.R.A.F.config.DataSeeder              : ===========================================
2025-10-23 10:30:20.001  INFO --- [main] c.r.R.A.F.config.DataSeeder              : Data Seeding Completed Successfully!
2025-10-23 10:30:20.002  INFO --- [main] c.r.R.A.F.config.DataSeeder              : ===========================================
2025-10-23 10:30:20.500  INFO --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2025-10-23 10:30:20.510  INFO --- [main] c.r.R.A.F.RangiraAgroFarmingApplication  : Started RangiraAgroFarmingApplication in 20.789 seconds
```

Key messages:
✅ "Starting Data Seeding for Rangira Agro Farming"  
✅ "Created Province: Kigali City" (and 4 more)  
✅ "Data Seeding Completed Successfully!"  
✅ "Tomcat started on port(s): 8080"  
✅ **"Started RangiraAgroFarmingApplication in XX.XXX seconds"**  

---

## 🎯 After App Starts

### Immediate Tests:

1. **Open Swagger UI:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

2. **Test in browser:**
   ```
   http://localhost:8080/api/provinces
   ```

3. **Test in Postman:**
   ```
   GET http://localhost:8080/api/users/by-province-code/NOR
   ```

---

## 💡 Tips

- **Keep terminal open** while testing
- **Press Ctrl+C** in terminal to stop app
- **Check logs** in terminal for any errors
- **Restart app** after changing `application.properties`

---

## ✅ Checklist

- [ ] Java 17 installed
- [ ] Project folder: `D:\Rangira-Agro-Farming`
- [ ] Run: `mvn spring-boot:run`
- [ ] See: "Started RangiraAgroFarmingApplication"
- [ ] Test: http://localhost:8080/swagger-ui.html
- [ ] Ready to test APIs! 🚀

