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
     * Constructor
     * @param in Scanner to read user input
     * @param backend BackendInterface to handle data operations
     */
    public Frontend(Scanner in, BackendInterface backend) {
        this.in = in;
        this.backend = backend;
    }

    /**
     * Displays instructions for the syntax of user commands.  And then 
     * repeatedly gives the user an opportunity to issue new commands until
     * they enter "quit".  Uses the processSingleCommand method below to
     * parse and run each command entered by the user.  If the backend ever
     * throws any exceptions, they should be caught here and reported to the
     * user.  The user should then continue to be able to issue subsequent
     * commands until they enter "quit".  This method must use the scanner
     * passed into the constructor to read commands input by the user.
     */
    @Override
    public void runCommandLoop() {
        showCommandInstructions();

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
     * Displays instructions for the user to understand the syntax of commands
     * that they are able to enter.  This should be displayed once from the
     * command loop, before the first user command is read in, and then later
     * in response to the user entering the command: help.
     * 
     * The lowercase words in the following examples are keywords that the 
     * user must match exactly in their commands, while the upper case words
     * are placeholders for arguments that the user can specify.  The following
     * are examples of valid command syntax that your frontend should be able
     * to handle correctly.
     * 
     * submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME
     * submit multiple FILEPATH
     * collectables MAX
     * collectables MIN to MAX
     * location CONTINENT
     * show MAX_COUNT
     * show fastest times
     * help
     * quit
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
     * This method takes a command entered by the user as input. It parses
     * that command to determine what kind of command it is, and then makes
     * use of the backend (which was passed to the constructor) to update the
     * state of that backend.  When a show or help command is issued, this
     * method prints the appropriate results to standard out.  When a command 
     * does not follow the syntax rules described above, this method should 
     * print out an error message that describes at least one defect in the 
     * syntax of the provided command argument.
     * 
     * Some notes on the expected behavior of the different commands:
     *     submit : results in backend adding a new record with the specific NAME, CONTINENT,
     *          SCORE, DAMAGE_TAKEN, COLLECTABLES, COMPLETION_TIME
     *          COMPLETION_TIME is of the format "hhh:mm:ss"
     *     submit multiple: results in backend loading data from specified path
     *     collectables: updates backend's range of records to return
     *                 should not result in any records being displayed
     *     location: updates backend's filter criteria
     *                   should not result in any records being displayed
     *     show: displays list of records with currently set thresholds and filters
     *           MAX_COUNT: argument limits the number of record names displayed
     *           to the first MAX_COUNT in the list returned from backend
     *           fastest times: argument displays results returned from the
     *           backend's getTopTen method
     *     help: displays command instructions
     *     quit: ends this program (handled by runCommandLoop method above)
     *           (do NOT use System.exit(), as this will interfere with tests)
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
     * Handles the submit command in two forms:
     * 1. submit multiple FILEPATH: loads records from a file
     * 2. submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME: adds a single record
     * 
     * Command parsing (parts array indices):
     *   parts[0] = "submit" (command keyword)
     *   parts[1] = "multiple" OR player name
     *   
     * For "submit multiple" format:
     *   parts[2] = file path
     * 
     * For single record format:
     *   parts[1] = player name
     *   parts[2] = continent (AFRICA, ASIA, ANTARCTICA, AUSTRALIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA)
     *   parts[3] = score (integer)
     *   parts[4] = damage taken (integer)
     *   parts[5] = collectables (integer)
     *   parts[6] = completion time (format: hhh:mm:ss)
     * 
     * Exceptions thrown:
     *   IllegalArgumentException: if parts array length is incorrect, if "multiple" format is missing filepath,
     *                             if single record format has wrong number of arguments, if score/damage/collectables
     *                             are not valid integers, if time format is invalid (hhh:mm:ss), or if continent
     *                             is not a valid enum value
     *   IOException: if file cannot be read for "submit multiple" command
     * 
     * @param parts the parsed command array
     * @throws IllegalArgumentException if command syntax is invalid
     * @throws IOException if file cannot be read for submit multiple command
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
     * Handles submission of a single game record.
     * 
     * Command parsing (parts array indices):
     *   parts[0] = "submit" (command keyword)
     *   parts[1] = player name
     *   parts[2] = continent (AFRICA, ASIA, ANTARCTICA, AUSTRALIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA)
     *   parts[3] = score (integer)
     *   parts[4] = damage taken (integer)
     *   parts[5] = collectables (integer)
     *   parts[6] = completion time (format: hhh:mm:ss)
     * 
     * Exceptions thrown:
     *   IllegalArgumentException: if parts array length is incorrect, if score/damage/collectables
     *                             are not valid integers, if time format is invalid (hhh:mm:ss), or if continent
     *                             is not a valid enum value
     * 
     * @param parts the parsed command array
     * @throws IllegalArgumentException if command syntax is invalid
     */
    private void handleSingleRecordSubmit(String[] parts) throws IllegalArgumentException {
        if (parts.length != 7) {
            throw new IllegalArgumentException(
                "Submit command requires: submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME");
        }

        String name = parts[1];
        String continentStr = parts[2].toUpperCase();
        int score;
        int damageT;
        int collectables;
        String completionTime = parts[6];

        try {
            score = Integer.parseInt(parts[3]);
            damageT = Integer.parseInt(parts[4]);
            collectables = Integer.parseInt(parts[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Score, damage taken, and collectables must be valid integers!");
        }

        if (!isValidTimeFormat(completionTime)) {
            throw new IllegalArgumentException(
                "Completion time must be in format hhh:mm:ss!");
        }

        GameRecord.Continent continent;
        try {
            continent = GameRecord.Continent.valueOf(continentStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid continent: " + parts[2] +
                ". Valid options: AFRICA, ASIA, ANTARCTICA, AUSTRALIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA");
        }

        GameRecord record = new GameRecord(name, continent, score, damageT, collectables, completionTime);
        backend.addRecord(record);
        System.out.println("Successfully added record for " + name);
    }

    private void handleCollectablesCommand(String[] parts) throws IllegalArgumentException {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Collectables command requires at least one argument");
        }

        if (parts.length == 2) {
            int max;
            try {
                max = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Collectables value must be a valid integer");
            }
            currentCollectablesLow = null;
            currentCollectablesHigh = max;
            backend.getAndSetRange(currentCollectablesLow, currentCollectablesHigh);
            System.out.println("Collectables filter set to maximum: " + max);
        } else if (parts.length == 4 && parts[2].equalsIgnoreCase("to")) {
            int min, max;
            try {
                min = Integer.parseInt(parts[1]);
                max = Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Collectables values must be valid integers");
            }
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

    private void handleLocationCommand(String[] parts) throws IllegalArgumentException {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Location command requires exactly one continent argument");
        }

        String continentStr = parts[1].toUpperCase();
        GameRecord.Continent continent;
        try {
            continent = GameRecord.Continent.valueOf(continentStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid continent: " + parts[1] +
                ". Valid options: AFRICA, ASIA, ANTARCTICA, AUSTRALIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA");
        }

        currentFilter = continent;
        backend.applyAndSetFilter(currentFilter);
        System.out.println("Location filter set to: " + continentStr);
    }

    private void handleShowCommand(String[] parts) throws IllegalArgumentException {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Show command requires at least one argument");
        }

        if (parts[1].equalsIgnoreCase("fastest")) {
            if (parts.length != 3 || !parts[2].equalsIgnoreCase("times")) {
                throw new IllegalArgumentException("Invalid show command. Use 'show fastest times'");
            }
            displayTopTenRecords();
        } else {
            if (parts.length != 2) {
                throw new IllegalArgumentException("Show command with count requires exactly one argument");
            }
            int maxCount;
            try {
                maxCount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Max count must be a valid integer");
            }
            if (maxCount < 0) {
                throw new IllegalArgumentException("Max count cannot be negative");
            }
            displayRecordsWithLimit(maxCount);
        }
    }

    private void displayRecordsWithLimit(int maxCount) {
        // Retrieve records based on the range filter previously set via handleCollectablesCommand
        // and location filter set via handleLocationCommand
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

    private void displayTopTenRecords() {
        List<String> topTen = backend.getTopTen();
        if (topTen.isEmpty()) {
            System.out.println("No records found matching the current filters.");
            return;
        }

        System.out.println("Top " + topTen.size() + " fastest records:");
        int index = 1;
        for (String record : topTen) {
            System.out.println("  " + index + ". " + record);
            index++;
        }
    }

    private boolean isValidTimeFormat(String time) {
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
