# HireME Internship Reminder

A Java AWT application that helps users manage and share internship opportunities. HireME features multi-user functionality, group sharing, and persistent storage using PostgreSQL.

## Features

- **User Authentication**: Login and registration system for multiple users
- **Internship Management**: Add, view, and remove internship opportunities
- **Groups**: Create and join groups with other users
- **Sharing Functionality**: Share internships with group members
- **Modern UI**: Clean interface with intuitive navigation
- **PostgreSQL Integration**: Persistent data storage using Docker

## Technical Architecture

- **Frontend**: Java AWT for UI components
- **Backend**: Java application logic with service layer
- **Database**: PostgreSQL (Docker container)
- **Data Models**: User, Group, Internship

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven for dependency management
- Docker for PostgreSQL database

### Database Setup

1. Start the PostgreSQL container:

   ```
   docker run -d --name hireme_postgres \
     -e POSTGRES_DB=hireme_db \
     -e POSTGRES_USER=hireme_user \
     -e POSTGRES_PASSWORD=hireme_password \
     -p 5432:5432 postgres:14
   ```

2. Database connection details:
   - **Host**: localhost
   - **Port**: 5432
   - **Database**: hireme_db
   - **Username**: hireme_user
   - **Password**: hireme_password

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the project with Maven:
   ```
   mvn clean package
   ```
4. Run the application:
   ```
   mvn exec:java -Dexec.mainClass="com.hireme.internship.InternshipReminderApp"
   ```

## Multi-User Functionality

HireME now supports multiple users with shared content:

- **Login/Register**: Users can create accounts or log in to existing ones
- **Group Management**: Create groups and invite other users
- **Shared Internships**: View internships shared by other users in your groups
- **Save Shared Content**: Save shared internships to your personal list

## Shell Script Utilities

Add these shell functions to your `.bashrc` for easy application management:

```bash
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
```

## Usage Examples

After adding the functions to your `.bashrc`:

1. **Run a single instance**:

   ```
   hireme
   ```

2. **Run multiple instances**:

   ```
   hireme_multi 3
   ```

3. **Run the demo with 3 instances and login info**:

   ```
   hireme_demo
   ```

4. **Connect to the database**:
   ```
   hireme_db
   ```

## Demo Accounts

The application comes with three demo accounts:

- john@example.com / password1
- jane@example.com / password2
- bob@example.com / password3

## Database Schema

- **users**: User accounts and authentication
- **shared_internships**: Internships shared within groups
- **groups**: Group management and membership

## Future Enhancements

- Email notifications for application deadlines
- Advanced search and filtering
- Application status tracking
- Mobile application version
