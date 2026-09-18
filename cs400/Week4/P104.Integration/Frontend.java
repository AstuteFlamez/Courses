import java.util.Scanner;
import java.util.List;
import java.io.IOException;

public class Frontend implements FrontendInterface {

    private Scanner in;
    private BackendInterface backend;
    private Integer currentCollectablesLow = null;
    private Integer currentCollectablesHigh = null;
    private GameRecord.Continent currentFilter = null;

    /**
     * Initializes the frontend with input scanner and backend service.
     * @param in Scanner for user input
     * @param backend Backend service for record management
     */
    public Frontend(Scanner in, BackendInterface backend) {
        this.in = in;
        this.backend = backend;
    }

    /**
     * Displays command instructions and repeatedly processes user commands until quit.
     */
    @Override
    public void runCommandLoop() {
        showCommandInstructions();

        // Main command loop
        boolean quit = false;
        while (!quit) {
            System.out.print("> ");
            String command = in.nextLine().trim();

            if (command.equalsIgnoreCase("quit")) {
                quit = true;
            } else {
                processSingleCommand(command);
            }
        }
    }

    /**
     * Displays all available commands and their syntax.
     */
    @Override
    public void showCommandInstructions() {
        System.out.println("\n=== Command Instructions ===");
        System.out.println("Available commands:");
        System.out.println("  submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME");
        System.out.println("  submit multiple FILEPATH");
        System.out.println("  collectables MAX");
        System.out.println("  collectables MIN to MAX");
        System.out.println("  location CONTINENT");
        System.out.println("  show MAX_COUNT");
        System.out.println("  show fastest times");
        System.out.println("  help");
        System.out.println("  quit");
        System.out.println("=== End Instructions ===\n");
    }

    /**
     * Parses and executes a single user command. Displays output for show/help commands.
     * @param command The user command string
     * @throws IllegalArgumentException for invalid syntax
     * @throws IOException if file read fails
     */
    @Override
    public void processSingleCommand(String command) {
        try {
            if (command.isEmpty()) {
                return;
            }

            String[] parts = command.split("\\s+");
            String commandType = parts[0].toLowerCase();

            switch (commandType) {
                case "submit":
                    handleSubmitCommand(parts);
                    break;
                case "collectables":
                    handleCollectablesCommand(parts);
                    break;
                case "location":
                    handleLocationCommand(parts);
                    break;
                case "show":
                    handleShowCommand(parts);
                    break;
                case "help":
                    showCommandInstructions();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command: " + commandType);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles submit command: either loads records from file or delegates to single record submission.
     * @param parts The command parts
     * @throws IllegalArgumentException for invalid syntax
     * @throws IOException if file read fails
     */
    private void handleSubmitCommand(String[] parts) throws IllegalArgumentException, IOException {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Submit command requires at least one argument");
        }

        if (parts[1].equalsIgnoreCase("multiple")) {
            if (parts.length != 3) {
                throw new IllegalArgumentException("submit multiple requires exactly one filepath argument");
            }
            try {
                backend.readData(parts[2]);
                System.out.println("Successfully loaded records from " + parts[2]);
            } catch (IOException e) {
                throw new IOException("Failed to read file: " + parts[2] + " - " + e.getMessage());
            }
        } else {
            handleSingleRecordSubmit(parts);
        }
    }

    /**
     * Validates and adds a single game record to the backend.
     * @param parts The command parts
     * @throws IllegalArgumentException for invalid syntax
     */
    private void handleSingleRecordSubmit(String[] parts) throws IllegalArgumentException {
        if (parts.length != 7) {
            throw new IllegalArgumentException(
                "Submit command requires: submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME");
        }

        String name = parts[1];
        String continentStr = parts[2].toUpperCase().replace(" ", "_");
        String completionTime = parts[6];
        int score;
        int damageT;
        int collectables;

        // Validate time format early before parsing other values
        if (!isValidTimeFormat(completionTime)) {
            throw new IllegalArgumentException(
                "Completion time must be in format hhh:mm:ss!");
        }

        // Parse score, damage taken, and collectables, throwing exception on invalid integers
        try {
            score = Integer.parseInt(parts[3]);
            damageT = Integer.parseInt(parts[4]);
            collectables = Integer.parseInt(parts[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Score, damage taken, and collectables must be valid integers!");
        }

        GameRecord.Continent continent = parseAndValidateContinent(continentStr, parts[2]);
        GameRecord record = new GameRecord(name, continent, score, damageT, collectables, completionTime);
        backend.addRecord(record);
        System.out.println("Successfully added record for " + name);
    }

    /**
     * Parses and validates an integer input, throwing IllegalArgumentException on failure.
     * @param value The string to parse
     * @param errorMessage The error message to use if parsing fails
     * @throws IllegalArgumentException for invalid syntax
     * @return The parsed integer value
     */
    private int parseIntegerOrThrow(String value, String errorMessage) throws IllegalArgumentException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Sets the collectables range filter for records.
     * @param parts The command parts
     * @throws IllegalArgumentException for invalid syntax
     */
    private void handleCollectablesCommand(String[] parts) throws IllegalArgumentException {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Collectables command requires at least one argument");
        }

        if (parts.length == 2) {
            // Single value: collectables MAX
            currentCollectablesHigh = parseIntegerOrThrow(parts[1], "Collectables value must be a valid integer");
            currentCollectablesLow = null;
            backend.getAndSetRange(currentCollectablesLow, currentCollectablesHigh);
            System.out.println("Collectables filter set to maximum: " + currentCollectablesHigh);
        } else if (parts.length == 4 && parts[2].equalsIgnoreCase("to")) {
            // Range: collectables MIN to MAX
            int min = parseIntegerOrThrow(parts[1], "Collectables values must be valid integers");
            int max = parseIntegerOrThrow(parts[3], "Collectables values must be valid integers");
            if (min > max) {
                throw new IllegalArgumentException("Minimum collectables cannot be greater than maximum");
            }
            currentCollectablesLow = min;
            currentCollectablesHigh = max;
            backend.getAndSetRange(currentCollectablesLow, currentCollectablesHigh);
            System.out.println("Collectables filter set to range: " + min + " to " + max);
        } else {
            throw new IllegalArgumentException(
                "Collectables command format: collectables MAX  OR  collectables MIN to MAX");
        }
    }

    /**
     * Sets the location (continent) filter for records.
     * @param parts The command parts
     * @throws IllegalArgumentException for invalid syntax
     */
    private void handleLocationCommand(String[] parts) throws IllegalArgumentException {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Location command requires at least one continent argument");
        }

        // Join all parts after "location" to handle multi-word continent names like "North America"
        String continentInput = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        String continentStr = continentInput.toUpperCase().replace(" ", "_");
        GameRecord.Continent continent = parseAndValidateContinent(continentStr, continentInput);

        currentFilter = continent;
        backend.applyAndSetFilter(currentFilter);
        System.out.println("Location filter set to: " + continentStr);
    }

    /**
     * Displays records by count or shows the top 10 fastest times.
     * @param parts The command parts
     * @throws IllegalArgumentException for invalid syntax
     */
    private void handleShowCommand(String[] parts) throws IllegalArgumentException {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Show command requires at least one argument");
        }

        // Check if the user wants to show the fastest times
        if (parts[1].equalsIgnoreCase("fastest")) {
            if (parts.length != 3 || !parts[2].equalsIgnoreCase("times")) {
                throw new IllegalArgumentException("Invalid show command. Use 'show fastest times'");
            }
            displayTopTenRecords();
        } else {
            // Show records with a maximum count limit
            int maxCount = parseIntegerOrThrow(parts[1], "Max count must be a valid integer");
            if (maxCount < 0) {
                throw new IllegalArgumentException("Max count cannot be negative");
            }
            displayRecordsWithLimit(maxCount);
        }
    }

    /**
     * Displays filtered records up to the specified count limit.
     * @param maxCount The maximum number of records to display (0 for no limit)
     */
    private void displayRecordsWithLimit(int maxCount) {
        // Retrieve filtered records within the specified range
        List<String> records = backend.getAndSetRange(currentCollectablesLow, currentCollectablesHigh);
        if (records.isEmpty()) {
            System.out.println("No records found matching the current filters.");
            return;
        }

        System.out.println("Records (showing up to " + maxCount + "):");
        for (int count = 0; count < records.size() && (maxCount == 0 || count < maxCount); count++) {
            System.out.println("  " + records.get(count));
        }

        int displayed = (maxCount == 0) ? records.size() : Math.min(records.size(), maxCount);
        System.out.println("Total records displayed: " + displayed);
    }

    /**
     * Displays the top 10 fastest records.
     */
    private void displayTopTenRecords() {
        List<String> topTen = backend.getTopTen();
        if (topTen.isEmpty()) {
            System.out.println("No records found matching the current filters.");
            return;
        }

        // Display the top 10 fastest records
        System.out.println("Top " + topTen.size() + " fastest records:");
        for (int i = 0; i < topTen.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + topTen.get(i));
        }
    }

    /**
     * Validates continent string against enum (spaces converted to underscores).
     * @param continentStr The continent string to validate
     * @param originalInput The original user input for error messaging
     * @throws IllegalArgumentException for invalid syntax
     * @return The corresponding GameRecord.Continent enum value
     */
    private GameRecord.Continent parseAndValidateContinent(String continentStr, String originalInput) throws IllegalArgumentException {
        try {
            return GameRecord.Continent.valueOf(continentStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid continent: " + originalInput +
                ". Valid options: AFRICA, ASIA, ANTARCTICA, AUSTRALIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA");
        }
    }

    /**
     * Validates time format: hhh:mm:ss with valid hour, minute, and second ranges.
     * @param time The time string to validate
     * @return true if valid, false otherwise   
     */
    private boolean isValidTimeFormat(String time) {
        // Split the time string into parts and validate each component
        String[] parts = time.split(":");
        if (parts.length != 3) {
            return false;
        }
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            return hours >= 0 && minutes >= 0 && minutes < 60 && seconds >= 0 && seconds < 60;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
