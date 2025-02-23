import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.File;
public class PlaneManagement{

    private static final int rows = 4;
    private static final int[] seats_per_row= {14, 12, 12, 14}; // Seat count in each row
    private static final int AVAILABLE = 0;
    private static final int SOLD = 1;
    private static final int[][] seats = new int[rows][]; //Using a 2D array, monitor seat availability (0 for available, 1 for sold)
    private static final Ticket[] tickets = new Ticket[52]; // Array to store all tickets sold during the session
    private static int ticketIndex = 0; //  Use the index to find the tickets array's next open slot.


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Welcome to the Plane Management application");

        // Set up each seat as available at first.
        firstnameSeats();

        // Main menu loop
        showMenu();

        int option;
        do {
            try {
                System.out.print("Please select an option: ");
                option = input.nextInt();
                switch (option) {
                    case 1:
                        buySeat(input);
                        showMenu();
                        break;
                    case 2:
                        cancelSeat(input);
                        showMenu();
                        break;
                    case 3:
                        find_first();
                        showMenu();
                        break;
                    case 4:
                        show_seating_plan();
                        showMenu();
                        break;
                    case 5:
                        printTicketsInfo();
                        showMenu();
                        break;
                    case 6:
                        searchTicket(input);
                        showMenu();
                        break;
                    case 0:
                        System.out.println("Exiting the program. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please select 0, 1, 2, 3, 4, 5, or 6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number (0, 1, 2, 3, 4, 5, or 6): ");
                input.nextLine(); // Empty the input buffer.
                option = -1; // To make the loop continue, set the option to an incorrect value.
            }
        } while (option != 0);

        input.close();
    }


    private static void showMenu() {
        char x = '*';
        String y = " ";

        // Print a line of 50 asterisks
        for (int i = 0; i < 50; i++) {
            System.out.print(x);
        }
        System.out.println();
        System.out.print(x);
        for (int i = 0; i < 18; i++) {
            System.out.print(y);
        }
        System.out.print("MENU OPTIONS");
        for (int i = 0; i < 18; i++) {
            System.out.print(y);
        }
        System.out.println(x);

        for (int i = 0; i < 50; i++) {
            System.out.print(x);
        }
        System.out.println();
        System.out.println("     1) Buy a seat");
        System.out.println("     2) Cancel a seat");
        System.out.println("     3) Find first available seat");
        System.out.println("     4) Show seating plan");
        System.out.println("     5) Print tickets information and total sales");
        System.out.println("     6) Search ticket");
        System.out.println("     0) Quit");
        // Print a line of 50 asterisks
        for (int i = 0; i < 50; i++) {
            System.out.print(x);
        }
        System.out.println();
    }

    // Method to initialize all seats as available
    private static void firstnameSeats() {
        for (int n = 0; n < rows; n++) {
            seats[n] = new int[seats_per_row[n]];
            for (int k = 0; k < seats_per_row[n]; k++) {
                seats[n][k] = AVAILABLE;
            }
        }
    }
    // Method to buy a seat
    private static void buySeat(Scanner scanner) {
        // Prompt user for row letter and seat number
        System.out.print("Enter row letter (A, B, C, or D): ");
        String inputRow = scanner.next().toUpperCase(); // Convert input to uppercase
        char rowLetter;

        // Validate row letter
        if (inputRow.length() != 1 || inputRow.charAt(0) < 'A' || inputRow.charAt(0) > 'D') {
            System.out.println("Invalid row letter. Please enter A, B, C, or D.");
            return;
        } else {
            rowLetter = inputRow.charAt(0);
        }

        System.out.print("Enter seat number: ");
        try {
            int seatNumber = scanner.nextInt();

            // Validate row and seat number
            int rowIndex = getRowIndex(rowLetter);
            if (rowIndex == -1 || seatNumber < 1 || seatNumber > seats_per_row[rowIndex]) {
                System.out.println("Invalid input. if Rows are A or D enter number between 1 to 14  and  if Rows are B or C number between 1 to 12 .");
                return;
            }

            // Check if seat is available (0 for available, 1 for sold)
            if (seats[rowIndex][seatNumber - 1] == SOLD) {
                System.out.println("Sorry, this seat is already sold. Please choose another seat.");
                return;
            }

            // Prompt user for person information
            System.out.print("Enter person's firstname: ");
            String firstname = scanner.next();
            System.out.print("Enter person's surname: ");
            String surname = scanner.next();
            System.out.print("Enter person's email: ");
            String email = scanner.next();

            // Create a new Person object
            Person person = new Person(firstname, surname, email);

            // Calculate ticket price based on row
            double price;
            if (seatNumber <= 5) {
                price = 200;
            } else if (seatNumber <= 9) {
                price = 150;
            } else {
                price = 180;
            }

            // Create a new Ticket object
            Ticket ticket = new Ticket(rowIndex, seatNumber, price, person);

            // Mark seat as sold (1)
            seats[rowIndex][seatNumber - 1] = SOLD;
            System.out.println("Seat " + rowLetter + seatNumber + " has been successfully sold.");

            // Add the ticket to the tickets array
            tickets[ticketIndex++] = ticket;

            // Print the person information
            System.out.println("Person Information:");
            System.out.println("FirstName: " + firstname);
            System.out.println("Surname: " + surname);
            System.out.println("Email: " + email);
            ticket.save();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Seat number must be a number.");
            scanner.nextLine(); // Clear the input buffer
        }
    }


    // Method to cancel a seat
    private static void cancelSeat(Scanner scanner) {
        // Prompt user for row letter and seat number
        System.out.print("Enter row letter (A, B, C, or D): ");
        String inputRow = scanner.next().toUpperCase(); // Convert input to uppercase
        char rowLetter;

        // Validate row letter
        if (inputRow.length() != 1 || inputRow.charAt(0) < 'A' || inputRow.charAt(0) > 'D') {
            System.out.println("Invalid row letter. Please enter A, B, C, or D.");
            return;
        } else {
            rowLetter = inputRow.charAt(0);
        }

        System.out.print("Enter seat number: ");
        try {
            int seatNumber = scanner.nextInt();

            // Validate row and seat number
            int rowIndex = getRowIndex(rowLetter);
            if (rowIndex == -1 || seatNumber < 1 || seatNumber > seats_per_row[rowIndex]) {
                System.out.println("Invalid input. if Rows are A or D enter number between 1 to 14  and  if Rows are B or C number between 1 to 12 .");
                return;
            }

            // Check if seat was previously bought
            if (seats[rowIndex][seatNumber - 1] == AVAILABLE) {
                System.out.println("This seat has not been booked yet. There is nothing to cancel.");
                return;
            }
            // delete file after cancel a seat
            delete_file(String.valueOf(rowLetter), seatNumber);

            // Mark seat as available (0)
            seats[rowIndex][seatNumber - 1] = AVAILABLE;
            System.out.println("Seat " + rowLetter + seatNumber + " has been successfully cancelled and made available.");
            System.out.println("seat  "+rowLetter+seatNumber+" .text file has been deleted. ");

            // Remove the canceled ticket from the tickets array
            cancelTicket(rowIndex, seatNumber);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Seat number must be a number.");
            scanner.nextLine(); // Clear the input buffer
        }
    }
    //delete a text file after cancel a seat
    public static void delete_file(String row, int seatNum) {
        File file = new File(row + seatNum + ".txt");
        if (file.exists()) {
            file.delete();

        }
    }
    //remove a ticket from the collection of tickets.
    private static void cancelTicket(int rowIndex, int seatNumber) {
        for (int n = 0; n < ticketIndex; n++) {
            Ticket ticket = tickets[n];
            if (ticket.getRow() == rowIndex && ticket.getSeat() == seatNumber) {
                //Determine the refund amount by using the ticket's purchase price.
                double refundedAmount = ticket.getPrice();
                System.out.println("Refunded amount: £" + refundedAmount);

                // Tickets in the array should be shifted to make up the space left by the removed ticket.
                for (int k = n; k < ticketIndex - 1; k++) {
                    tickets[k] = tickets[k + 1];
                }
                tickets[--ticketIndex] = null; // Lower ticketIndex by one and give the last element null.
                return; // Exit the loop after removing the ticket
            }
        }
    }



    // Method to print tickets information and total sales
    private static void printTicketsInfo() {
        double totalSales = 0;

        System.out.println("Tickets Information:");
        for (int i = 0; i < ticketIndex; i++) {
            Ticket ticket = tickets[i];
            System.out.println("Ticket for seat " + (char) ('A' + ticket.getRow()) + ticket.getSeat() +
                    " sold for £" + ticket.getPrice() + " to " + ticket.getPerson().getName()+ " " + ticket.getPerson().getSurname());
            totalSales += ticket.getPrice();
        }
        System.out.println("Total sales: £" + totalSales);
    }

    private static void searchTicket(Scanner scanner) {
        System.out.print("Enter row letter (A, B, C, or D): ");
        String inputRow = scanner.next().toUpperCase(); // Convert input to uppercase
        char rowLetter;

        // Validate row letter
        if (inputRow.length() != 1 || inputRow.charAt(0) < 'A' || inputRow.charAt(0) > 'D') {
            System.out.println("Invalid row letter. Please enter A, B, C, or D.");
            return;
        } else {
            rowLetter = inputRow.charAt(0);
        }

        System.out.print("Enter seat number: ");
        try {
            int seatNumber = scanner.nextInt();

            // Validate row and seat number
            int rowIndex = getRowIndex(rowLetter);
            if (rowIndex == -1 || seatNumber < 1 || seatNumber > seats_per_row[rowIndex]) {
                System.out.println("Invalid input. if Rows are A or D enter number between 1 to 14  and  if Rows are B or C number between 1 to 12 .");
                return;
            }

            boolean seatFound = false;
            for (Ticket ticket : tickets) {
                if (ticket != null && rowLetter == getRowLetter(ticket.getRow()) && seatNumber == ticket.getSeat()) {
                    System.out.println("Buyer name: " + ticket.getPerson().getName()+" "+ticket.getPerson().getSurname());
                    System.out.println("Ticket: " + inputRow + seatNumber);
                    seatFound = true;
                    break;
                }
            }

            if (!seatFound) {
                System.out.println("The Seat is Available");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Seat number must be a number.");
            scanner.nextLine(); // Clear the input buffer
        }
    }



    // Helper method to get the index of the row
    private static int getRowIndex(char rowLetter) {
        return switch (rowLetter) {
            case 'A' -> 0;
            case 'B' -> 1;
            case 'C' -> 2;
            case 'D' -> 3;
            default -> -1; // Invalid row letter
        };
    }

    private static void find_first() {
        boolean seat_available = false;
        for (int row = 0; row < rows; row++) {
            for (int seat = 0; seat < seats_per_row[row]; seat++) {
                if (seats[row][seat] == AVAILABLE) {
                    char rowChar = getRowLetter(row);
                    System.out.println(rowChar + "" + (seat + 1));
                    seat_available = true;
                    break;
                }
            }
            if (seat_available) {
                break;
            }
        }
        if (!seat_available) {
            System.out.println("No Available Seats Found");
        }
    }

    // Helper method to get the row letter based on its index
    private static char getRowLetter(int rowIndex) {
        return switch (rowIndex) {
            case 0 -> 'A';
            case 1 -> 'B';
            case 2 -> 'C';
            case 3 -> 'D';
            default -> '?'; // Invalid row index
        };
    }

    private static void show_seating_plan() {
        // Use logic to cycle through seatPlane and show the arrangement with reserved and available seat symbols.
        for (int row = 0; row < rows; row++) {
            for (int seat = 0; seat < seats_per_row[row]; seat++) {
                if (seats[row][seat] == AVAILABLE) {
                    System.out.print("O ");  // 0 for available seat
                } else {
                    System.out.print("X ");  // X for booked seat
                }
            }
            System.out.println(); // Move to the next row after printing all seats in the current row
        }
    }
}