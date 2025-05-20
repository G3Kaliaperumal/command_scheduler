## CommandScheduler
CommandScheduler is a Java utility for scheduling and executing shell commands based on a simple text file configuration. It supports two types of scheduling: one-time scheduled commands and recurring commands.

### Features
- **One-time scheduled commands:** Execute a command at a specific date and time.
- **Recurring scheduled commands:** Execute a command at a fixed interval (in minutes).
- **Skips one-time commands if the scheduled time is in the past.**
- **Loads commands from a plain text file.**
- **Logs execution and scheduling details.**

### Command File Format
Commands are read from a file (default: `/tmp/commands.txt`). Each line represents a command to be scheduled. Lines starting with `#` are treated as comments and ignored.

#### 1. One-time Scheduled Commands
**Format:**
```
Minute Hour Day Month Year <command>

- `Minute`: 0-59
- `Hour`: 0-23
- `Day`: 1-31
- `Month`: 1-12
- `Year`: >=1970
- `<command>`: The shell command to execute
```
**Examples:**
```
30 17 30 4 2025 date && echo "At Amex, We Do What's Right."
30 18 1 5 2025 date && echo "At Amex, We Do What's Right."
```
> **Note:** If the scheduled date/time is in the past, the command will be skipped.

#### 2. Recurring Scheduled Commands

**Format:**
```
*/n <command>

- `n`: Frequency in minutes (positive integer, e.g., 1, 2, 5, 10, 15, 30, 60)
- `<command>`: The shell command to execute
```
**Examples:**
```
*/1 date && echo "Amex' motto is 'Don't live life without it!'"
*/2 date && echo "Amex was founded in 1850."
*/5 date && echo "Amex is headquartered in New York"
```

#### Example `commands.txt`
```
# One-time scheduled commands
48 2 21 5 2025 date && echo "May 21, 2025 at 2:45 AM"
55 2 21 5 2025 date && echo "May 21, 2025 at 2:46 AM"

# Recurring commands
*/1 date && echo "Every 1 minute"
*/15 date && echo "Every 15 minutes"
```

### Usage
1. **Import project:**  
   Open the project folder in IntelliJ IDEA or any IDE of your preference.
2. **Edit your commands file:**  
   Modify scheduled and recurring commands in `/tmp/commands.txt` located inside the project (or another file).
3. **Run the scheduler:**  
   Locate `CommandScheduler.java` and run its `main` method directly from the IDE.
   By default, it reads from `tmp/commands.txt`. To use a different file, modify the path in `CommandScheduler.java`.
4. **Output:**  
   Command outputs are written to `<home-directory>/tmp/sample-output.txt`.
5. **To open the sample-output.txt file:**

### Assumptions
- One-time commands with a scheduled time in the past are skipped.
- Only valid commands (as per the formats above) are scheduled.
- Invalid lines or commands are logged and ignored.

### Logging
- The application logs scheduling, execution, and errors using Java's built-in logging.



