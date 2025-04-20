# HireME Internship App Helper Functions
function hireme() {
  # Change to the app directory
  cd ~/Desktop/HireME_java_app

  # Make sure the PostgreSQL container is running
  if ! docker ps | grep -q hireme_postgres; then
    echo "Starting PostgreSQL container..."
    if docker ps -a | grep -q hireme_postgres; then
      docker start hireme_postgres
    else
      docker run -d --name hireme_postgres \
        -e POSTGRES_DB=hireme_db \
        -e POSTGRES_USER=hireme_user \
        -e POSTGRES_PASSWORD=hireme_password \
        -p 5432:5432 postgres:14
    fi
    # Wait for PostgreSQL to start
    sleep 3
  fi

  # Run the application
  mvn exec:java -Dexec.mainClass="com.hireme.internship.InternshipReminderApp"
}

function hireme_multi() {
  local instances=${1:-2}

  # Start PostgreSQL if not running
  if ! docker ps | grep -q hireme_postgres; then
    echo "Starting PostgreSQL container..."
    if docker ps -a | grep -q hireme_postgres; then
      docker start hireme_postgres
    else
      docker run -d --name hireme_postgres \
        -e POSTGRES_DB=hireme_db \
        -e POSTGRES_USER=hireme_user \
        -e POSTGRES_PASSWORD=hireme_password \
        -p 5432:5432 postgres:14
    fi
    # Wait for PostgreSQL to start
    sleep 3
  fi

  # Change to app directory
  cd ~/Desktop/HireME_java_app

  # Compile once
  mvn clean compile

  # Launch multiple instances
  for ((i=1; i<=$instances; i++)); do
    echo "Starting HireME instance $i..."
    mvn exec:java -Dexec.mainClass="com.hireme.internship.InternshipReminderApp" &
    # Small delay to prevent UI overlap
    sleep 2
  done

  echo "$instances instances of HireME started"
}

function hireme_demo() {
  echo "Starting HireME multi-user demo..."
  echo "This will launch 3 instances of the application"
  echo "You can login with demo accounts:"
  echo "  - john@example.com / password1"
  echo "  - jane@example.com / password2"
  echo "  - bob@example.com / password3"
  echo ""
  hireme_multi 3
}

function hireme_db() {
  echo "PostgreSQL Connection Details:"
  echo "  Host: localhost"
  echo "  Port: 5432"
  echo "  Database: hireme_db"
  echo "  Username: hireme_user"
  echo "  Password: hireme_password"

  # Connect to PostgreSQL in container
  docker exec -it hireme_postgres psql -U hireme_user -d hireme_db
}
